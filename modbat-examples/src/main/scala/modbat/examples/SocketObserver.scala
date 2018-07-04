package modbat.examples

import modbat.dsl._

import java.net.Socket

class SocketObserver(val socket: Socket) extends Observer {
  // transitions
  "init" -> "connected" := {
    require(socket.isConnected)
  }
  "connected" -> "closed" := {
    require(socket.isClosed)
  }

  @After def checkIsClosed {
    assert(getCurrentState.equals("closed"))
  }
}
