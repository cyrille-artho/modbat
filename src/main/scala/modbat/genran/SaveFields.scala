package modbat.genran

import java.lang.reflect.Field

import modbat.dsl.Model

import scala.collection.mutable.HashMap

class SaveFields(val fields: List[Field], val model: Model)  {
  val values = new HashMap[Field, Any]

}
