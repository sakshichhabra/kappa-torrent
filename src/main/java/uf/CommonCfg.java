package uf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CommonCfg {
  public int NumberOfPreferredNeighbors;
  public int UnchokingInterval;
  public int OptimisticUnchokingInterval;
  public int FileSize;
  public int PieceSize;
  public String FileName;

  private CommonCfg(final String CfgFilePath) {
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(CfgFilePath));

      String[] lineSplit;

      String NumberOfPreferredNeighborsLine = bufferedReader.readLine();
      lineSplit = NumberOfPreferredNeighborsLine.split("\\s+");
      NumberOfPreferredNeighbors = Integer.parseInt(lineSplit[1]);

      String UnchokingIntervalLine = bufferedReader.readLine();
      lineSplit = UnchokingIntervalLine.split("\\s+");
      UnchokingInterval = Integer.parseInt(lineSplit[1]);

      String OptimisticUnchokingIntervalLine = bufferedReader.readLine();
      lineSplit = OptimisticUnchokingIntervalLine.split("\\s+");
      OptimisticUnchokingInterval = Integer.parseInt(lineSplit[1]);

      String FileNameLine = bufferedReader.readLine();
      lineSplit = FileNameLine.split("\\s+");
      FileName = lineSplit[1];

      String FileSizeLine = bufferedReader.readLine();
      lineSplit = FileSizeLine.split("\\s+");
      FileSize = Integer.parseInt(lineSplit[1]);

      String PieceSizeLine = bufferedReader.readLine();
      lineSplit = PieceSizeLine.split("\\s+");
      PieceSize = Integer.parseInt(lineSplit[1]);

    } catch (IOException e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

  private CommonCfg(
      final int numberOfPreferredNeighbors,
      final int unchokingInterval,
      final int optimisticUnchokingInterval,
      final int fileSize,
      final int pieceSize,
      final String fileName) {
    NumberOfPreferredNeighbors = numberOfPreferredNeighbors;
    UnchokingInterval = unchokingInterval;
    OptimisticUnchokingInterval = optimisticUnchokingInterval;
    FileSize = fileSize;
    PieceSize = pieceSize;
    FileName = fileName;
  }

  public static CommonCfg from(final String CfgFilePath) {
    return new CommonCfg(CfgFilePath);
  }

  public static CommonCfg from(
      final int numberOfPreferredNeighbors,
      final int unchokingInterval,
      final int optimisticUnchokingInterval,
      final int fileSize,
      final int pieceSize,
      final String fileName) {
    return new CommonCfg(
        numberOfPreferredNeighbors,
        unchokingInterval,
        optimisticUnchokingInterval,
        fileSize,
        pieceSize,
        fileName);
  }
}
