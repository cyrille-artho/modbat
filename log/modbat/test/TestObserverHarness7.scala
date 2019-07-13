package modbat.test

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT
import modbat.log.Log
import modbat.offline.ReplayRandom

object TestObserverHarness7 {
  def test402b73cd0066eaea() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/ObserverHarness7.scala:8: modbat.test.ObserverHarness7-1: launch

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ObserverHarness7.scala:11: modbat.test.ObserverHarness7-1: i

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/ConcurrentObserver.scala:15: modbat.test.ConcurrentObserver-1: println

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ObserverHarness7.scala:11: modbat.test.ObserverHarness7-1: i

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/ConcurrentObserver.scala:19: modbat.test.ConcurrentObserver-1: println

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/ConcurrentObserver.scala:23: modbat.test.ConcurrentObserver-1: join

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ObserverHarness7.scala:11: modbat.test.ObserverHarness7-1: i

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/ConcurrentObserver.scala:23: modbat.test.ConcurrentObserver-1: join

    m0.executeTransition(m0.getTransition(1))
    // modbat/test/ObserverHarness7.scala:11: modbat.test.ObserverHarness7-1: i

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/ConcurrentObserver.scala:23: modbat.test.ConcurrentObserver-1: join
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
    MBT.loadModelClass ("modbat.test.ObserverHarness7")
    MBT.invokeAnnotatedStaticMethods(classOf[Init], null)

    test402b73cd0066eaea()

    MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
  }
}
