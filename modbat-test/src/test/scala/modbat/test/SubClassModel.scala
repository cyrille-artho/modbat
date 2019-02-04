package modbat.test

import modbat.dsl._

class SubClassModel extends SuperClass {
  // transitions
  "init" -> "end" := show
}
