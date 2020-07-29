package modbat.test

import modbat.dsl._

/** Test Modbat API functions to get the current random seed,
    and perform a given action only if a test failed */
class RSeedTestSuccess extends Model {

  @After def finish: Unit = {
    if (testFailed) {
      Console.out.println("This number is odd: " + rSeed)
    }
  }

  val rSeed = getRandomSeed()
  // transitions
  "init" -> "end" := {
    assert (rSeed == getRandomSeed)
    assert (rSeed % 2 == 0)
  }
}
