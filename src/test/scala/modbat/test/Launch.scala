package modbat.test

import modbat.dsl._

class Launch extends Model {
  var child :LaunchChild = _
  "parent-init" -> "parent-main" := {
    child = new LaunchChild()
    launch(child)
    println("launched child")
  } label "launch-child"
  "parent-main" -> "parent-end" := {
    println("parent second transition")
  } label "parent-finish"
}

class LaunchChild() extends Model {
  "child-init" -> "child-end" := {
    println("child transition")
  } label "child-trans"
}
