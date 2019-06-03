package modbat.mbt

import org.scalatest._

class TickTockTest extends FlatSpec with Matchers {
  "TickTockTest1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--no-redirect-out","--log-level=fine","modbat.test.TickTockTest"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "TickTockTest2" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--no-redirect-out","--show-choices","modbat.test.TickTockTest"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}