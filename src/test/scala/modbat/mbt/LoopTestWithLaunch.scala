package modbat.mbt

import org.scalatest._

class LoopTestWithLaunch extends FlatSpec with Matchers {
  "LoopTestWithLaunch1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--log-level=fine","--no-redirect-out","--loop-limit=3","modbat.test.LoopTestWithLaunch"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "LoopTestWithLaunch2" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=20","--no-redirect-out","--loop-limit=4","modbat.test.LoopTestWithLaunch"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}