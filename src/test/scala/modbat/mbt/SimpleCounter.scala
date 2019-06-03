package modbat.mbt

import org.scalatest._

class SimpleCounter extends FlatSpec with Matchers {
  "SimpleCounter1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","--no-redirect-out","modbat.examples.SimpleCounter"), ModbatTestHarness.setExamplesJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}