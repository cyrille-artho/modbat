package modbat.test

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT
import modbat.log.Log
import modbat.offline.ReplayRandom

object TestObserverHarness2 {
  def test402b73cd0066eaea() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/ObserverHarness2.scala:8: modbat.test.ObserverHarness2-1: launch

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/ObserverHarness2.scala:8: modbat.test.ObserverHarness2-1: launch

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/ObserverHarness2.scala:8: modbat.test.ObserverHarness2-1: launch

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/ObserverHarness2.scala:8: modbat.test.ObserverHarness2-1: launch

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/ObserverHarness2.scala:8: modbat.test.ObserverHarness2-1: launch

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/ObserverHarness2.scala:8: modbat.test.ObserverHarness2-1: launch

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ObserverHarness2.scala:10: modbat.test.ObserverHarness2-1: skip

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/ObserverHarness2.scala:12: modbat.test.ObserverHarness2-1: i

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/TestObserver.scala:9: modbat.test.TestObserver-1: println

    val m2: MBT = MBT.getLaunchedModel(2)
    m2.executeTransition(m2.getTransition(0))
    // modbat/test/TestObserver.scala:9: modbat.test.TestObserver-2: println

    val m3: MBT = MBT.getLaunchedModel(3)
    m3.executeTransition(m3.getTransition(0))
    // modbat/test/TestObserver.scala:9: modbat.test.TestObserver-3: println

    val m4: MBT = MBT.getLaunchedModel(4)
    m4.executeTransition(m4.getTransition(0))
    // modbat/test/TestObserver.scala:9: modbat.test.TestObserver-4: println

    val m5: MBT = MBT.getLaunchedModel(5)
    m5.executeTransition(m5.getTransition(0))
    // modbat/test/TestObserver.scala:9: modbat.test.TestObserver-5: println

    val m6: MBT = MBT.getLaunchedModel(6)
    m6.executeTransition(m6.getTransition(0))
    // modbat/test/TestObserver.scala:9: modbat.test.TestObserver-6: println

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/ObserverHarness2.scala:12: modbat.test.ObserverHarness2-1: i

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/TestObserver.scala:13: modbat.test.TestObserver-1: println

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/TestObserver.scala:18: modbat.test.TestObserver-1: assert

    m2.executeTransition(m2.getTransition(1))
    // modbat/test/TestObserver.scala:13: modbat.test.TestObserver-2: println

    m2.executeTransition(m2.getTransition(2))
    // modbat/test/TestObserver.scala:18: modbat.test.TestObserver-2: assert

    m3.executeTransition(m3.getTransition(1))
    // modbat/test/TestObserver.scala:13: modbat.test.TestObserver-3: println

    m3.executeTransition(m3.getTransition(2))
    // modbat/test/TestObserver.scala:18: modbat.test.TestObserver-3: assert

    m4.executeTransition(m4.getTransition(1))
    // modbat/test/TestObserver.scala:13: modbat.test.TestObserver-4: println

    m4.executeTransition(m4.getTransition(2))
    // modbat/test/TestObserver.scala:18: modbat.test.TestObserver-4: assert

    m5.executeTransition(m5.getTransition(1))
    // modbat/test/TestObserver.scala:13: modbat.test.TestObserver-5: println

    m5.executeTransition(m5.getTransition(2))
    // modbat/test/TestObserver.scala:18: modbat.test.TestObserver-5: assert

    m6.executeTransition(m6.getTransition(1))
    // modbat/test/TestObserver.scala:13: modbat.test.TestObserver-6: println

    m6.executeTransition(m6.getTransition(2))
    // modbat/test/TestObserver.scala:18: modbat.test.TestObserver-6: assert

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/ObserverHarness2.scala:12: modbat.test.ObserverHarness2-1: i

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/TestObserver.scala:18: modbat.test.TestObserver-1: assert

    m2.executeTransition(m2.getTransition(2))
    // modbat/test/TestObserver.scala:18: modbat.test.TestObserver-2: assert

    m3.executeTransition(m3.getTransition(2))
    // modbat/test/TestObserver.scala:18: modbat.test.TestObserver-3: assert

    m4.executeTransition(m4.getTransition(2))
    // modbat/test/TestObserver.scala:18: modbat.test.TestObserver-4: assert

    m5.executeTransition(m5.getTransition(2))
    // modbat/test/TestObserver.scala:18: modbat.test.TestObserver-5: assert

    m6.executeTransition(m6.getTransition(2))
    // modbat/test/TestObserver.scala:18: modbat.test.TestObserver-6: assert

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/ObserverHarness2.scala:12: modbat.test.ObserverHarness2-1: i

    m1.setExpectedException("java.lang.AssertionError")
    m1.executeTransition(m1.getTransition(2))
    // modbat/test/TestObserver.scala:18: modbat.test.TestObserver-1: assert
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
    MBT.loadModelClass ("modbat.test.ObserverHarness2")
    MBT.invokeAnnotatedStaticMethods(classOf[Init], null)

    test402b73cd0066eaea()

    MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
  }
}
