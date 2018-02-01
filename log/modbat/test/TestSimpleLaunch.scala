package modbat.test

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT
import modbat.log.Log
import modbat.offline.ReplayRandom

object TestSimpleLaunch {
  def test402b73cd0066eaea() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-1: skip

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-1: launch

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-1: assert

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-2: skip

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-2: launch

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-2: launch

    val m2: MBT = MBT.getLaunchedModel(2)
    m2.executeTransition(m2.getTransition(0))
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-3: skip

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-2: launch

    m2.executeTransition(m2.getTransition(2))
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-3: assert

    val m3: MBT = MBT.getLaunchedModel(3)
    m3.executeTransition(m3.getTransition(0))
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-4: skip

    m3.executeTransition(m3.getTransition(2))
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-4: assert

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-2: launch

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-2: launch

    val m6: MBT = MBT.getLaunchedModel(6)
    m6.executeTransition(m6.getTransition(0))
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-7: skip

    val m5: MBT = MBT.getLaunchedModel(5)
    m5.executeTransition(m5.getTransition(0))
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-6: skip

    m6.executeTransition(m6.getTransition(2))
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-7: assert

    m5.executeTransition(m5.getTransition(2))
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-6: assert

    val m4: MBT = MBT.getLaunchedModel(4)
    m4.executeTransition(m4.getTransition(0))
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-5: skip

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-2: assert

    m4.executeTransition(m4.getTransition(1))
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-5: launch

    m4.executeTransition(m4.getTransition(2))
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-5: assert

    val m7: MBT = MBT.getLaunchedModel(7)
    m7.executeTransition(m7.getTransition(0))
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-8: skip

    m7.setExpectedException("java.lang.AssertionError")
    m7.executeTransition(m7.getTransition(2))
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-8: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test41530f4040856586() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x41530f4040856586L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-1: skip

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-1: launch

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-1: launch

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-1: assert

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-2: skip

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-2: assert

    val m2: MBT = MBT.getLaunchedModel(2)
    m2.executeTransition(m2.getTransition(0))
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-3: skip

    m2.executeTransition(m2.getTransition(1))
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-3: launch

    val m3: MBT = MBT.getLaunchedModel(3)
    m3.executeTransition(m3.getTransition(0))
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-4: skip

    m3.executeTransition(m3.getTransition(1))
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-4: launch

    m2.executeTransition(m2.getTransition(1))
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-3: launch

    m3.executeTransition(m3.getTransition(2))
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-4: assert

    val m5: MBT = MBT.getLaunchedModel(5)
    m5.executeTransition(m5.getTransition(0))
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-6: skip

    m5.executeTransition(m5.getTransition(1))
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-6: launch

    val m6: MBT = MBT.getLaunchedModel(6)
    m6.executeTransition(m6.getTransition(0))
    // modbat/test/SimpleLaunch.scala:9: modbat.test.SimpleLaunch-7: skip

    m5.executeTransition(m5.getTransition(1))
    // modbat/test/SimpleLaunch.scala:11: modbat.test.SimpleLaunch-6: launch

    m6.setExpectedException("java.lang.AssertionError")
    m6.executeTransition(m6.getTransition(2))
    // modbat/test/SimpleLaunch.scala:16: modbat.test.SimpleLaunch-7: assert
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
    MBT.loadModelClass ("modbat.test.SimpleLaunch")
    MBT.invokeAnnotatedStaticMethods(classOf[Init], null)

    test402b73cd0066eaea()
    test41530f4040856586()

    MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
  }
}
