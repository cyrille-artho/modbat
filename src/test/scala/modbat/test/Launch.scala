package modbat.test

import modbat.dsl._

class Launch extends Model {
  val child = new Child(this)
  var run = true
  "parent-init" -> "parent-main" := {
    launch(child)
  }
  "parent-main" -> "parent-end" := {
    run = false
  }
  class Child(parent: Launch) extends Model {
    "child-init" -> "child-end" := {
      //require(!parent.run)
    }
  }
}
