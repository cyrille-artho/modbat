package modbat.mbt

import org.scalatest._

class ThrowTest extends FlatSpec with Matchers {
  "ThrowTest1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--loop-limit=5","modbat.test.ThrowTest"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}