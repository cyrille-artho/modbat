package modbat.examples

import modbat.dsl._
//import gov.nasa.jpf.util.test.TestJPF
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.ClosedByInterruptException
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

class JavaNioServerSocket2 extends Model {
  var ch: ServerSocketChannel = null
  var connection: SocketChannel = null
  var client: TestClient = null
  var count: Int = 0
  var port: Int = 0

  class TestClient extends Thread {
    override def run(): Unit = {
      try {
	val connection = SocketChannel.open()
	connection.connect(new InetSocketAddress("localhost", port))
	val buf = ByteBuffer.allocate(2)
	buf.put(42.asInstanceOf[Byte])
	buf.put(254.asInstanceOf[Byte])
	buf.flip()
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

  def toggleBlocking(ch: ServerSocketChannel): Unit = {
    ch.configureBlocking(!ch.isBlocking())
  }

  @After def cleanup(): Unit = {
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

  def readFrom(ch: SocketChannel): Unit = {
    val buf = ByteBuffer.allocate(1)
    val ret = ch.read(buf)
    count += 1
    if (count < 3) {
      assert (ret == 1, { "1 != (ret == " + ret + ")" })
    } else {
      assert (ret == -1, { "-1 != (ret == " + ret + ")" })
    }
  }

  def startClient: Unit = {
    assert(client == null)
    client = new TestClient()
//    if (!TestJPF.isJPFRun()) {
      client.run()
//    }
    count = 0
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
