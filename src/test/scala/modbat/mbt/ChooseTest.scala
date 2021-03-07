package modbat.mbt

import org.scalatest._

class ChooseTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "Choose00" should "pass with one transition" in { td =>
    val result = ModbatTestHarness.test(Array("-s=1", "-n=1", "--no-redirect-out", "modbat.test.Choose00"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "Choose01" should "pass with 2 nodes (states), 2 edges (transitions), 1 edge-pair" in { td =>
    // note that edge-pair coverage option is activated by using --dotify-path-coverage option
    // here, dotDir does exist
    ModbatTestHarness.test(
      Array("-n=1", "-s=7", "--dotify-path-coverage", "--no-redirect-out", "modbat.test.ChooseTest"),
      () => ModbatTestHarness.setTestJar(),
      td
    )
  }
}
