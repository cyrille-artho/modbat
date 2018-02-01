package modbat.test

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT
import modbat.log.Log
import modbat.offline.ReplayRandom

object TestGetState3 {
  def test3ba471c1785a0175() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x3ba471c1785a0175L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedOverrideTrans(0)
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/GetState3.scala:14: assert

    m0.executeTransition(m0.getTransition(5))
    // modbat/test/GetState3.scala:7: invariantCheck

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/GetState3.scala:20: i = ...

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/GetState3.scala:22: skip

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/GetState3.scala:23: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test402b73cd0066eaea() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedOverrideTrans(0)
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/GetState3.scala:14: assert

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/GetState3.scala:20: i = ...

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/GetState3.scala:22: skip

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/GetState3.scala:23: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test41530f4040856586() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x41530f4040856586L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedOverrideTrans(0)
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/GetState3.scala:14: assert

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/GetState3.scala:20: i = ...

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/GetState3.scala:22: skip

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/GetState3.scala:23: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test89a82931be29e65() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x89a82931be29e65L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedOverrideTrans(0)
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/GetState3.scala:14: assert

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/GetState3.scala:20: i = ...

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/GetState3.scala:22: skip

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/GetState3.scala:23: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test49a846e52b813972() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x49a846e52b813972L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedOverrideTrans(0)
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/GetState3.scala:14: assert

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/GetState3.scala:20: i = ...

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/GetState3.scala:22: skip

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/GetState3.scala:23: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test27fe2d950fc74b21() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedOverrideTrans(0)
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/GetState3.scala:14: assert

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/GetState3.scala:20: i = ...

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/GetState3.scala:22: skip

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/GetState3.scala:23: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test19b6aa1b14a29017() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x19b6aa1b14a29017L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedOverrideTrans(0)
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/GetState3.scala:14: assert

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/GetState3.scala:20: i = ...

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/GetState3.scala:22: skip

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/GetState3.scala:23: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test5ff50ec9279365d2() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x5ff50ec9279365d2L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedOverrideTrans(0)
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/GetState3.scala:14: assert

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/GetState3.scala:20: i = ...

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/GetState3.scala:22: skip

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/GetState3.scala:23: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test85780661bf76533() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x85780661bf76533L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedOverrideTrans(0)
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/GetState3.scala:14: assert

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/GetState3.scala:20: i = ...

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/GetState3.scala:22: skip

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/GetState3.scala:23: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test486e122d1bcbade7() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x486e122d1bcbade7L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.setExpectedOverrideTrans(0)
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/GetState3.scala:14: assert

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/GetState3.scala:20: i = ...

    m0.executeTransition(m0.getTransition(6))
    // modbat/test/GetState3.scala:7: invariantCheck

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/GetState3.scala:22: skip

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/GetState3.scala:23: assert
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
    MBT.loadModelClass ("modbat.test.GetState3")
    MBT.invokeAnnotatedStaticMethods(classOf[Init], null)

    test3ba471c1785a0175()
    test402b73cd0066eaea()
    test41530f4040856586()
    test89a82931be29e65()
    test49a846e52b813972()
    test27fe2d950fc74b21()
    test19b6aa1b14a29017()
    test5ff50ec9279365d2()
    test85780661bf76533()
    test486e122d1bcbade7()

    MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
  }
}
