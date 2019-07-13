package modbat.test

import modbat.dsl.After;
import modbat.dsl.Before;
import modbat.dsl.Init;
import modbat.dsl.Shutdown;
import modbat.mbt.MBT
import modbat.log.Log
import modbat.offline.ReplayRandom

object TestSimpleFileModel {
  def test41530f4040856586() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x41530f4040856586L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-1: skip

    MBT.setRNG (new ReplayRandom(0x41530f4040856586L, Array(0x365c65d2), 0))
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/SimpleFileModel.scala:34: modbat.test.SimpleFileModel-1: launch

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-1: require

    MBT.setRNG (new ReplayRandom(0x41530f4040856586L, Array(0x0, 0x6c12f7d), 0))
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-1: launch

    m0.executeTransition(m0.getTransition(4))
    // modbat/test/SimpleFileModel.scala:49: modbat.test.SimpleFileModel-1: skip

    m0.executeTransition(m0.getTransition(5))
    // modbat/test/SimpleFileModel.scala:50: modbat.test.SimpleFileModel-1: println

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-2: skip

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-2: require

    MBT.setRNG (new ReplayRandom(0x41530f4040856586L, Array(0x1, 0xeeb59e4), 0))
    m1.executeTransition(m1.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-2: launch

    m0.executeTransition(m0.getTransition(5))
    // modbat/test/SimpleFileModel.scala:50: modbat.test.SimpleFileModel-1: println

    val m3: MBT = MBT.getLaunchedModel(3)
    m3.executeTransition(m3.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-4: skip

    val m2: MBT = MBT.getLaunchedModel(2)
    m2.executeTransition(m2.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-3: skip

    m3.executeTransition(m3.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-4: require

    m2.executeTransition(m2.getTransition(4))
    // modbat/test/SimpleFileModel.scala:49: modbat.test.SimpleFileModel-3: skip

    MBT.setRNG (new ReplayRandom(0x41530f4040856586L, Array(0x1, 0x3c2a0a55), 0))
    m3.executeTransition(m3.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-4: launch

    m0.executeTransition(m0.getTransition(5))
    // modbat/test/SimpleFileModel.scala:50: modbat.test.SimpleFileModel-1: println

    m1.executeTransition(m1.getTransition(4))
    // modbat/test/SimpleFileModel.scala:49: modbat.test.SimpleFileModel-2: skip

    val m4: MBT = MBT.getLaunchedModel(4)
    m4.executeTransition(m4.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-5: skip

    m4.setExpectedException("java.lang.AssertionError")
    m4.executeTransition(m4.getTransition(1))
    // modbat/test/SimpleFileModel.scala:37: modbat.test.SimpleFileModel-5: launch
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test27fe2d950fc74b21() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-1: skip

    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(0x3ca69dfb), 0))
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/SimpleFileModel.scala:34: modbat.test.SimpleFileModel-1: launch

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-1: require

    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(0x0, 0x3fac253f), 0))
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-1: launch

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-1: require

    val m2: MBT = MBT.getLaunchedModel(2)
    m2.executeTransition(m2.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-3: skip

    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(0x52b7db34), 0))
    m2.executeTransition(m2.getTransition(1))
    // modbat/test/SimpleFileModel.scala:34: modbat.test.SimpleFileModel-3: launch

    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(0x0, 0xbc0c690), 0))
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-1: launch

    val m3: MBT = MBT.getLaunchedModel(3)
    m3.executeTransition(m3.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-4: skip

    m2.executeTransition(m2.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-3: require

    m3.executeTransition(m3.getTransition(4))
    // modbat/test/SimpleFileModel.scala:49: modbat.test.SimpleFileModel-4: skip

    m3.executeTransition(m3.getTransition(5))
    // modbat/test/SimpleFileModel.scala:50: modbat.test.SimpleFileModel-4: println

    m3.executeTransition(m3.getTransition(6))
    // modbat/test/SimpleFileModel.scala:64: modbat.test.SimpleFileModel-4: skip

    m0.executeTransition(m0.getTransition(4))
    // modbat/test/SimpleFileModel.scala:49: modbat.test.SimpleFileModel-1: skip

    m0.executeTransition(m0.getTransition(5))
    // modbat/test/SimpleFileModel.scala:50: modbat.test.SimpleFileModel-1: println

    m0.executeTransition(m0.getTransition(6))
    // modbat/test/SimpleFileModel.scala:64: modbat.test.SimpleFileModel-1: skip

    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(0x1, 0x609eea70), 0))
    m2.executeTransition(m2.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-3: launch

    m2.executeTransition(m2.getTransition(4))
    // modbat/test/SimpleFileModel.scala:49: modbat.test.SimpleFileModel-3: skip

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-2: skip

    m2.executeTransition(m2.getTransition(6))
    // modbat/test/SimpleFileModel.scala:64: modbat.test.SimpleFileModel-3: skip

    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(0x18f17337), 0))
    m1.executeTransition(m1.getTransition(1))
    // modbat/test/SimpleFileModel.scala:34: modbat.test.SimpleFileModel-2: launch

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-2: require

    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(0x3, 0x693d873), 0))
    m1.executeTransition(m1.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-2: launch

    val m5: MBT = MBT.getLaunchedModel(5)
    m5.executeTransition(m5.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-6: skip

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-2: require

    m5.executeTransition(m5.getTransition(4))
    // modbat/test/SimpleFileModel.scala:49: modbat.test.SimpleFileModel-6: skip

    val m4: MBT = MBT.getLaunchedModel(4)
    m4.executeTransition(m4.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-5: skip

    m5.executeTransition(m5.getTransition(6))
    // modbat/test/SimpleFileModel.scala:64: modbat.test.SimpleFileModel-6: skip

    m4.executeTransition(m4.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-5: require

    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(0x3, 0x7fbc9b0c), 0))
    m1.executeTransition(m1.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-2: launch

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-2: require

    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(0x0, 0x1ee0b5c5), 0))
    m4.executeTransition(m4.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-5: launch

    val m6: MBT = MBT.getLaunchedModel(6)
    m6.executeTransition(m6.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-7: skip

    m4.executeTransition(m4.getTransition(4))
    // modbat/test/SimpleFileModel.scala:49: modbat.test.SimpleFileModel-5: skip

    m4.executeTransition(m4.getTransition(6))
    // modbat/test/SimpleFileModel.scala:64: modbat.test.SimpleFileModel-5: skip

    m6.executeTransition(m6.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-7: require

    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(0x0, 0x72d601dc), 0))
    m6.executeTransition(m6.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-7: launch

    m6.executeTransition(m6.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-7: require

    MBT.setRNG (new ReplayRandom(0x27fe2d950fc74b21L, Array(0x2, 0xde4334), 0))
    m1.executeTransition(m1.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-2: launch

    m1.executeTransition(m1.getTransition(4))
    // modbat/test/SimpleFileModel.scala:49: modbat.test.SimpleFileModel-2: skip

    val m7: MBT = MBT.getLaunchedModel(7)
    m7.executeTransition(m7.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-8: skip

    m1.executeTransition(m1.getTransition(6))
    // modbat/test/SimpleFileModel.scala:64: modbat.test.SimpleFileModel-2: skip

    m7.executeTransition(m7.getTransition(4))
    // modbat/test/SimpleFileModel.scala:49: modbat.test.SimpleFileModel-8: skip

    m7.setExpectedException("java.io.IOException")
    m7.executeTransition(m7.getTransition(5))
    // modbat/test/SimpleFileModel.scala:54: modbat.test.SimpleFileModel-8: println
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test85780661bf76533() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x85780661bf76533L, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-1: skip

    MBT.setRNG (new ReplayRandom(0x85780661bf76533L, Array(0x6c12f7d), 0))
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/SimpleFileModel.scala:34: modbat.test.SimpleFileModel-1: launch

    m0.executeTransition(m0.getTransition(4))
    // modbat/test/SimpleFileModel.scala:49: modbat.test.SimpleFileModel-1: skip

    m0.executeTransition(m0.getTransition(5))
    // modbat/test/SimpleFileModel.scala:50: modbat.test.SimpleFileModel-1: println

    m0.executeTransition(m0.getTransition(6))
    // modbat/test/SimpleFileModel.scala:64: modbat.test.SimpleFileModel-1: skip

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-2: skip

    m1.executeTransition(m1.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-2: require

    MBT.setRNG (new ReplayRandom(0x85780661bf76533L, Array(0x0, 0xbc0c690), 0))
    m1.executeTransition(m1.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-2: launch

    val m2: MBT = MBT.getLaunchedModel(2)
    m2.executeTransition(m2.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-3: skip

    m2.setExpectedException("java.lang.AssertionError")
    m2.executeTransition(m2.getTransition(1))
    // modbat/test/SimpleFileModel.scala:37: modbat.test.SimpleFileModel-3: launch
    MBT.setTestFailed(true)
    MBT.cleanup()

  }

  def test1690fa4e0c732f7d() {
    MBT.clearLaunchedModels()
    MBT.setRNG (new ReplayRandom(0x1690fa4e0c732f7dL, Array(), 0))
    val m0: MBT = MBT.launch(null)

    m0.executeTransition(m0.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-1: skip

    MBT.setRNG (new ReplayRandom(0x1690fa4e0c732f7dL, Array(0x1ed17a5a), 0))
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/SimpleFileModel.scala:34: modbat.test.SimpleFileModel-1: launch

    val m1: MBT = MBT.getLaunchedModel(1)
    m1.executeTransition(m1.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-2: skip

    MBT.setRNG (new ReplayRandom(0x1690fa4e0c732f7dL, Array(0x52b7db34), 0))
    m1.executeTransition(m1.getTransition(1))
    // modbat/test/SimpleFileModel.scala:34: modbat.test.SimpleFileModel-2: launch

    MBT.setRNG (new ReplayRandom(0x1690fa4e0c732f7dL, Array(0x4e37a686), 0))
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/SimpleFileModel.scala:34: modbat.test.SimpleFileModel-1: launch

    m1.executeTransition(m1.getTransition(4))
    // modbat/test/SimpleFileModel.scala:49: modbat.test.SimpleFileModel-2: skip

    m1.executeTransition(m1.getTransition(5))
    // modbat/test/SimpleFileModel.scala:50: modbat.test.SimpleFileModel-2: println

    m1.executeTransition(m1.getTransition(6))
    // modbat/test/SimpleFileModel.scala:64: modbat.test.SimpleFileModel-2: skip

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-1: require

    MBT.setRNG (new ReplayRandom(0x1690fa4e0c732f7dL, Array(0x3, 0x2cc0e38a), 0))
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-1: launch

    MBT.setRNG (new ReplayRandom(0x1690fa4e0c732f7dL, Array(0x9955186), 0))
    m0.executeTransition(m0.getTransition(1))
    // modbat/test/SimpleFileModel.scala:34: modbat.test.SimpleFileModel-1: launch

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-1: require

    val m3: MBT = MBT.getLaunchedModel(3)
    m3.executeTransition(m3.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-4: skip

    m3.executeTransition(m3.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-4: require

    MBT.setRNG (new ReplayRandom(0x1690fa4e0c732f7dL, Array(0x3, 0x15e7ace2), 0))
    m3.executeTransition(m3.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-4: launch

    val m4: MBT = MBT.getLaunchedModel(4)
    m4.executeTransition(m4.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-5: skip

    val m2: MBT = MBT.getLaunchedModel(2)
    m2.executeTransition(m2.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-3: skip

    m3.executeTransition(m3.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-4: require

    m4.executeTransition(m4.getTransition(4))
    // modbat/test/SimpleFileModel.scala:49: modbat.test.SimpleFileModel-5: skip

    MBT.setRNG (new ReplayRandom(0x1690fa4e0c732f7dL, Array(0x4, 0xc2d4bbc), 0))
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-1: launch

    m4.executeTransition(m4.getTransition(5))
    // modbat/test/SimpleFileModel.scala:50: modbat.test.SimpleFileModel-5: println

    val m5: MBT = MBT.getLaunchedModel(5)
    m5.executeTransition(m5.getTransition(0))
    // modbat/test/SimpleFileModel.scala:33: modbat.test.SimpleFileModel-6: skip

    m2.executeTransition(m2.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-3: require

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-1: require

    m4.executeTransition(m4.getTransition(6))
    // modbat/test/SimpleFileModel.scala:64: modbat.test.SimpleFileModel-5: skip

    MBT.setRNG (new ReplayRandom(0x1690fa4e0c732f7dL, Array(0x0, 0x2cd6fcbc), 0))
    m2.executeTransition(m2.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-3: launch

    MBT.setRNG (new ReplayRandom(0x1690fa4e0c732f7dL, Array(0x2, 0x7e8eed32), 0))
    m3.executeTransition(m3.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-4: launch

    m2.executeTransition(m2.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-3: require

    MBT.setRNG (new ReplayRandom(0x1690fa4e0c732f7dL, Array(0x2, 0x7734b0ce), 0))
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-1: launch

    m5.executeTransition(m5.getTransition(4))
    // modbat/test/SimpleFileModel.scala:49: modbat.test.SimpleFileModel-6: skip

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-1: require

    m3.executeTransition(m3.getTransition(4))
    // modbat/test/SimpleFileModel.scala:49: modbat.test.SimpleFileModel-4: skip

    m5.executeTransition(m5.getTransition(6))
    // modbat/test/SimpleFileModel.scala:64: modbat.test.SimpleFileModel-6: skip

    MBT.setRNG (new ReplayRandom(0x1690fa4e0c732f7dL, Array(0x0, 0x2a9ea160), 0))
    m0.executeTransition(m0.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-1: launch

    MBT.setRNG (new ReplayRandom(0x1690fa4e0c732f7dL, Array(0x2, 0x1da67752), 0))
    m2.executeTransition(m2.getTransition(3))
    // modbat/test/SimpleFileModel.scala:45: modbat.test.SimpleFileModel-3: launch

    m0.executeTransition(m0.getTransition(2))
    // modbat/test/SimpleFileModel.scala:43: modbat.test.SimpleFileModel-1: require

    m3.setExpectedException("java.io.IOException")
    m3.executeTransition(m3.getTransition(5))
    // modbat/test/SimpleFileModel.scala:54: modbat.test.SimpleFileModel-4: println
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
    MBT.loadModelClass ("modbat.test.SimpleFileModel")
    MBT.invokeAnnotatedStaticMethods(classOf[Init], null)

    test41530f4040856586()
    test27fe2d950fc74b21()
    test85780661bf76533()
    test1690fa4e0c732f7d()

    MBT.invokeAnnotatedStaticMethods(classOf[Shutdown], null)
  }
}
