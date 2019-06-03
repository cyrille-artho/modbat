package modbat.mbt

import org.scalatest._

class MaybeElseTest extends FlatSpec with Matchers {
  "MaybeElseTest1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","modbat.test.MaybeElseTest","--maybe-probability=0.0","--no-redirect-out"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "MaybeElseTest2" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","modbat.test.MaybeElseTest","--maybe-probability=1.0","--no-redirect-out"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "MaybeElseTest3" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","modbat.test.MaybeElseTest2","--maybe-probability=0.0","--no-redirect-out"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "MaybeElseTest4" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","modbat.test.MaybeElseTest2","--maybe-probability=1.0","--no-redirect-out"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}