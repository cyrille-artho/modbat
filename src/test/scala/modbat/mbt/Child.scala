package modbat.mbt

import org.scalatest._

class Child extends FlatSpec with Matchers {
  "Child1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("--mode=dot","modbat.test.Child"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}