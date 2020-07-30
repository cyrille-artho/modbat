package modbat.examples

import modbat.dsl._
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.ClosedByInterruptException
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

/* Revised model of JavaNioSocket. Old model is kept as example and
   for regression testing. */

object JavaNioSocket3 {
  var port: Int = 0

  object TestServer extends Thread {
    val ch = ServerSocketChannel.open()
    ch.socket().bind(new InetSocketAddress("localhost", 0))
    JavaNioSocket3.port = ch.socket().getLocalPort()
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
      TestServer.ch.close()
    }
  }

  @Init def startServer(): Unit = {
    TestServer.start()
  }

  @Shutdown def shutdown(): Unit = {
    TestServer.interrupt()
  }
}

class JavaNioSocket3 extends Model {
  var connection: SocketChannel = null
  var connected: Boolean = false // track ret. val. of non-blocking connect
  var n = 0 // number of bytes read so far

  @After def cleanup(): Unit = {
    if (connection != null) {
      connection.close()
    }
  }

  // helper functions
  def connect(connection: SocketChannel) = {
    connection.connect(new InetSocketAddress("localhost", JavaNioSocket3.port))
  }

  def readFrom(connection: SocketChannel, n: Int) = {
    val buf = ByteBuffer.allocate(1)
   /* TODO: for non-blocking reads: check return value, increment n only
      if data is actually read. JPF model should help to find this. */
    val l = connection.read(buf)
    var limit = 0 // non-blocking read may return 0 bytes
    var res = 0
    if (n < 2) {
      if (connection.isBlocking()) {
	limit = 1
      }
      assert(l >= limit,
	     {"Expected data, got " + l + " after " +
	      (n + 1) + " reads with blocking = " + connection.isBlocking()})
      res = l
    } else {
      if ((n > 2) || connection.isBlocking()) { // definitely EOF
	limit = -1
      }
      assert(l <= limit,
	     {"Expected EOF, got " + l + " after " +
	      (n + 1) + " reads with blocking = " + connection.isBlocking()})
      res = -l // advance count for reads also upon EOF
    }
    res
  }

  def toggleBlocking(connection: SocketChannel): Unit = {
    connection.configureBlocking(!connection.isBlocking())
  }

  // transitions
  "reset" -> "open" := {
    connection = SocketChannel.open()
  }
  "open" -> "open" := {
    toggleBlocking(connection)
  }
  "open" -> "connected" := {
    require(connection.isBlocking())
    connect(connection)
  }
  "open" -> "maybeconnected" := {
    require(!connection.isBlocking())
    Thread.sleep(50)
    connected = connect(connection)
    maybe { toggleBlocking(connection); connected = connection.finishConnect }
  } maybeNextIf (() => connected) -> "connected"
  "maybeconnected" -> "maybeconnected" := {
    toggleBlocking(connection)
  }
  "maybeconnected" -> "connected" := {
    require(connection.isBlocking())
    connection.finishConnect()
  }
  "maybeconnected" -> "maybeconnected" := {
    require(!connection.isBlocking())
    Thread.sleep(50)
  } maybeNextIf (() => connection.finishConnect) -> "connected"
  "open" -> "open" := {
    connection.finishConnect()
  } throws ("NoConnectionPendingException")
  "maybeconnected" -> "maybeconnected" := {
    require(!connected)
    connect(connection)
  } throws ("ConnectionPendingException")
  "connected" -> "connected" := {
    connect(connection)
  } throws ("AlreadyConnectedException")
  "open" -> "open" := {
    readFrom(connection, n)
  } throws ("NotYetConnectedException")
  "maybeconnected" -> "maybeconnected" := {
    require(!connected)
    readFrom(connection, n)
  } throws ("NotYetConnectedException")
  "connected" -> "connected" := {
    connection.finishConnect() // redundant call to finishConnect (no effect)
  }
  "connected" -> "connected" := {
    val l = readFrom(connection, n)
    n = n + l
  }
  List("open", "connected", "maybeconnected", "closed") -> "closed" := {
    connection.close()
  }
  "closed" -> "closed" := {
    choose(
      { () => readFrom(connection, n) },
      { () => toggleBlocking(connection) },
      { () => connect(connection) },
      { () => connection.finishConnect }
    )
  } throws ("ClosedChannelException")
}
