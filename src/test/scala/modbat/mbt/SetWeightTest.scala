package modbat.mbt

import org.scalatest._

class SetWeightTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "SetWeight1" should "pass with one transition" in { td =>
    val result = ModbatTestHarness.test(Array("-s=1", "-n=1", "--no-redirect-out",  "modbat.test.SetWeight1"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "SetWeight2" should "pass with two transitions" in { td =>
    val result = ModbatTestHarness.test(Array("-s=1", "-n=1", "--no-redirect-out", "modbat.test.SetWeight2"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "SetWeight3" should "pass with three transitions" in { td =>
    val result = ModbatTestHarness.test(Array("-s=1", "-n=1", "--no-redirect-out", "modbat.test.SetWeight3"), (() => ModbatTestHarness.setTestJar()), td)
  }
}

