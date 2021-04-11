package uf;

import org.junit.*;
import uf.FileHandler;

import java.io.*;

public class FileHandlerTest {

  @Before
  @After
  public void writeContentToFile() throws IOException {
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(testFilePath));
    bufferedWriter.write(content);
    bufferedWriter.close();
  }

  @Test
  public void getPieceReturnsFullPiece() {
    int index = 9;
    int length = 9;
    FileHandler fileHandler = new FileHandler(testFilePath);
    byte[] piece = fileHandler.get(index, length);
    String expected = "123456789";
    Assert.assertEquals(new String(piece), expected);
  }

  @Test
  public void getPieceReturnsPartialPiece() {
    int index = 50;
    int length = 9;
    FileHandler fileHandler = new FileHandler(testFilePath);
    byte[] piece = fileHandler.get(index, length);
    String expected = "56789";
    Assert.assertEquals(new String(piece), expected);
  }

  @Test
  public void getPieceIndexAfterEOF() {
    int index = 150;
    int length = 9;
    FileHandler fileHandler = new FileHandler(testFilePath);
    byte[] piece = fileHandler.get(index, length);
    Assert.assertNull(piece);
  }

  @Test
  public void setPiece() throws IOException {
    int index = 150;
    FileHandler fileHandler = new FileHandler(testFilePath);
    String s = "set this string in file";
    int length = s.length();

    // Act
    fileHandler.set(s.getBytes(), index);
    byte[] current = fileHandler.get(index, length);

    // Assert
    Assert.assertEquals(new String(current), s);
  }

  // UPDATE BYTESIZE ACCORDINGLY
  @Test
  public void fileTransfer() throws FileNotFoundException {
    clearFile(destFile);
    int bytesSize = 20972523;
    int pieceSize = 200;
    int numPieces = bytesSize / pieceSize + (bytesSize % pieceSize == 0 ? 0 : 1);
    FileHandler srcHandler = new FileHandler(srcFile);
    FileHandler destHandler = new FileHandler(destFile);

    for (int i = 0; i < numPieces; i++) {
      byte[] data = srcHandler.get(i*pieceSize, pieceSize);
      destHandler.set(data, i*pieceSize);
    }
    srcHandler.close();
    destHandler.close();
  }

  public void clearFile(final String file) throws FileNotFoundException {
    new PrintWriter(file).close();
  }

  static final String srcFile = "src/test/resources/srcFile.txt";
  static final String destFile = "src/test/resources/destFile.txt";
  final String content = "123456789123456789123456789\n123456789123456789123456789";
  final String testFilePath = "src/test/resources/FileHandlerTestData.txt";
}
