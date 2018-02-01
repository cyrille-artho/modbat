package modbat.examples

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT
import modbat.log.Log
import modbat.offline.ReplayRandom

object TestSimpleModel {
  def test49a846e52b813972() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x49a846e52b813972L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/examples/SimpleModel.scala:10: new SimpleCounter

    m0.executeTransition(m0.getTransition(1))
    // modbat/examples/SimpleModel.scala:13: toggleSwitch

    m0.executeTransition(m0.getTransition(2))
    // modbat/examples/SimpleModel.scala:16: inc

    m0.executeTransition(m0.getTransition(3))
    // modbat/examples/SimpleModel.scala:19: inc

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(5))
    // modbat/examples/SimpleModel.scala:25: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test486e122d1bcbade7() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x486e122d1bcbade7L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/examples/SimpleModel.scala:10: new SimpleCounter

    m0.executeTransition(m0.getTransition(1))
    // modbat/examples/SimpleModel.scala:13: toggleSwitch

    m0.executeTransition(m0.getTransition(2))
    // modbat/examples/SimpleModel.scala:16: inc

    m0.executeTransition(m0.getTransition(3))
    // modbat/examples/SimpleModel.scala:19: inc

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(5))
    // modbat/examples/SimpleModel.scala:25: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test1690fa4e0c732f7d() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x1690fa4e0c732f7dL, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/examples/SimpleModel.scala:10: new SimpleCounter

    m0.executeTransition(m0.getTransition(1))
    // modbat/examples/SimpleModel.scala:13: toggleSwitch

    m0.executeTransition(m0.getTransition(2))
    // modbat/examples/SimpleModel.scala:16: inc

    m0.executeTransition(m0.getTransition(3))
    // modbat/examples/SimpleModel.scala:19: inc

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(5))
    // modbat/examples/SimpleModel.scala:25: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test636e772f0798297b() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x636e772f0798297bL, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/examples/SimpleModel.scala:10: new SimpleCounter

    m0.executeTransition(m0.getTransition(1))
    // modbat/examples/SimpleModel.scala:13: toggleSwitch

    m0.executeTransition(m0.getTransition(2))
    // modbat/examples/SimpleModel.scala:16: inc

    m0.executeTransition(m0.getTransition(3))
    // modbat/examples/SimpleModel.scala:19: inc

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(5))
    // modbat/examples/SimpleModel.scala:25: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def main(args: Array[String]) {
    Log.setLevel(3)
    MBT.setMaybeProbability(0.5)
    if (args.contains("--rethrow-exceptions")) {
      MBT.setRethrowExceptions(true)
    }
    MBT.configClassLoader("build/modbat-examples.jar")
    MBT.loadModelClass ("modbat.examples.SimpleModel")
    MBT.invokeAnnotatedStaticMethods(classOf[Init], null)

    test49a846e52b813972()
    test486e122d1bcbade7()
    test1690fa4e0c732f7d()
    test636e772f0798297b()

    MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
  }
}
