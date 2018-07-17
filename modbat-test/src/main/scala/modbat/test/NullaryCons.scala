package modbat.test

import modbat.dsl._

class NullaryCons(val foo: Object) extends Model {
  def this() = this(null)
  val bar = foo.toString() // NullPointerException
  // transitions
  "reset" -> "end" := {
  } // no comma after last transition
}
