package modbat.test

import modbat.dsl._

class ThrowTest extends Model {
  var on = true

  def dangerous {
    require(on)
    throw new Exception("Test")
  }

  def defuse {
    on = false
  }

  "init" -> "init" := dangerous throws "Exception"
  "init" -> "init" := defuse
}
