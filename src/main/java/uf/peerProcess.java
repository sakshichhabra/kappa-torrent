package uf;

public class peerProcess {
  public static void main(String[] args) {
      PeersInfo peersInfo = PeersInfo.from(Constants.testPeerInfoPath);
      CommonCfg commonCfg = CommonCfg.from(Constants.testCommonCfgPath);
      Peer peer = new Peer(Integer.parseInt(args[1]), commonCfg, peersInfo, Constants.testLogfile1001);
      peer.run();
  }
}
