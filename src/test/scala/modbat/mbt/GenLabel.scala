package modbat.mbt

import org.scalatest._

class GenLabel extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "GenLabel1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=5","--no-redirect-out","modbat.test.GenLabel"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "GenLabel2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=5","--auto-labels","--no-redirect-out","modbat.test.GenLabel"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "GenLabel3" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=5","--auto-labels","--no-redirect-out","modbat.test.GenLabel2"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "GenLabel4" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=5","--auto-labels","--no-redirect-out","modbat.test.GenLabel3"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "GenLabel5" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("--mode=dot","--auto-labels","modbat.test.GenLabel3"), ModbatTestHarness.setTestJar, td, Some("modbat.test.GenLabel3.dot","genlabel3.dot"))
    result._2 shouldBe empty
  }
}
