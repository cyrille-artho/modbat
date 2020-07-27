package modbat.test

import modbat.dsl._

class ChooseTest2 extends Model {
  var n = 0

  def use(i: Int) {
    Console.out.println("Using " + i)
    assert (i > 0)
    assert (i < 4)
    n += 1
  }

  // transitions
  "ok" -> "ok" := use(1 + Integer.valueOf(choose(0, n)))
}
