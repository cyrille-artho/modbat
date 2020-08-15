package modbat.examples

import modbat.dsl._
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.ClosedByInterruptException
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

object NioSocket1 {
  var port: Int = 8888 // pre-set to non-0 value for "no-init" test
  var testServer: TestServer =_

  class TestServer extends Thread {
    val ch = ServerSocketChannel.open()
    ch.socket().bind(new InetSocketAddress("localhost", 0))
    NioSocket1.port = ch.socket().getLocalPort()
    ch.configureBlocking(true)

    override def run(): Unit = {
      var closed = false
      var connection: SocketChannel = null
      while (!closed) {
        try {
          connection = ch.accept()
          val buf = ByteBuffer.allocate(2)
          buf.asCharBuffer().put("\n")
          connection.write(buf)
          connection.socket().close()
        } catch {
          case e: ClosedByInterruptException => {
	    if (connection != null) {
              connection.socket().close()
	    }
            closed = true
          }
        }
      }
      ch.close()
    }
  }

  @Init def startServer(): Unit = {
    testServer = new TestServer()
    testServer.start()
  }

  @Shutdown def shutdown(): Unit = {
    testServer.interrupt()
  }
}

class NioSocket1 extends Model {
  var ch: SocketChannel = null

  @After def cleanup(): Unit = {
    if (ch != null) {
      ch.close()
    }
  }

  // helper functions
  def connect(ch: SocketChannel): Unit = {
    ch.connect(new InetSocketAddress("localhost", NioSocket1.port))
  }

  def readFrom(ch: SocketChannel): Unit = {
    val buf = ByteBuffer.allocate(1)
    assert(ch.read(buf) != -1)
  }

  def toggleBlocking(ch: SocketChannel): Unit = {
    ch.configureBlocking(!ch.isBlocking())
  }

  // transitions
  "reset" -> "open" := {
    ch = SocketChannel.open()
  }
  "open" -> "open" := {
    toggleBlocking(ch)
  }
  "open" -> "connected" := {
    require(ch.isBlocking())
    connect(ch)
  }
  "open" -> "maybeconnected" := {
    require(!ch.isBlocking())
    connect(ch)
  }
  "maybeconnected" -> "maybeconnected" := {
    toggleBlocking(ch)
  }
  "maybeconnected" -> "connected" := {
    require(ch.isBlocking())
    ch.finishConnect()
  }
  "maybeconnected" -> "maybeconnected" := {
    require(!ch.isBlocking())
    Thread.sleep(50)
  } maybeNextIf (() => ch.finishConnect) -> "connected"
  "open" -> "err" := {
    ch.finishConnect()
  } throws ("NoConnectionPendingException")
  "maybeconnected" -> "err" := {
    connect(ch)
  } throws ("ConnectionPendingException")
  "connected" -> "err" := {
    connect(ch)
  } throws ("AlreadyConnectedException")
  "open" -> "err" := {
    readFrom(ch)
  } throws ("NotYetConnectedException")
  "maybeconnected" -> "err" := {
    readFrom(ch)
  } throws ("NotYetConnectedException")
  "connected" -> "connected" := {
    ch.finishConnect() // redundant call to finishConnect (no effect)
  }
  "connected" -> "connected" := {
    readFrom(ch)
  }
  List("open", "connected", "maybeconnected", "closed") -> "closed" := {
    ch.close()
  }
  "closed" -> "err" := {
    readFrom(ch)
  } throws ("ClosedChannelException")
}
