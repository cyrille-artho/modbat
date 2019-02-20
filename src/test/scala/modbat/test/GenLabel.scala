package modbat.test

import modbat.dsl._

class GenLabel extends Model {
  var n = 0

  def inc() { n = n + 1 }

  def dec() { n = n - 1; assert (n > 0) }

  "init" -> "init" := inc
  "init" -> "init" := dec
}
