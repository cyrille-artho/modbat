package modbat.mbt

import org.scalatest._

class InvCheck extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "InvCheck1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=20","--no-redirect-out","modbat.test.InvCheck2"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "InvCheck2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=10","--loop-limit=2","--no-redirect-out","modbat.test.InvCheck3"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "InvCheck3" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=20","--loop-limit=2","--no-redirect-out","modbat.test.InvCheck3_1"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "InvCheck4" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=20","--loop-limit=2","--no-redirect-out","modbat.test.InvCheck3_2"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "InvCheck5" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=20","--loop-limit=2","--no-redirect-out","modbat.test.InvCheck3_3"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "InvCheck6" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=10","--loop-limit=2","--no-redirect-out","--log-level=fine","modbat.test.InvCheck4"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "InvCheck7" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=20","--auto-labels","--no-redirect-out","modbat.test.InvCheck2"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "InvCheck8" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=20","--auto-labels","--no-redirect-out","modbat.test.InvCheck5"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "InvCheck9" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=20","--no-redirect-out","modbat.test.InvCheck5"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "InvCheck10" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=10","--auto-labels","--loop-limit=2","--no-redirect-out","modbat.test.InvCheck3"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "InvCheck11" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=10","--auto-labels","--loop-limit=2","--no-redirect-out","--log-level=fine","modbat.test.InvCheck4"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }
}
