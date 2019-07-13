package modbat.test

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT
import modbat.log.Log
import modbat.offline.ReplayRandom

object TestComplexLaunch {
  def test5ff50ec9279365d2() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x5ff50ec9279365d2L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/ComplexLaunch.scala:8: modbat.test.ComplexLaunch-1: skip

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-1: skip

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/Child.scala:12: modbat.test.Child-1: assert

    val m2: MBT = MBT.getLaunchedModel(2)
    m2.executeTransition(m2.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-2: skip

    m2.executeTransition(m2.getTransition(2))
    // modbat/test/Child.scala:12: modbat.test.Child-2: assert

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    val m5: MBT = MBT.getLaunchedModel(5)
    m5.executeTransition(m5.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-5: skip

    val m4: MBT = MBT.getLaunchedModel(4)
    m4.executeTransition(m4.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-4: skip

    m5.setExpectedException("java.lang.AssertionError")
    m5.executeTransition(m5.getTransition(2))
    // modbat/test/Child.scala:12: modbat.test.Child-5: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test1e528010d3d2d54() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x1e528010d3d2d54L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/ComplexLaunch.scala:8: modbat.test.ComplexLaunch-1: skip

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    val m2: MBT = MBT.getLaunchedModel(2)
    m2.executeTransition(m2.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-2: skip

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-1: skip

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m2.executeTransition(m2.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-2: id

    m2.executeTransition(m2.getTransition(2))
    // modbat/test/Child.scala:12: modbat.test.Child-2: assert

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/Child.scala:12: modbat.test.Child-1: assert

    val m3: MBT = MBT.getLaunchedModel(3)
    m3.executeTransition(m3.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-3: skip

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(2))
    // modbat/test/ComplexLaunch.scala:14: modbat.test.ComplexLaunch-1: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test8d32a08e0d0b1183() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x8d32a08e0d0b1183L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/ComplexLaunch.scala:8: modbat.test.ComplexLaunch-1: skip

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-1: skip

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-1: id

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/Child.scala:12: modbat.test.Child-1: assert

    val m2: MBT = MBT.getLaunchedModel(2)
    m2.executeTransition(m2.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-2: skip

    m2.executeTransition(m2.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-2: id

    m2.executeTransition(m2.getTransition(2))
    // modbat/test/Child.scala:12: modbat.test.Child-2: assert

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    val m3: MBT = MBT.getLaunchedModel(3)
    m3.executeTransition(m3.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-3: skip

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(2))
    // modbat/test/ComplexLaunch.scala:14: modbat.test.ComplexLaunch-1: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test8102b494089e6116() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x8102b494089e6116L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/ComplexLaunch.scala:8: modbat.test.ComplexLaunch-1: skip

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-1: skip

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/ComplexLaunch.scala:14: modbat.test.ComplexLaunch-1: assert

    val m2: MBT = MBT.getLaunchedModel(2)
    m2.executeTransition(m2.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-2: skip

    m2.executeTransition(m2.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-2: id

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-1: id

    m2.executeTransition(m2.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-2: id

    m2.executeTransition(m2.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-2: id

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-1: id

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-1: id

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-1: id

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-1: id

    m2.setExpectedException("java.lang.AssertionError")
    m2.executeTransition(m2.getTransition(2))
    // modbat/test/Child.scala:12: modbat.test.Child-2: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test46e3ffb302d6b70f() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x46e3ffb302d6b70fL, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/ComplexLaunch.scala:8: modbat.test.ComplexLaunch-1: skip

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-1: skip

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-1: id

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/Child.scala:12: modbat.test.Child-1: assert

    val m2: MBT = MBT.getLaunchedModel(2)
    m2.executeTransition(m2.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-2: skip

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    val m6: MBT = MBT.getLaunchedModel(6)
    m6.executeTransition(m6.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-6: skip

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(2))
    // modbat/test/ComplexLaunch.scala:14: modbat.test.ComplexLaunch-1: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test7c984b5d1a209c21() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x7c984b5d1a209c21L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/ComplexLaunch.scala:8: modbat.test.ComplexLaunch-1: skip

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/ComplexLaunch.scala:14: modbat.test.ComplexLaunch-1: assert

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-1: skip

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-1: id

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-1: id

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-1: id

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-1: id

    m1.setExpectedException("java.lang.AssertionError")
    m1.executeTransition(m1.getTransition(2))
    // modbat/test/Child.scala:12: modbat.test.Child-1: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test2a83b5bd2ae1ea70() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x2a83b5bd2ae1ea70L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/ComplexLaunch.scala:8: modbat.test.ComplexLaunch-1: skip

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-1: skip

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-1: id

    val m2: MBT = MBT.getLaunchedModel(2)
    m2.executeTransition(m2.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-2: skip

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m2.executeTransition(m2.getTransition(2))
    // modbat/test/Child.scala:12: modbat.test.Child-2: assert

    val m4: MBT = MBT.getLaunchedModel(4)
    m4.executeTransition(m4.getTransition(0))
    // modbat/test/Child.scala:7: modbat.test.Child-4: skip

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-1: id

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ComplexLaunch.scala:9: modbat.test.ComplexLaunch-1: launch

    m4.executeTransition(m4.getTransition(1))
    // modbat/test/Child.scala:9: modbat.test.Child-4: id

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(2))
    // modbat/test/ComplexLaunch.scala:14: modbat.test.ComplexLaunch-1: assert
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
    MBT.loadModelClass ("modbat.test.ComplexLaunch")
    MBT.invokeAnnotatedStaticMethods(classOf[Init], null)

    test5ff50ec9279365d2()
    test1e528010d3d2d54()
    test8d32a08e0d0b1183()
    test8102b494089e6116()
    test46e3ffb302d6b70f()
    test7c984b5d1a209c21()
    test2a83b5bd2ae1ea70()

    MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
  }
}
