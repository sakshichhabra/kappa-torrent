package uf;

import org.mockito.Mockito;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class MessageIOTest {
  @Test
  public void writeHandShake() throws IOException {
    // Arrange
    DataOutputStream out = new DataOutputStream(new ByteArrayOutputStream());
    Socket socket = Mockito.mock(Socket.class);
    Mockito.when(socket.getOutputStream()).thenReturn(out);
    MessageIO io = new MessageIO(socket);

    // Act
    io.writeHandShake(1001);
    int actual = out.size();

    // Assert
    Assert.assertEquals(32, actual);
  }

  @Test
  public void readHandShake() throws IOException, HandShakeException {
    // Arrange
    Socket socket = Mockito.mock(Socket.class);
    ByteBuffer buf = ByteBuffer.allocate(32);
    buf.put("P2PFILESHARINGPROJ0000000000".getBytes());
    buf.putInt(1001);
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(buf.array()));
    Mockito.when(socket.getInputStream()).thenReturn(in);
    MessageIO io = new MessageIO(socket);

    // Act
    int actual = io.readHandShake();

    // Assert
    Assert.assertEquals(1001, actual);
  }

  @Test(expected = HandShakeException.class)
  public void readHandShakeNotEnoughBytes() throws IOException, HandShakeException {
    // Arrange
    Socket socket = Mockito.mock(Socket.class);
    ByteBuffer buf = ByteBuffer.allocate(20);
    buf.put("P2PFILESHARINGPROJ00".getBytes());
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(buf.array()));
    Mockito.when(socket.getInputStream()).thenReturn(in);
    MessageIO io = new MessageIO(socket);

    // Act
    int actual = io.readHandShake();

    // Assert
    Assert.assertEquals(1001, actual);
  }

  @Test(expected = HandShakeException.class)
  public void readHandShakeWrongHeader() throws IOException, HandShakeException {
    // Arrange
    Socket socket = Mockito.mock(Socket.class);
    ByteBuffer buf = ByteBuffer.allocate(32);
    buf.put("P2PFILESHARINGPROJ00000wrong".getBytes());
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(buf.array()));
    Mockito.when(socket.getInputStream()).thenReturn(in);
    MessageIO io = new MessageIO(socket);

    // Act
    int actual = io.readHandShake();

    // Assert
    Assert.assertEquals(1001, actual);
  }

  @Test
  public void readAndWriteBitField() throws AlreadyHavePieceException, IOException {
    BitField expected = BitField.from(10, false);
    expected.setHave(0);
    expected.setHave(2);
    expected.setHave(3);
    expected.setHave(5);
    expected.setHave(6);
    expected.setHave(8);
    expected.setHave(9);

    Socket socket = Mockito.mock(Socket.class);
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(bout);
    Mockito.when(socket.getOutputStream()).thenReturn(out);
    MessageIO io = new MessageIO(socket);
    io.writeBitField(expected);

    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bout.toByteArray()));
    Mockito.when(socket.getInputStream()).thenReturn(in);
    MessageIO readIo = new MessageIO(socket);

    int lengthBitField = readIo.readInt();
    readIo.readByte();
    byte[] inArray = readIo.readBitField(lengthBitField);
    BitField actual = BitField.from(10, false);
    actual.setBitField(inArray);
    for (int i = 0; i < 10; i++) {
      Assert.assertEquals(actual.getStatus(i), actual.getStatus(i));
    }
//    uf.BitField have = uf.BitField.from(10, true);
//    byte[] b = have.toByteArray();
//    System.out.println(Arrays.toString(b));
//    uf.BitField a = uf.BitField.from(10, false);
//    a.setBitField(b);
//    System.out.println(Arrays.toString(a.toByteArray()));
  }

  @Test
  public void readAndWriteHave() throws IOException {
    Socket socket = Mockito.mock(Socket.class);
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(bout);
    Mockito.when(socket.getOutputStream()).thenReturn(out);
    MessageIO io = new MessageIO(socket);
    io.writeHave(15);

    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bout.toByteArray()));
    Mockito.when(socket.getInputStream()).thenReturn(in);
    MessageIO readIO = new MessageIO(socket);
    readIO.readInt();
    readIO.readByte();
    int have = readIO.readHave();
    Assert.assertEquals(15, have);
  }

  @Test
  public void readAndWriteRequest() throws IOException {
    Socket socket = Mockito.mock(Socket.class);

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(bout);
    Mockito.when(socket.getOutputStream()).thenReturn(out);
    MessageIO io = new MessageIO(socket);
    io.writeRequest(15);


    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bout.toByteArray()));
    Mockito.when(socket.getInputStream()).thenReturn(in);
    MessageIO readIO = new MessageIO(socket);
    readIO.readInt();
    readIO.readByte();
    int have = readIO.readRequest();
    Assert.assertEquals(15, have);
  }

  @Test
  public void readAndWritePiece() throws IOException {
    Socket socket = Mockito.mock(Socket.class);

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(bout);
    Mockito.when(socket.getOutputStream()).thenReturn(out);
    MessageIO io = new MessageIO(socket);
    byte[] piece = new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    io.writePiece(new Piece(20, piece));

    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bout.toByteArray()));
    Mockito.when(socket.getInputStream()).thenReturn(in);
    MessageIO readIO = new MessageIO(socket);
    int length = readIO.readInt();
    readIO.readByte();
    Piece actual = readIO.readPiece(length);
    Assert.assertArrayEquals(piece, actual.bytes);
  }

  @Test
  public void readAndWriteNoBodyMessages() throws IOException {
    Socket socket = Mockito.mock(Socket.class);
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(bout);
    Mockito.when(socket.getOutputStream()).thenReturn(out);
    MessageIO io = new MessageIO(socket);
    io.writeChoke();
    io.writeUnChoke();
    io.writeInterested();
    io.writeNotInterested();
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bout.toByteArray()));
    Mockito.when(socket.getInputStream()).thenReturn(in);
    MessageIO readIO = new MessageIO(socket);
    int chokeLength = readIO.readInt();
    byte actualChoke = readIO.readByte();
    int unchokeLength = readIO.readInt();
    byte actualUnChoke = readIO.readByte();
    int interestedLength = readIO.readInt();
    byte actualInterested = readIO.readByte();
    int notInterestedLength = readIO.readInt();
    byte actualNotInterested = readIO.readByte();
    Assert.assertEquals(0, chokeLength);
    Assert.assertEquals(0, unchokeLength);
    Assert.assertEquals(0, interestedLength);
    Assert.assertEquals(0, notInterestedLength);
    Assert.assertEquals(MessageIO.CHOKE, actualChoke);
    Assert.assertEquals(MessageIO.UNCHOKE, actualUnChoke);
    Assert.assertEquals(MessageIO.INTERESTED, actualInterested);
    Assert.assertEquals(MessageIO.NOT_INTERESTED, actualNotInterested);
  }
}
