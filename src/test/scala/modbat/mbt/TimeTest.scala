package modbat.mbt

import org.scalatest._

class TimeTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "Time1" should "pass with one transition" in { td =>
    val result = ModbatTestHarness.test(Array("-s=1", "-n=1", "--no-redirect-out", "modbat.test.Time1"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "Time2" should "pass with three transitions" in { td =>
    val result = ModbatTestHarness.test(Array("-s=1", "-n=1", "--no-redirect-out", "modbat.test.Time2"), (() => ModbatTestHarness.setTestJar()), td)
  }
}
