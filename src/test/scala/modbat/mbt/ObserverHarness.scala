package modbat.mbt

import org.scalatest._

class ObserverHarness extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "ObserverHarness1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--no-redirect-out","--log-level=fine","modbat.test.ObserverHarness"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "ObserverHarness2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--no-redirect-out","--log-level=fine","modbat.test.ObserverHarness2"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "ObserverHarness3" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--no-redirect-out","--log-level=fine","modbat.test.ObserverHarness3"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "ObserverHarness4" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--no-redirect-out","--log-level=fine","modbat.test.ObserverHarness4"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "ObserverHarness5" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--no-redirect-out","--log-level=fine","modbat.test.ObserverHarness5"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "ObserverHarness6" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--no-redirect-out","--log-level=fine","modbat.test.ObserverHarness6"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }

  "ObserverHarness7" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--no-redirect-out","--log-level=fine","modbat.test.ObserverHarness7"), ModbatTestHarness.setTestJar, td)
    result._2 should not be empty
  }
}
