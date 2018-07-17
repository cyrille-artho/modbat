package modbat.test

import modbat.dsl._

object SubModel4 {
  @Init def init { Console.err.println("Init") }
}

class SubModel4 extends SubModel3 {
  @States(Array("init")) def foo { Console.out.println("...") }
}
