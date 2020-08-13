package modbat.mbt

import org.scalatest._

class ChooseTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "Choose00" should "pass with one transition" in { td =>
    val result = ModbatTestHarness.test(Array("-s=1", "-n=1", "--no-redirect-out", "modbat.test.Choose00"), (() => ModbatTestHarness.setTestJar()), td)
  }
}
