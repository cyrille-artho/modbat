import modbat.dsl._

class NoPackage extends Model {
  // transitions
  "init" -> "init" := {
    assert(false) // test reporting bug with no package name in source
  }
}
