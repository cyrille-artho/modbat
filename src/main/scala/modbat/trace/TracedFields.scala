package modbat.trace

import java.lang.reflect.Field

import modbat.dsl.Model
import modbat.util.FieldUtil

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

class TracedFields(val fields: List[Field], val model: Model) {
  val values = new HashMap[Field, Any]

  def updates = {
    val results = new ListBuffer[(Field, Any)]
    for (f <- fields) {
      val newVal = FieldUtil.getValue(f, model)
      if (!values(f).equals(newVal)) {
	results += (f -> newVal)
	values(f) = newVal
      }
    }
    results.toList
  }
}
