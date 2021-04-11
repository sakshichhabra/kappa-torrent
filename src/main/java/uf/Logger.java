package uf;

import uf.BitField;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Logger {
  public Logger(final String logFilePath) {
    try {
      printWriter = new PrintWriter(logFilePath);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public synchronized void connection(final int peer1, final int peer2) throws IOException {
    printWriter.printf(
        "%s : uf.Peer %s makes a connection to uf.Peer %s.\n", LocalTime.now(), peer1, peer2);
  }

  public synchronized void connected(final int peer1, final int peer2) {
    printWriter.printf(
        "%s : uf.Peer %s is connected from uf.Peer %s.\n", LocalTime.now(), peer1, peer2);
  }

  public synchronized void neighborsChange(final int peer1, final ArrayList<Integer> neighborList) {
    printWriter.printf("%s : uf.Peer %s has the preferred neighbors ", LocalTime.now(), peer1);
    neighborList.forEach(
        neighbor -> {
          printWriter.printf(", %s", neighbor);
        });
    printWriter.printf(".\n");
  }

  public synchronized void optimisticallyUnchoking(final int peer1, final int neighbor) {
    printWriter.printf(
        "%s : uf.Peer %s has the optimistically unchoked neighbor %s.\n",
        LocalTime.now(), peer1, neighbor);
  }

  public synchronized void request(final int peer1, final int request) {
    printWriter.printf("%s : uf.Peer %s requested %s.\n", LocalTime.now(), peer1, request);
  }

  public synchronized void sentRequest(final int peer1, final int request) {
    printWriter.printf("%s : requesting %s from %s.\n", LocalTime.now(), request, peer1);
  }

  public synchronized void unchoke(final int peer1, final int neighbor) {
    printWriter.printf("%s : uf.Peer %s is unchoked by %s.\n", LocalTime.now(), peer1, neighbor);
  }

  public synchronized void choke(final int peer1, final int neighbor) {
    printWriter.printf("%s : uf.Peer %s is choked by %s.\n", LocalTime.now(), peer1, neighbor);
  }

  public synchronized void sentUnchoke(final int peer1, final int neighbor) {
    printWriter.printf("%s : uf.Peer %s sent unchoke to %s.\n", LocalTime.now(), peer1, neighbor);
  }

  public synchronized void sentChoke(final int peer1, final int neighbor) {
    printWriter.printf("%s : uf.Peer %s sent choke to %s.\n", LocalTime.now(), peer1, neighbor);
  }

  public synchronized void have(final int peer1, final int peer2, final int pieceIndex) {
    printWriter.printf(
        "%s : uf.Peer %s received the ‘have’ message from %s for the piece %s.\n",
        LocalTime.now(), peer1, peer2, pieceIndex);
  }

  public synchronized void sentInterested(final int peer1, final int peer2) {
    printWriter.printf(
        "%s : uf.Peer %s send ‘interested’ message to %s.\n", LocalTime.now(), peer1, peer2);
  }

  public synchronized void sentNotInterested(final int peer1, final int peer2) {
    printWriter.printf(
        "%s : uf.Peer %s sent ‘not interested’ message to %s.\n", LocalTime.now(), peer1, peer2);
  }

  public synchronized void interested(final int peer1, final int peer2) {
    printWriter.printf(
        "%s : uf.Peer %s received the ‘interested’ message from %s.\n",
        LocalTime.now(), peer1, peer2);
  }

  public synchronized void notInterested(final int peer1, final int peer2) {
    printWriter.printf(
        "%s : uf.Peer %s received the ‘not interested’ message from %s.\n",
        LocalTime.now(), peer1, peer2);
  }

  public synchronized void sentPiece(final int neigh, final int pieceIndex) {
    printWriter.printf("%s : sent piece %s to %s \n", LocalTime.now(), pieceIndex, neigh);
  }

  public synchronized void download(
      final int peer1, final int peer2, final int pieceIndex, final int numPieces) {
    printWriter.printf(
        "%s : uf.Peer %s has downloaded the piece %s from %s. Now the number of pieces it has is %s.\n",
        LocalTime.now(), peer1, pieceIndex, peer2, numPieces);
  }

  public synchronized void complete(final int peer1) {
    printWriter.printf(
        "%s : uf.Peer %s has downloaded the complete file.\n", LocalTime.now(), peer1);
  }

  public synchronized void finishHandle(final int peer1, final int neigh) {
    printWriter.printf(
            "%s : uf.Peer %s has downloaded the complete file from %s\n", LocalTime.now(), peer1, neigh);
  }

  public synchronized void sentHave(final int peer, final int index) {
    printWriter.printf("%s : sent have to %s index: %s \n", LocalTime.now(), peer, index);
  }

  public synchronized void sendBitfield(final int neigh, final BitField bitField) {
    printWriter.printf(
        "%s : sent a bitfield to %s with file %s \n",
        LocalTime.now(), neigh, bitField.isFull() ? "FULL" : "EMPTY");
  }

  public synchronized void gotBitfield(final int neigh, final BitField bitField) {
    printWriter.printf(
        "%s : received a bitfield from %s with file %s \n",
        LocalTime.now(), neigh, bitField.isFull() ? "FULL" : "EMPTY");
  }

  public void close() {
    printWriter.close();
  }

  public void flush() {
    printWriter.flush();
  }

  PrintWriter printWriter;
}
