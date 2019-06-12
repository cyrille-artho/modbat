package modbat.mbt

import org.scalatest._

class AnyFuncTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "AnyFuncTest1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=20","--no-redirect-out","modbat.test.AnyFuncTest"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "AnyFuncTest2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=4","--no-redirect-out","--show-choices","modbat.test.AnyFuncTest"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "AnyFuncTest3" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("--mode=dot","--show-choices","modbat.test.AnyFuncTest"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}