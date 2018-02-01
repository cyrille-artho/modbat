package modbat.test

import modbat.dsl._

class SimpleLaunch (val id: Int) extends Model {
  def this() = this(0)

  // transitions
  "init" -> "active" := skip
  "active" -> "active" := {
    launch(new SimpleLaunch(id + 1))
    // UNCONDITIONAL launch; the only non-det. is in the transition choice
    // used for a simpler test where "maybe" is not needed
  }
  "active" -> "done" := {
    assert (id < 3)
  }
}
