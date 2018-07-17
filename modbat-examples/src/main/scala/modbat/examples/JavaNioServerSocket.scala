package modbat.examples

import modbat.dsl._
//import gov.nasa.jpf.util.test.TestJPF
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.ClosedByInterruptException
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

class JavaNioServerSocket extends Model {
  var ch: ServerSocketChannel = null
  var connection: SocketChannel = null
  var client: TestClient = null
  var port: Int = 0

  class TestClient extends Thread {
    override def run() {
      try {
//	Thread.sleep(50)
	val connection = SocketChannel.open()
	connection.connect(new InetSocketAddress("localhost", port))
	val buf = ByteBuffer.allocate(2)
	buf.asCharBuffer().put("\n")
	connection.write(buf)
	connection.close()
      } catch {
        case e: ClosedByInterruptException => {
	  if (connection != null) {
            connection.socket().close()
	  }
	}
      }
    }
  }

  def toggleBlocking(ch: ServerSocketChannel) {
    ch.configureBlocking(!ch.isBlocking())
  }

  @After def cleanup() {
    if (connection != null) {
      connection.close()
      connection = null
    }
    if (ch != null) {
      ch.close()
      ch = null
    }
    if (client != null) {
      client.interrupt()
      client = null
    }
  }

  def readFrom(ch: SocketChannel) {
    val buf = ByteBuffer.allocate(1)
    assert(ch.read(buf) != -1)
  }

  def startClient {
    assert(client == null)
    client = new TestClient()
//    if (!TestJPF.isJPFRun()) {
      client.run()
//    }
  }

  // transitions
  "reset" -> "open" := {
    ch = ServerSocketChannel.open()
  }
  "open" -> "open" := {
    toggleBlocking(ch)
  }
  "open" -> "bound" := {
    ch.socket().bind(new InetSocketAddress("localhost", 0))
    port = ch.socket().getLocalPort()
  }
  "bound" -> "bound" := {
    toggleBlocking(ch)
  }
  "open" -> "err" := {
    connection = ch.accept()
  } throws ("NotYetBoundException")
  "bound" -> "connected" := {
    require(ch.isBlocking())
    startClient
    connection = ch.accept()
  }
  "bound" -> "accepting" := {
    require(!ch.isBlocking())
    startClient
  }
  "accepting" -> "accepting" := {
    assert(client != null)
    connection = null
    maybe (connection = ch.accept())
  } nextIf { () => connection != null} -> "connected"
  "connected" -> "connected" := {
    readFrom(connection)
  }
  "connected" -> "bound" := {
    connection.close()
    client = null
  }
  "accepting" -> "bound" := {
    client.interrupt()
    client = null
  }
  List("open", "bound", "accepting", "closed") -> "closed" := {
    ch.close()
  }
  "closed" -> "err" := {
    connection = ch.accept()
  } throws ("ClosedChannelException")
}
