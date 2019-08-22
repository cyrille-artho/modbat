package modbat.test

import modbat.dsl._

object SubModel4 {
  @Init def init { System.err.println("Init") }
}

class SubModel4 extends SubModel3 {
  @States(Array("init")) def foo { System.out.println("...") }
}
