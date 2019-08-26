package modbat.mbt

import org.scalatest._

class ChooseTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "ChooseTest1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=100","modbat.test.ChooseTest"), ModbatTestHarness.setTestJar, td)
    result._2 shouldBe empty
  }

  "ChooseTest2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("--mode=dot","--show-choices","modbat.test.ChooseTest"), ModbatTestHarness.setTestJar, td, Some("modbat.test.ChooseTest.dot","chooseTest.dot"))
    result._2 shouldBe empty
  }
}
