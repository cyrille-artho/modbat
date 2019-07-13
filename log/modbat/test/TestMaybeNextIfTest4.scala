package modbat.test

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT
import modbat.log.Log
import modbat.offline.ReplayRandom

object TestMaybeNextIfTest4 {
  def test3ba471c1785a0175() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x3ba471c1785a0175L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x3ba471c1785a0175L, Array(0x1e759e65), 0))
    m0.setExpectedOverrideTrans(0)
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/MaybeNextIfTest4.scala:8: println

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(2))
    // modbat/test/MaybeNextIfTest4.scala:16: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test402b73cd0066eaea() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(0x72663972), 0))
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/MaybeNextIfTest4.scala:8: println

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/MaybeNextIfTest4.scala:12: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test41530f4040856586() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x41530f4040856586L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    MBT.setRNG (new ReplayRandom(0x41530f4040856586L, Array(0x3d5c4b21), 0))
    m0.setExpectedOverrideTrans(0)
    m0.executeTransition(m0.getTransition(0))
    // modbat/test/MaybeNextIfTest4.scala:8: println

    m0.setExpectedException("java.lang.AssertionError")
    m0.executeTransition(m0.getTransition(2))
    // modbat/test/MaybeNextIfTest4.scala:16: assert
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def main(args: Array[String]) {
    Log.setLevel(2)
    MBT.setMaybeProbability(0.48)
    if (args.contains("--rethrow-exceptions")) {
      MBT.setRethrowExceptions(true)
    }
    MBT.configClassLoader("build/modbat-test.jar")
    MBT.loadModelClass ("modbat.test.MaybeNextIfTest4")
    MBT.invokeAnnotatedStaticMethods(classOf[Init], null)

    test3ba471c1785a0175()
    test402b73cd0066eaea()
    test41530f4040856586()

    MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
  }
}
