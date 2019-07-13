package modbat.test

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT
import modbat.log.Log
import modbat.offline.ReplayRandom

object TestChooseTest {
  def test782de49f112cb820() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x782de49f112cb820L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x782de49f112cb820L, Array(0x0, 0x0), 0))
    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(2))
    // modbat/test/ChooseTest.scala:15: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def main(args: Array[String]) {
    Log.setLevel(2)
    MBT.setMaybeProbability(0.5)
    if (args.contains("--rethrow-exceptions")) {
      MBT.setRethrowExceptions(true)
    }
    MBT.configClassLoader("build/modbat-test.jar")
    MBT.loadModelClass ("modbat.test.ChooseTest")
    MBT.invokeAnnotatedStaticMethods(classOf[Init], null)

    test782de49f112cb820()

    MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
  }
}
