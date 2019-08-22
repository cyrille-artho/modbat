package modbat.test

import modbat.dsl._

class SubModel3 extends SubModel {
  // transitions
  "init" -> "err" := System.out.println("Hello, World!")
}
