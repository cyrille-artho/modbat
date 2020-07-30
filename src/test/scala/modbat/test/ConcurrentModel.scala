package modbat.test

import modbat.dsl._

class ConcurrentModel extends Model {
  class BGThread extends Thread {
    override def run: Unit = {
      assert(false)
    }
  }

  // transitions
  "reset" -> "somestate" := {
    val t = new BGThread()
    t.start
    t.join
  }
  "somestate" -> "end" := skip
}
