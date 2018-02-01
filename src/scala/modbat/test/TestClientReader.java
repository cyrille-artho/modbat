package modbat.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

class TestClientReader {
  public static final void main(String args[]) throws IOException {
    SocketChannel ch = SocketChannel.open();
    //ch.connect(new InetSocketAddress("localhost", 8888));
    InetSocketAddress addr = 
      new InetSocketAddress(InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }), 8888);
    ch.connect(addr);
    ch.configureBlocking(true);

    boolean closed = false;
    Socket connection = ch.socket();
    ByteBuffer buf;
    buf = ByteBuffer.allocate(1);
    int count = 0;
    int res;
    
    while ((res = ch.read(buf)) != -1) {
      System.err.println("Byte " + count + " = " + buf.get(0));
      assert (res == 1) : "Expected to read one byte, got " + res + " bytes.";
      count++;
      buf = ByteBuffer.allocate(1);
    }
    assert (count == 2) : "Expected two bytes in total, got " + count + " bytes.";
    assert (res == -1) : "Expected EOF, got value " + res + ".";
    System.err.println("res = " + res + ", count = " + count);
    ch.close();
  }
}
