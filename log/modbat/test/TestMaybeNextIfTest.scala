package modbat.test

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT
import modbat.log.Log
import modbat.offline.ReplayRandom

object TestMaybeNextIfTest {
  def test3ba471c1785a0175() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x3ba471c1785a0175L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x3ba471c1785a0175L, Array(0x1e759e65), 0))
    m0.setExpectedOverrideTrans(0)
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/MaybeNextIfTest.scala:8: println

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/MaybeNextIfTest.scala:11: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test402b73cd0066eaea() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(0x72663972), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/MaybeNextIfTest.scala:8: println

    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(0x365c65d2), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/MaybeNextIfTest.scala:8: println

    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(0x3ca69dfb), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/MaybeNextIfTest.scala:8: println

    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(0x6c12f7d), 0))
    m0.setExpectedOverrideTrans(0)
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/MaybeNextIfTest.scala:8: println

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/MaybeNextIfTest.scala:11: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def main(args: Array[String]) {
    Log.setLevel(2)
    MBT.setMaybeProbability(0.3)
    if (args.contains("--rethrow-exceptions")) {
      MBT.setRethrowExceptions(true)
    }
    MBT.configClassLoader("build/modbat-test.jar")
    MBT.loadModelClass ("modbat.test.MaybeNextIfTest")
    MBT.invokeAnnotatedStaticMethods(classOf[Init], null)

    test3ba471c1785a0175()
    test402b73cd0066eaea()

    MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
  }
}
