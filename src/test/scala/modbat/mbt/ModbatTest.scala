package modbat.mbt

import org.scalatest._

class ModbatTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "A missing model class" should "cause Modbat to fail" in { td =>
    ModbatTestHarness.test(Array("--model-class=x"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "A missing model class 2" should "cause Modbat to fail" in { td =>
    ModbatTestHarness.test(Array("a", "b", "c"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "A missing model class 3" should "cause Modbat to fail" in { td =>
    ModbatTestHarness.test(Array("-n=1", "x", "b", "c"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "A missing model class 4" should "cause Modbat to fail" in { td =>
    ModbatTestHarness.test(Array("x", "y", "-n=1"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "A missing model class 5" should "cause Modbat to fail" in { td =>
    ModbatTestHarness.test(Array("-n=2", "--", "-n=1"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "A missing model class 6" should "cause Modbat to fail" in { td =>
    ModbatTestHarness.test(Array("a", "-n=2", "--", "-n=1"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "A missing model class 7" should "cause Modbat to fail" in { td =>
    ModbatTestHarness.test(Array("-s=0"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "A missing model class 8" should "cause Modbat to fail" in { td =>
    ModbatTestHarness.test(Array("--log-level=fine"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "A class that is not a model class" should "cause Modbat to fail" in { td =>
    ModbatTestHarness.test(Array("-s=1", "--no-redirect-out", "modbat.examples.SimpleCounter"), (() => ModbatTestHarness.setExamplesJar()), td)
  }

  "An empty model" should "cause Modbat to fail" in { td =>
    ModbatTestHarness.test(Array("-s=1", "--no-redirect-out", "modbat.test.EmptyModel"), (() => ModbatTestHarness.setTestJar()), td)
  }

  "An abstract class for the model" should "cause Modbat to fail" in { td =>
    ModbatTestHarness.test(Array("-s=1", "--no-redirect-out", "modbat.test.AbstractCls"), (() => ModbatTestHarness.setTestJar()), td)
  }
}
