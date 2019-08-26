package modbat.mbt

import org.scalatest._

class ExcTestA extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "ExcTestA1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.ExcTestA3"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "ExcTestA2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.ExcTestA4"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "ExcTestA3" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.ExcTestA5"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "ExcTestA4" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.ExcTestA5","--log-level=fine"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }
}
