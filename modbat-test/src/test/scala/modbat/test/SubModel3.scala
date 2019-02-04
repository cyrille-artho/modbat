package modbat.test

import modbat.dsl._

class SubModel3 extends SubModel {
  // transitions
  "init" -> "err" := println("Hello, World!")
}
