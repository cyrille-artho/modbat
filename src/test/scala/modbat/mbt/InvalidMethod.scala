package modbat.mbt

import org.scalatest._

class InvalidMethod extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "InvalidMethod1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("--mode=dot","modbat.test.InvalidMethod"), ModbatTestHarness.setTestJar, td, Some("modbat.test.InvalidMethod.dot","invmethod.dot"))
    result._1 should be(0)
    result._3 should not be empty
  }



  "InvalidMethod2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=15","--log-level=fine","--no-redirect-out","modbat.test.InvalidMethod"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "InvalidMethod3" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("--mode=dot","--auto-labels","modbat.test.InvalidMethod"), ModbatTestHarness.setTestJar, td, Some("modbat.test.InvalidMethod.dot","invmethod-auto.dot"))
    result._1 should be(0)
    result._3 should not be empty
  }



}

