package modbat

import modbat.dsl._

class ModelTemplate extends Model {
  // transitions
  "reset" -> "somestate" := {
    // insert code here
  }
  "somestate" -> "end" := skip // empty transition function
  "reset" -> "end" := {
  }
}
