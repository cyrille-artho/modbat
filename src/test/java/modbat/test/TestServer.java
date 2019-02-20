package modbat.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

class TestServer {
  public static final void main(String args[]) throws IOException {
    ServerSocketChannel ch = ServerSocketChannel.open();
    InetSocketAddress addr = new InetSocketAddress("localhost", 8888);
    ch.socket().bind(addr);
    System.out.println("Listening on address " + addr + "...");
    ch.configureBlocking(true);

    boolean closed = false;
    SocketChannel connection = null;
    while (!closed) {
      try {
	connection = ch.accept();
	System.out.println("Accepted connection");
	ByteBuffer buf = ByteBuffer.allocate(2);
	buf.asCharBuffer().put("\n");
	connection.write(buf);
	connection.socket().close();
      } catch (ClosedByInterruptException e) {
	if (connection != null) {
	  connection.socket().close();
	}
	closed = true;
      }
    }
    ch.close();
  }
}
