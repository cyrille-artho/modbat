package modbat.test

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT
import modbat.log.Log
import modbat.offline.ReplayRandom

object TestRSeedTestSuccess {
  def test3ba471c1785a0175() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x3ba471c1785a0175L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/RSeedTestSuccess.scala:19: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test89a82931be29e65() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x89a82931be29e65L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/RSeedTestSuccess.scala:19: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test27fe2d950fc74b21() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/RSeedTestSuccess.scala:19: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test19b6aa1b14a29017() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x19b6aa1b14a29017L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/RSeedTestSuccess.scala:19: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test85780661bf76533() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x85780661bf76533L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/RSeedTestSuccess.scala:19: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test486e122d1bcbade7() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x486e122d1bcbade7L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/RSeedTestSuccess.scala:19: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def main(args: Array[String]) {
    Log.setLevel(3)
    MBT.setMaybeProbability(0.5)
    if (args.contains("--rethrow-exceptions")) {
      MBT.setRethrowExceptions(true)
    }
    MBT.configClassLoader("build/modbat-test.jar")
    MBT.loadModelClass ("modbat.test.RSeedTestSuccess")
    MBT.invokeAnnotatedStaticMethods(classOf[Init], null)

    test3ba471c1785a0175()
    test89a82931be29e65()
    test27fe2d950fc74b21()
    test19b6aa1b14a29017()
    test85780661bf76533()
    test486e122d1bcbade7()

    MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
  }
}
