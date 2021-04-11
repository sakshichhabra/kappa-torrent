package uf;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class PeerTest {

  @Before
  public void setup() throws IOException {}

  @Test
  public void runStartsThreads() throws InterruptedException, IOException {
    ArrayList<Thread> threads = new ArrayList<>();
    for (Peer peer : peers) {
      PeerRunner peerRunner = new PeerRunner(peer);
      Thread thread = new Thread(peerRunner);
      threads.add(thread);
    }
    for (Thread thread : threads) {
      thread.start();
    }
    for (Thread thread : threads) {
      thread.join();
    }
    File file1 = new File(commonCfg1.FileName);
    for (int i = 1; i < peers.size(); i++) {
      File file2 = new File(peers.get(i).state.commonCfg.FileName);
      boolean areTwoFilesEqual = FileUtils.contentEquals(file1, file2);
      Assert.assertTrue(areTwoFilesEqual);
    }
  }

  @Test
  public void runTwoPeers() throws InterruptedException {
    PeerInfo peer1_info = new PeerInfo(1001, "localhost", 3000, true);
    PeerInfo peer2_info = new PeerInfo(1002, "localhost", 3001, false);
    PeerInfo peer3_info = new PeerInfo(1003, "localhost", 3002, false);
    CommonCfg commonCfg1 = CommonCfg.from(2, 5, 15, 1024, 200, Constants.testData1001);
    CommonCfg commonCfg2 = CommonCfg.from(2, 5, 15, 1024, 200, Constants.testData1002);
    CommonCfg commonCfg3 = CommonCfg.from(2, 5, 15, 1024, 200, Constants.testData1003);
    ArrayList<PeerInfo> peersInfoList = new ArrayList<>();
    peersInfoList.add(peer1_info);
    peersInfoList.add(peer2_info);
    //    peersInfoList.add(peer3_info);
    PeersInfo peersInfo = PeersInfo.from(peersInfoList);
    Peer peer1 = new Peer(1001, commonCfg1, peersInfo, Constants.testLogfile1001);
    Peer peer2 = new Peer(1002, commonCfg2, peersInfo, Constants.testLogfile1002);
    //    Peer peer3 = new Peer(1003, commonCfg3, peersInfo, Constants.testLogfile1003);
    ArrayList<Peer> twoPeers = new ArrayList<>();
    twoPeers.add(peer1);
    twoPeers.add(peer2);
    //    twoPeers.add(peer3);
    ArrayList<Thread> threads = new ArrayList<>();
    for (Peer peer : twoPeers) {
      PeerRunner peerRunner = new PeerRunner(peer);
      Thread thread = new Thread(peerRunner);
      thread.setName("Peer" + peer.getPeerID());
      threads.add(thread);
    }
    for (Thread thread : threads) {
      thread.start();
    }
    for (Thread thread : threads) {
      thread.join();
    }
  }

  @Test
  public void timerTest() throws InterruptedException {
    Timer timer = new Timer();
    int period = 1000;
    timer.schedule(
        new TimerTask() {
          @Override
          public void run() {
            System.out.println("hello");
          }
        },
        0,
        period);
    timer.cancel();
  }

  @Test
  public void ninePeersTest() throws InterruptedException {
    PeersInfo peersInfo = PeersInfo.from(Constants.PeerInfoPathNine);
    CommonCfg commonCfg1 = CommonCfg.from(3, 5, 10, 2167705, 32768, Constants.testData1001);
    CommonCfg commonCfg2 = CommonCfg.from(3, 5, 10, 2167705, 32768, Constants.testData1002);
    CommonCfg commonCfg3 = CommonCfg.from(3, 5, 10, 2167705, 32768, Constants.testData1003);
    CommonCfg commonCfg4 = CommonCfg.from(3, 5, 10, 2167705, 32768, Constants.testData1004);
    CommonCfg commonCfg5 = CommonCfg.from(3, 5, 10, 2167705, 32768, Constants.testData1005);
    CommonCfg commonCfg6 = CommonCfg.from(3, 5, 10, 2167705, 32768, Constants.testData1006);
    CommonCfg commonCfg7 = CommonCfg.from(3, 5, 10, 2167705, 32768, Constants.testData1007);
    CommonCfg commonCfg8 = CommonCfg.from(3, 5, 10, 2167705, 32768, Constants.testData1008);
    CommonCfg commonCfg9 = CommonCfg.from(3, 5, 10, 2167705, 32768, Constants.testData1009);
    Peer peer1001 = new Peer(1001, commonCfg1, peersInfo, Constants.testLogfile1001);
    Peer peer1002 = new Peer(1002, commonCfg2, peersInfo, Constants.testLogfile1002);
    Peer peer1003 = new Peer(1003, commonCfg3, peersInfo, Constants.testLogfile1003);
    Peer peer1004 = new Peer(1004, commonCfg4, peersInfo, Constants.testLogfile1004);
    Peer peer1005 = new Peer(1005, commonCfg5, peersInfo, Constants.testLogfile1005);
    Peer peer1006 = new Peer(1006, commonCfg6, peersInfo, Constants.testLogfile1006);
    Peer peer1007 = new Peer(1007, commonCfg7, peersInfo, Constants.testLogfile1007);
    Peer peer1008 = new Peer(1008, commonCfg8, peersInfo, Constants.testLogfile1008);
    Peer peer1009 = new Peer(1009, commonCfg9, peersInfo, Constants.testLogfile1009);
    ArrayList<Peer> twoPeers = new ArrayList<>();
    twoPeers.add(peer1001);
    twoPeers.add(peer1002);
    twoPeers.add(peer1003);
    twoPeers.add(peer1004);
    twoPeers.add(peer1005);
    twoPeers.add(peer1006);
    twoPeers.add(peer1007);
    twoPeers.add(peer1008);
    twoPeers.add(peer1009);
    ArrayList<Thread> threads = new ArrayList<>();
    for (Peer peer : twoPeers) {
      PeerRunner peerRunner = new PeerRunner(peer);
      Thread thread = new Thread(peerRunner);
      thread.setName("Peer" + peer.getPeerID());
      threads.add(thread);
    }
    for (Thread thread : threads) {
      thread.start();
    }
    for (Thread thread : threads) {
      thread.join();
    }
  }

  PeersInfo peersInfo = PeersInfo.from(Constants.testPeerInfoPath);
  CommonCfg commonCfg = CommonCfg.from(Constants.testCommonCfgPath);
  CommonCfg commonCfg1 = CommonCfg.from(2, 5, 15, 1024, 200, Constants.testData1001);
  CommonCfg commonCfg2 = CommonCfg.from(2, 5, 15, 1024, 200, Constants.testData1002);
  CommonCfg commonCfg3 = CommonCfg.from(2, 5, 15, 1024, 200, Constants.testData1003);
  CommonCfg commonCfg4 = CommonCfg.from(2, 5, 15, 1024, 200, Constants.testData1004);
  CommonCfg commonCfg5 = CommonCfg.from(2, 5, 15, 1024, 200, Constants.testData1005);
  CommonCfg commonCfg6 = CommonCfg.from(2, 5, 15, 1024, 200, Constants.testData1006);
  Peer peer1001 = new Peer(1001, commonCfg1, peersInfo, Constants.testLogfile1001);
  Peer peer1002 = new Peer(1002, commonCfg2, peersInfo, Constants.testLogfile1002);
  Peer peer1003 = new Peer(1003, commonCfg3, peersInfo, Constants.testLogfile1003);
  Peer peer1004 = new Peer(1004, commonCfg4, peersInfo, Constants.testLogfile1004);
  Peer peer1005 = new Peer(1005, commonCfg5, peersInfo, Constants.testLogfile1005);
  Peer peer1006 = new Peer(1006, commonCfg6, peersInfo, Constants.testLogfile1006);
  ArrayList<Peer> peers =
      new ArrayList<>(Arrays.asList(peer1001, peer1002, peer1003, peer1004, peer1005, peer1006));
}

class PeerRunner implements Runnable {

  PeerRunner(Peer peer) {
    this.peer = peer;
  }

  @Override
  public void run() {
    peer.run();
  }

  final Peer peer;
}
