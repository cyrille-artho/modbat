package modbat.test

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT
import modbat.log.Log
import modbat.offline.ReplayRandom

object TestTickTockTest {
  def test402b73cd0066eaea() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/TickTockTest.scala:9: modbat.test.TickTockTest-1: launch

    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(0x3), 0))
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/TickTockTest.scala:11: modbat.test.TickTockTest-1: i

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/TickTockObserver.scala:8: modbat.test.TickTockObserver-1: i

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/TickTockObserver.scala:13: modbat.test.TickTockObserver-1: i

    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(0x3), 0))
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/TickTockTest.scala:11: modbat.test.TickTockTest-1: i

    m1.executeTransition(m1.getTransition(0))
    // modbat/test/TickTockObserver.scala:8: modbat.test.TickTockObserver-1: i

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/TickTockObserver.scala:13: modbat.test.TickTockObserver-1: i

    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(0x0), 0))
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/TickTockTest.scala:11: modbat.test.TickTockTest-1: i

    m1.executeTransition(m1.getTransition(0))
    // modbat/test/TickTockObserver.scala:8: modbat.test.TickTockObserver-1: i

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/TickTockObserver.scala:13: modbat.test.TickTockObserver-1: i

    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(0x3), 0))
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/TickTockTest.scala:11: modbat.test.TickTockTest-1: i

    m1.executeTransition(m1.getTransition(0))
    // modbat/test/TickTockObserver.scala:8: modbat.test.TickTockObserver-1: i

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/TickTockObserver.scala:13: modbat.test.TickTockObserver-1: i

    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(0x2), 0))
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/TickTockTest.scala:11: modbat.test.TickTockTest-1: i

    m1.executeTransition(m1.getTransition(0))
    // modbat/test/TickTockObserver.scala:8: modbat.test.TickTockObserver-1: i

    m1.executeTransition(m1.getTransition(1))
    // modbat/test/TickTockObserver.scala:13: modbat.test.TickTockObserver-1: i

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(2))
    // modbat/test/TickTockTest.scala:17: modbat.test.TickTockTest-1: assert
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
    MBT.loadModelClass ("modbat.test.TickTockTest")
    MBT.invokeAnnotatedStaticMethods(classOf[Init], null)

    test402b73cd0066eaea()

    MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
  }
}
