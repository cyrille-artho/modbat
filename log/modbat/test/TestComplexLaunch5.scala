package modbat.test

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT
import modbat.log.Log
import modbat.offline.ReplayRandom

object TestComplexLaunch5 {
  def test402b73cd0066eaea() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x402b73cd0066eaeaL, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/ComplexLaunch5.scala:15: modbat.test.ComplexLaunch5-1: skip

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/ComplexLaunch5.scala:20: modbat.test.ComplexLaunch5-1: launch

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/ComplexLaunch5.scala:20: modbat.test.ComplexLaunch5-1: launch

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/ComplexLaunch5.scala:20: modbat.test.ComplexLaunch5-1: launch

    m0.executeTransition(m0.getTransition(3))
    // modbat/test/ComplexLaunch5.scala:25: modbat.test.ComplexLaunch5-1: assert

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/ComplexLaunch5.scala:15: modbat.test.ComplexLaunch5-2: skip

    m1.executeTransition(m1.getTransition(3))
    // modbat/test/ComplexLaunch5.scala:25: modbat.test.ComplexLaunch5-2: assert

    val m2: MBT = MBT.getLaunchedModel(2)
    m2.executeTransition(m2.getTransition(0))
    // modbat/test/ComplexLaunch5.scala:15: modbat.test.ComplexLaunch5-3: skip

    m2.executeTransition(m2.getTransition(3))
    // modbat/test/ComplexLaunch5.scala:25: modbat.test.ComplexLaunch5-3: assert

    val m3: MBT = MBT.getLaunchedModel(3)
    m3.executeTransition(m3.getTransition(0))
    // modbat/test/ComplexLaunch5.scala:15: modbat.test.ComplexLaunch5-4: skip

    m3.executeTransition(m3.getTransition(2))
    // modbat/test/ComplexLaunch5.scala:20: modbat.test.ComplexLaunch5-4: launch

    m3.executeTransition(m3.getTransition(1))
    // modbat/test/ComplexLaunch5.scala:16: modbat.test.ComplexLaunch5-4: launch

    m3.executeTransition(m3.getTransition(1))
    // modbat/test/ComplexLaunch5.scala:16: modbat.test.ComplexLaunch5-4: launch

    val m6: MBT = MBT.getLaunchedModel(6)
    m6.executeTransition(m6.getTransition(0))
    // modbat/test/Child5.scala:12: modbat.test.Child5-2: skip

    val m5: MBT = MBT.getLaunchedModel(5)
    m5.executeTransition(m5.getTransition(0))
    // modbat/test/Child5.scala:12: modbat.test.Child5-1: skip

    m6.executeTransition(m6.getTransition(2))
    // modbat/test/Child5.scala:17: modbat.test.Child5-2: assert

    m5.executeTransition(m5.getTransition(2))
    // modbat/test/Child5.scala:17: modbat.test.Child5-1: assert

    val m4: MBT = MBT.getLaunchedModel(4)
    m4.executeTransition(m4.getTransition(0))
    // modbat/test/ComplexLaunch5.scala:15: modbat.test.ComplexLaunch5-5: skip

    m3.executeTransition(m3.getTransition(3))
    // modbat/test/ComplexLaunch5.scala:25: modbat.test.ComplexLaunch5-4: assert

    m4.executeTransition(m4.getTransition(1))
    // modbat/test/ComplexLaunch5.scala:16: modbat.test.ComplexLaunch5-5: launch

    m4.executeTransition(m4.getTransition(2))
    // modbat/test/ComplexLaunch5.scala:20: modbat.test.ComplexLaunch5-5: launch

    val m8: MBT = MBT.getLaunchedModel(8)
    m8.executeTransition(m8.getTransition(0))
    // modbat/test/ComplexLaunch5.scala:15: modbat.test.ComplexLaunch5-6: skip

    m8.executeTransition(m8.getTransition(2))
    // modbat/test/ComplexLaunch5.scala:20: modbat.test.ComplexLaunch5-6: launch

    val m9: MBT = MBT.getLaunchedModel(9)
    m9.executeTransition(m9.getTransition(0))
    // modbat/test/ComplexLaunch5.scala:15: modbat.test.ComplexLaunch5-7: skip

    m4.executeTransition(m4.getTransition(2))
    // modbat/test/ComplexLaunch5.scala:20: modbat.test.ComplexLaunch5-5: launch

    m9.executeTransition(m9.getTransition(3))
    // modbat/test/ComplexLaunch5.scala:25: modbat.test.ComplexLaunch5-7: assert

    m4.executeTransition(m4.getTransition(2))
    // modbat/test/ComplexLaunch5.scala:20: modbat.test.ComplexLaunch5-5: launch

    m8.executeTransition(m8.getTransition(1))
    // modbat/test/ComplexLaunch5.scala:16: modbat.test.ComplexLaunch5-6: launch

    val m12: MBT = MBT.getLaunchedModel(12)
    m12.executeTransition(m12.getTransition(0))
    // modbat/test/Child5.scala:12: modbat.test.Child5-4: skip

    m4.executeTransition(m4.getTransition(2))
    // modbat/test/ComplexLaunch5.scala:20: modbat.test.ComplexLaunch5-5: launch

    m4.executeTransition(m4.getTransition(2))
    // modbat/test/ComplexLaunch5.scala:20: modbat.test.ComplexLaunch5-5: launch

    m12.executeTransition(m12.getTransition(1))
    // modbat/test/Child5.scala:14: modbat.test.Child5-4: id

    val m13: MBT = MBT.getLaunchedModel(13)
    m13.executeTransition(m13.getTransition(0))
    // modbat/test/ComplexLaunch5.scala:15: modbat.test.ComplexLaunch5-10: skip

    m8.executeTransition(m8.getTransition(1))
    // modbat/test/ComplexLaunch5.scala:16: modbat.test.ComplexLaunch5-6: launch

    m4.executeTransition(m4.getTransition(2))
    // modbat/test/ComplexLaunch5.scala:20: modbat.test.ComplexLaunch5-5: launch

    m13.executeTransition(m13.getTransition(1))
    // modbat/test/ComplexLaunch5.scala:16: modbat.test.ComplexLaunch5-10: launch

    val m7: MBT = MBT.getLaunchedModel(7)
    m7.executeTransition(m7.getTransition(0))
    // modbat/test/Child5.scala:12: modbat.test.Child5-3: skip

    m8.setExpectedException("java.lang.AssertionError")
    m8.executeTransition(m8.getTransition(3))
    // modbat/test/ComplexLaunch5.scala:25: modbat.test.ComplexLaunch5-6: assert
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
    MBT.loadModelClass ("modbat.test.ComplexLaunch5")
    MBT.invokeAnnotatedStaticMethods(classOf[Init], null)

    test402b73cd0066eaea()

    MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
  }
}
