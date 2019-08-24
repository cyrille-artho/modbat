package modbat.test

import modbat.dsl._

/** Test Modbat API functions to get the current random seed,
    and perform a given action only if a test failed */
class RSeedTestSuccess extends Model {
  var rSeed: Long = _

  @After def finish {
    if (testFailed) {
      System.out.println("This number is odd: " + rSeed)
    }
  }

  @Before def setSeed {
    rSeed = getRandomSeed()
  }

  // transitions
  "init" -> "end" := {
    assert (rSeed == getRandomSeed)
    assert (rSeed % 2 == 0)
  }
}
