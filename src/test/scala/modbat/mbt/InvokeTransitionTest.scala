package modbat.mbt

import org.scalatest._

class InvokeTransitionTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "InvokeTransition1" should "pass with one transition" in { td =>
    val result = ModbatTestHarness.test(Array("-s=1", "-n=1", "--no-redirect-out", "-s",  "modbat.test.InvokeTransition1"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "InvokeTransition2" should "pass with three transitions" in { td =>
    val result = ModbatTestHarness.test(Array("-s=1", "-n=1", "--no-redirect-out", "-s",  "modbat.test.InvokeTransition2"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "InvokeTransition3" should "pass with two transitions" in { td =>
    ModbatTestHarness.test(Array("-s=1", "-n=1", "--no-redirect-out", "-s",  "modbat.test.InvokeTransition3"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "InvokeTransition4" should "pass with n==3" in { td =>
    val result = ModbatTestHarness.test(Array("-s=1", "-n=1", "--no-redirect-out", "-s",  "modbat.test.InvokeTransition4"), (() => ModbatTestHarness.setTestJar()), td)
  }
}

