package modbat.test

import modbat.dsl._

abstract class BaseModel extends Model {
  @Before def init: Unit = { Console.err.println("Hello.") }
}
