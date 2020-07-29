package modbat.test

import modbat.dsl._

class GenLabel extends Model {
  var n = 0

  def inc(): Unit = { n = n + 1 }

  def dec(): Unit = { n = n - 1; assert (n > 0) }

  "init" -> "init" := inc
  "init" -> "init" := dec
}
