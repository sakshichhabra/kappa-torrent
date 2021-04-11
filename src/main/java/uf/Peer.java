package uf;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Peer {
  public Peer(
      final int peerID,
      final CommonCfg commonCfg,
      final PeersInfo peersInfo,
      final String logPath) {
    this.peerID = peerID;
    this.commonCfg = commonCfg;
    this.peersInfo = peersInfo;
    this.logger = new Logger(logPath);
    this.threadPool = new ArrayList<>();
  }

  public void run() {
    // get my peer info
    Optional<PeerInfo> currentPeerInfo = peersInfo.get(peerID);
    if (!currentPeerInfo.isPresent()) {
      throw new IllegalArgumentException("peerID is invalid");
    }
    // Data
    ArrayList<PeerInfo> servers = peersInfo.before(peerID);
    ArrayList<PeerInfo> after = peersInfo.after(peerID);

    // My server
    Acceptor acceptor = new Acceptor(currentPeerInfo.get().port, after.size(), serverIOHandlers);
    Thread acceptThread = new Thread(acceptor);
    acceptThread.start();

    // Connect to clients
    makeConnections(servers);

    // Finish server accepting
    try {
      acceptThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    IOHandlers.addAll(serverIOHandlers);

    performHandshake(currentPeerInfo.get(), after);

    state = PeerState.from(peerID, commonCfg, peersInfo, IOHandlersMap);

    sendBitField();

    FileHandler fileHandler = new FileHandler(commonCfg.FileName);
    for (Map.Entry<Integer, MessageIO> set : IOHandlersMap.entrySet()) {
      try {
        PeerHandler peerHandler = new PeerHandler(state, set.getKey(), fileHandler, logger);
        Thread thread = new Thread(peerHandler);
        thread.setName("p" + peerID + "h" + set.getKey());
        threadPool.add(thread);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    Timer preferredNeighbourTimer = new Timer();
    Timer optimisticNeighbourTimer = new Timer();

    preferredNeighbourTimer.scheduleAtFixedRate(
        new PreferredNeighborSelectionTimerTask(state, logger),
        0,
        commonCfg.UnchokingInterval * 1000);
    optimisticNeighbourTimer.scheduleAtFixedRate(
        new OptimisticNeighborSelectionTimerTask(state, logger),
        0,
        commonCfg.OptimisticUnchokingInterval * 1000);

    for (Thread thread : threadPool) {
      thread.start();
    }
    for (Thread thread : threadPool) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    preferredNeighbourTimer.cancel();
    optimisticNeighbourTimer.cancel();
    for (Map.Entry<Integer, MessageIO> set : IOHandlersMap.entrySet()) {
      try {
        set.getValue().close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    logger.close();
  }

  // Make this private when not testing
  public Socket connect(final String ipAddress, final int port) throws IOException {
    return new Socket(ipAddress, port);
  }

  private void makeConnections(final ArrayList<PeerInfo> servers) {
    for (PeerInfo peerInfo : servers) {
      try {
        Socket clientSocket = connect("localhost", peerInfo.port);
        IOHandlers.add(new MessageIO(clientSocket));
        logger.connection(peerID, peerInfo.peerID);
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println(peerID + " Cant connect to " + peerInfo.peerID);
        System.out.println(e.getMessage());
        return;
      }
    }
  }

  private void performHandshake(final PeerInfo currentPeerInfo, final ArrayList<PeerInfo> after) {
    // Send handshake
    for (MessageIO io : IOHandlers) {
      try {
        io.writeHandShake(currentPeerInfo.peerID);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // Read handshake and set map<peer,socket>
    for (MessageIO io : IOHandlers) {
      try {
        int receivedPeerID = io.readHandShake();
        IOHandlersMap.put(receivedPeerID, io);
        Optional<PeerInfo> clientPeer =
            after.stream().filter(peerInfo -> peerInfo.peerID == receivedPeerID).findFirst();
        if (clientPeer.isPresent()) {
          logger.connected(currentPeerInfo.peerID, receivedPeerID);
        }
      } catch (IOException | HandShakeException e) {
        e.printStackTrace();
      }
    }
  }

  private void sendBitField() {
    for (Map.Entry<Integer, MessageIO> set : IOHandlersMap.entrySet()) {
      try {
        BitField myBitField = state.getBitFieldOfPeer(peerID);
        set.getValue().writeBitField(myBitField);
        logger.sendBitfield(set.getKey(), myBitField);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private final CommonCfg commonCfg;
  private final Logger logger;
  private final PeersInfo peersInfo;

  public int getPeerID() {
    return peerID;
  }

  private final int peerID;
  private final ArrayList<Thread> threadPool;
  final ArrayList<MessageIO> serverIOHandlers = new ArrayList<>();
  final ArrayList<MessageIO> IOHandlers = new ArrayList<>();
  HashMap<Integer, MessageIO> IOHandlersMap = new HashMap<>();
  PeerState state;
}

class Acceptor implements Runnable {
  Acceptor(int port, int expectedConnections, ArrayList<MessageIO> ioHandlers) {
    this.port = port;
    this.expectedConnections = expectedConnections;
    this.ioHandlers = ioHandlers;
  }

  @Override
  public void run() {
    try {
      ServerSocket serverSocket = new ServerSocket(port, 200);
      while (expectedConnections-- > 0) {
        Socket clientSocket = serverSocket.accept();
        ioHandlers.add(new MessageIO(clientSocket));
      }
      serverSocket.close();
    } catch (IOException e) {
      System.out.println("cant close server");
      e.printStackTrace();
    }
  }

  final int port;
  int expectedConnections;
  final ArrayList<MessageIO> ioHandlers;
}

class PreferredNeighborSelectionTimerTask extends TimerTask {
  public PreferredNeighborSelectionTimerTask(final PeerState state, Logger logger) {
    this.state = state;
    this.logger = logger;
  }

  @Override
  public void run() {
    state.updatePreferredNeighbors();
    for (PeerInfo peerInfo : state.getPeersInfo()) {
      if (peerInfo.peerID == state.peerID) continue;
      MessageIO io = state.getIOHandlerPeer(peerInfo.peerID);
      try {
        if (state.neighbourChokeStatus.get(peerInfo.peerID) == PeerState.ChokeStatus.CHOKED) {
          io.writeChoke();
          logger.sentChoke(state.peerID, peerInfo.peerID);
        } else {
          io.writeUnChoke();
          logger.sentUnchoke(state.peerID, peerInfo.peerID);
        }
      } catch (IOException e) {
        break;
      }
    }
  }

  final PeerState state;
  final Logger logger;
}

class OptimisticNeighborSelectionTimerTask extends TimerTask {
  OptimisticNeighborSelectionTimerTask(PeerState state, Logger logger) {
    this.state = state;
    this.logger = logger;
  }

  @Override
  public void run() {
    Optional<Integer> opUnchokedPeer = state.updateOptimisticNeighbor();
    if (opUnchokedPeer.isPresent()) {
      MessageIO io = state.getIOHandlerPeer(opUnchokedPeer.get());
      try {
        io.writeUnChoke();
        logger.optimisticallyUnchoking(state.peerID, opUnchokedPeer.get());
      } catch (IOException ignored) {
      }
    }
  }

  final PeerState state;
  final Logger logger;
}
