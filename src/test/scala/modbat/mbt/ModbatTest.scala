package modbat.mbt

import org.scalatest._

class ModbatTest extends FlatSpec with Matchers with AppendedClues {
  "A missing model class" should "cause Modbat to fail" in {
    ModbatTestHarness.test(Array("--model-class=x"), (() => ModbatTestHarness.setTestJar()), 1)
  }

  "A missing model class 2" should "cause Modbat to fail" in {
    ModbatTestHarness.test(Array("a", "b", "c"), (() => ModbatTestHarness.setTestJar()), 1)
  }

  "A missing model class 3" should "cause Modbat to fail" in {
    ModbatTestHarness.test(Array("-n=1", "x", "b", "c"), (() => ModbatTestHarness.setTestJar()), 1)
  }

  "A missing model class 4" should "cause Modbat to fail" in {
    ModbatTestHarness.test(Array("x", "y", "-n=1"), (() => ModbatTestHarness.setTestJar()), 1)
  }

  "A missing model class 5" should "cause Modbat to fail" in {
    ModbatTestHarness.test(Array("-n=2", "--", "-n=1"), (() => ModbatTestHarness.setTestJar()), 1)
  }

  "A missing model class 6" should "cause Modbat to fail" in {
    ModbatTestHarness.test(Array("a", "-n=2", "--", "-n=1"), (() => ModbatTestHarness.setTestJar()), 1)
  }

  "A missing model class 7" should "cause Modbat to fail" in {
    ModbatTestHarness.test(Array("-s=0"), (() => ModbatTestHarness.setTestJar()), 1)
  }

  "A missing model class 8" should "cause Modbat to fail" in {
    ModbatTestHarness.test(Array("--log-level=fine"), (() => ModbatTestHarness.setTestJar()), 1)
  }
}
