package modbat.mbt

import org.scalatest._

class Child extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "Child1" should "fail" in { td =>
    val result = ModbatTestHarness.testMain(Array("--mode=dot","modbat.test.Child"), ModbatTestHarness.setTestJar, td)
    result._1 should be(1)
    result._3 shouldBe empty
  }



}
