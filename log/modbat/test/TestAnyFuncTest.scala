package modbat.test

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT
import modbat.log.Log
import modbat.offline.ReplayRandom

object TestAnyFuncTest {
  def test89a82931be29e65() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x89a82931be29e65L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x89a82931be29e65L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x89a82931be29e65L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x89a82931be29e65L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x89a82931be29e65L, Array(0x0), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x89a82931be29e65L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/AnyFuncTest.scala:20: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test27fe2d950fc74b21() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(0x0), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/AnyFuncTest.scala:20: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test19b6aa1b14a29017() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x19b6aa1b14a29017L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x19b6aa1b14a29017L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x19b6aa1b14a29017L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x19b6aa1b14a29017L, Array(0x0), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x19b6aa1b14a29017L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/AnyFuncTest.scala:20: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test85780661bf76533() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x85780661bf76533L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x85780661bf76533L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x85780661bf76533L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x85780661bf76533L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/AnyFuncTest.scala:20: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test486e122d1bcbade7() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x486e122d1bcbade7L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x486e122d1bcbade7L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x486e122d1bcbade7L, Array(0x0), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x486e122d1bcbade7L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/AnyFuncTest.scala:20: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test745035c2b643033() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x745035c2b643033L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x745035c2b643033L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x745035c2b643033L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/AnyFuncTest.scala:20: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test1e528010d3d2d54() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x1e528010d3d2d54L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x1e528010d3d2d54L, Array(0x0), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    MBT.setRNG (new ReplayRandom(0x1e528010d3d2d54L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/AnyFuncTest.scala:20: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test8d32a08e0d0b1183() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x8d32a08e0d0b1183L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x8d32a08e0d0b1183L, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/AnyFuncTest.scala:20: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test5a92477004cf57fb() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x5a92477004cf57fbL, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x5a92477004cf57fbL, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/AnyFuncTest.scala:20: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test284ca782182a253f() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x284ca782182a253fL, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x284ca782182a253fL, Array(0x1), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/AnyFuncTest.scala:13: setTo3

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/AnyFuncTest.scala:20: assert
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
    MBT.loadModelClass ("modbat.test.AnyFuncTest")
    MBT.invokeAnnotatedStaticMethods(classOf[Init], null)

    test89a82931be29e65()
    test27fe2d950fc74b21()
    test19b6aa1b14a29017()
    test85780661bf76533()
    test486e122d1bcbade7()
    test745035c2b643033()
    test1e528010d3d2d54()
    test8d32a08e0d0b1183()
    test5a92477004cf57fb()
    test284ca782182a253f()

    MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
  }
}
