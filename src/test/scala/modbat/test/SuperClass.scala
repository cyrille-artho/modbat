package modbat.test

import modbat.dsl._

abstract class SuperClass extends Model {
  var a = 1
  def show: Unit = { println(a) }
}
