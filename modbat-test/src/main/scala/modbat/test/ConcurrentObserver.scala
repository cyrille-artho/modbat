package modbat.test

import modbat.dsl._

class ConcurrentObserver(val target: ObserverHarness7) extends Observer {
  class BGThread(i: Int) extends Thread {
    override def run() {
      assert (i < 4)
    }
  }

  def this() = this(null)

  // transitions
  "zero" -> "one" := {
    require(target.i > 0)
    Console.out.println("one")
  }
  "one" -> "many" := {
    require(target.i > 1)
    Console.out.println("many")
  }
  "many" -> "many" := {
    val t = new BGThread(target.i)
    t.start
    t.join
  }
}
