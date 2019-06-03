package modbat.mbt

import org.scalatest._

class CounterPosNeg extends FlatSpec with Matchers {
  "CounterPosNeg1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("--mode=dot","--auto-labels","modbat.examples.CounterPosNeg"), ModbatTestHarness.setExamplesJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}