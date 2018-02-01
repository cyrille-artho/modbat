package modbat.test

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT
import modbat.log.Log
import modbat.offline.ReplayRandom

object TestMaybeTest {
  def test6cd31fcc37fc8efa() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x6cd31fcc37fc8efaL, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x6cd31fcc37fc8efaL, Array(0x23f1b8), 0))
    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/MaybeTest.scala:10: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def main(args: Array[String]) {
    Log.setLevel(2)
    MBT.setMaybeProbability(0.01)
    if (args.contains("--rethrow-exceptions")) {
      MBT.setRethrowExceptions(true)
    }
    MBT.configClassLoader("build/modbat-test.jar")
    MBT.loadModelClass ("modbat.test.MaybeTest")
    MBT.invokeAnnotatedStaticMethods(classOf[Init], null)

    test6cd31fcc37fc8efa()

    MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
  }
}
