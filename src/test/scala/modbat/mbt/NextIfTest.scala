package modbat.mbt

import org.scalatest._

class NextIfTest extends FlatSpec with Matchers {
  "NextIfTest1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","modbat.test.NextIfTest","--no-redirect-out"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NextIfTest2" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.NextIfTest2"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NextIfTest3" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.NextIfTest3"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NextIfTest4" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.NextIfTest4"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NextIfTest5" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.NextIfTest5"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}