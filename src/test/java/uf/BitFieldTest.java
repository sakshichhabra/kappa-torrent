package uf;

import org.junit.Assert;
import org.junit.Test;

public class BitFieldTest {

  @Test
  public void requestWhenAlreadyRequested() throws AlreadyHavePieceException {
    BitField bitField = BitField.from(5, false);
    bitField.setRequested(2);
    bitField.setRequested(2);
    PieceStatus actual = bitField.getStatus(2);
    Assert.assertEquals(PieceStatus.REQUESTED, actual);
  }

  @Test
  public void requestWhenMissing() throws AlreadyHavePieceException {
    BitField bitField = BitField.from(5, false);
    bitField.setRequested(2);
    PieceStatus actual = bitField.getStatus(2);
    Assert.assertEquals(PieceStatus.REQUESTED, actual);
  }

  @Test(expected = AlreadyHavePieceException.class)
  public void requestWhenHave() throws AlreadyHavePieceException {
    BitField bitField = BitField.from(5, false);
    bitField.setHave(2);
    bitField.setRequested(2);
  }

  @Test(expected = AlreadyHavePieceException.class)
  public void haveWhenHave() throws AlreadyHavePieceException {
    BitField bitField = BitField.from(5, false);
    bitField.setHave(2);
    bitField.setHave(2);
  }

  @Test
  public void haveWhenMissing() throws AlreadyHavePieceException {
    BitField bitField = BitField.from(5, false);
    bitField.setHave(2);
    PieceStatus actual = bitField.getStatus(2);
    Assert.assertEquals(PieceStatus.HAVE, actual);
  }

  @Test
  public void haveWhenRequested() throws AlreadyHavePieceException {
    BitField bitField = BitField.from(5, false);
    bitField.setRequested(2);
    bitField.setHave(2);
    PieceStatus actual = bitField.getStatus(2);
    Assert.assertEquals(PieceStatus.HAVE, actual);
  }

  @Test
  public void initWithIsFullTrue() {
    BitField bitField = BitField.from(5, true);
    Assert.assertTrue(bitField.isFull());
  }

  @Test
  public void initWithIsFullFalse() {
    BitField bitField = BitField.from(5, false);
    Assert.assertFalse(bitField.isFull());
  }

  @Test
  public void addFillsBitField() throws AlreadyHavePieceException {
    BitField bitField = BitField.from(3, false);
    bitField.setRequested(0);
    bitField.setHave(0);
    Assert.assertFalse(bitField.isFull());
    bitField.setHave(1);
    Assert.assertFalse(bitField.isFull());
    bitField.setRequested(2);
    bitField.setHave(2);
    Assert.assertTrue(bitField.isFull());
  }

  @Test
  public void toAndFromByteArray() throws AlreadyHavePieceException {
    BitField bitField = BitField.from(10, false);
    bitField.setHave(0);
    bitField.setHave(2);
    bitField.setHave(3);
    bitField.setHave(5);
    bitField.setHave(6);
    bitField.setHave(8);
    bitField.setHave(9);
    byte[] bytes = bitField.toByteArray();
    BitField expected = BitField.from(10,false);
    expected.setBitField(bytes);
    Assert.assertEquals(PieceStatus.HAVE, expected.getStatus(0));
    Assert.assertEquals(PieceStatus.MISSING, expected.getStatus(1));
    Assert.assertEquals(PieceStatus.HAVE, expected.getStatus(2));
    Assert.assertEquals(PieceStatus.HAVE, expected.getStatus(3));
    Assert.assertEquals(PieceStatus.MISSING, expected.getStatus(4));
    Assert.assertEquals(PieceStatus.HAVE, expected.getStatus(5));
    Assert.assertEquals(PieceStatus.HAVE, expected.getStatus(6));
    Assert.assertEquals(PieceStatus.MISSING, expected.getStatus(7));
    Assert.assertEquals(PieceStatus.HAVE, expected.getStatus(8));
    Assert.assertEquals(PieceStatus.HAVE, expected.getStatus(9));
    Assert.assertFalse(expected.isFull());
    expected.setHave(1);
    Assert.assertFalse(expected.isFull());
    expected.setHave(4);
    Assert.assertFalse(expected.isFull());
    expected.setHave(7);
    Assert.assertTrue(expected.isFull());
  }
}
