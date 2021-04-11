package uf;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;

public class PeerStateTest {

  @Test
  public void setRequestedPiece() {
    PeerState state = PeerState.from(1, commonCfg, peersInfo, null);
    PieceStatus before = state.getStatusOfPiece(1002, 15);
    state.setRequestedPiece(1002, 15);
    PieceStatus after = state.getStatusOfPiece(1002, 15);
    Assert.assertEquals(PieceStatus.MISSING, before);
    Assert.assertEquals(PieceStatus.REQUESTED, after);
  }

  @Test
  public void setHavePiece() {
    PeerState state = PeerState.from(1, commonCfg, peersInfo, null);
    PieceStatus before = state.getStatusOfPiece(1002, 30);
    state.setHavePiece(1002, 30);
    PieceStatus after = state.getStatusOfPiece(1002, 30);
    Assert.assertEquals(PieceStatus.MISSING, before);
    Assert.assertEquals(PieceStatus.HAVE, after);
  }

  // This test is specific to cfg files in project description, change "peers" and
  // "NUMBER_OF_PIECES" if config is changed.
  @Test
  public void allDone() {
    PeerState state = PeerState.from(1, commonCfg, peersInfo, null);
    int NUMBER_OF_PIECES = 306;
    int[] peers = new int[] {1002, 1003, 1004, 1005, 1006};
    for (int peer : peers) {
      for (int i = 0; i < NUMBER_OF_PIECES; i++) {
        state.setHavePiece(peer, i);
      }
    }
    Assert.assertTrue(state.areAllDone());
  }

  @Test
  public void getIOHandlerOfPeer() {
    HashMap<Integer, MessageIO> map = new HashMap<>();
    MessageIO io = Mockito.mock(MessageIO.class);
    map.put(1001, io);
    PeerState state = PeerState.from(1, commonCfg, peersInfo, map);
    MessageIO actual = state.getIOHandlerPeer(1001);
    Assert.assertEquals(io, actual);
  }

  @Test
  public void updateNeighbors(){
    PeersInfo peersInfo = PeersInfo.from(Constants.testPeerInfoPath);
    CommonCfg commonCfg = CommonCfg.from(Constants.testCommonCfgPath);
    PeerState state = PeerState.from(1001,commonCfg,peersInfo,null);
    state.addInterested(1002);
    state.addInterested(1002);
    state.addInterested(1004);
    state.addInterested(1006);
    state.incrementDownloadCounter(1002);
    state.incrementDownloadCounter(1002);
    state.incrementDownloadCounter(1002);
    state.incrementDownloadCounter(1003);
    state.incrementDownloadCounter(1003);
    state.incrementDownloadCounter(1004);
    state.incrementDownloadCounter(1004);
    state.incrementDownloadCounter(1004);
    state.incrementDownloadCounter(1004);
    state.incrementDownloadCounter(1004);
    state.incrementDownloadCounter(1004);
    state.incrementDownloadCounter(1006);
    state.incrementDownloadCounter(1006);
    state.updatePreferredNeighbors();
    Assert.assertEquals(state.neighbourChokeStatus.get(1002), PeerState.ChokeStatus.UNCHOKED);
    Assert.assertEquals(state.neighbourChokeStatus.get(1003), PeerState.ChokeStatus.CHOKED);
    Assert.assertEquals(state.neighbourChokeStatus.get(1004), PeerState.ChokeStatus.UNCHOKED);
    Assert.assertEquals(state.neighbourChokeStatus.get(1006), PeerState.ChokeStatus.CHOKED);
  }

  private final CommonCfg commonCfg = CommonCfg.from(Constants.testCommonCfgPath);
  private final PeersInfo peersInfo = PeersInfo.from(Constants.testPeerInfoPath);
}
