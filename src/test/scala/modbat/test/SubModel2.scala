package modbat.test

import modbat.dsl._

class SubModel2 extends SubModel {
  // transitions
  "init" -> "err" := println("Hello, World!")
}
