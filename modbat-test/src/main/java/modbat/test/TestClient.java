package modbat.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;

class TestClient {
  public final static void main(String[] args) throws IOException {
    SocketChannel connection = null;
    try {
      connection = SocketChannel.open();
      connection.connect(new InetSocketAddress("localhost", 8888));
      ByteBuffer buf = ByteBuffer.allocate(2);
      buf.asCharBuffer().put("\n");
      connection.write(buf);
      connection.close();
    } catch (ClosedByInterruptException e) {
      if (connection != null) {
	connection.socket().close();
      }
    }
  }
}
