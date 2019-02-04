package modbat.test

import modbat.dsl._

class TickTockObserver(val target: TickTockTest) extends Observer {
  var i = 0
  // transitions
  "tick" -> "tock" := {
    require(target.i > i)
    Console.out.println("tick")
    i = i + 1
  }
  "tock" -> "tick" := {
    require(target.i > i)
    Console.out.println("tock")
    i = i + 2
  }
}
