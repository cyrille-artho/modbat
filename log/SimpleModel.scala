import modbat.examples.SimpleCounter

import modbat.mbt._
import modbat.mbt.Predef._

class SimpleModel extends Model {
  var counter: SimpleCounter = _
  def instance() = {
    new MBT (
      // EVENT_001 -> 0
      "reset" -> "zero" := {
	counter = new SimpleCounter()
      },
      // EVENT_0012 -> 1
      "zero" -> "zero" := {
	counter.toggleSwitch
      },
      // EVENT_0013 -> 2
      "zero" -> "two" := {
	counter.inc2
      },
      // EVENT_0014 -> 3
      "two" -> "end" := {
	assert (counter.value == 2)
      },
      // EVENT_0011 -> 4
      "zero" -> "one" := {
	counter.inc
      }
    )
  }
}
