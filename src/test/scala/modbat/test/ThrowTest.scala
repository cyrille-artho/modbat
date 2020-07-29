package modbat.test

import modbat.dsl._

class ThrowTest extends Model {
  var on = true

  def dangerous: Unit = {
    require(on)
    throw new Exception("Test")
  }

  def defuse: Unit = {
    on = false
  }

  "init" -> "init" := dangerous throws "Exception"
  "init" -> "init" := defuse
}
