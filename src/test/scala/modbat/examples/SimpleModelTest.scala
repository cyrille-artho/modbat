package modbat.examples

import org.scalatest._
import modbat.mbt.ModbatTestHarness

class SimpleModelTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers { "SimpleModelTest1" should "pass" in { td =>
    ModbatTestHarness.test(Array("-s=1", "-n=30", "--no-redirect-out", "--log-level=fine", "modbat.examples.SimpleModel"), (() => ModbatTestHarness.setExamplesJar()), td)
  }

  "SimpleModelTest2" should "pass" in { td =>
    ModbatTestHarness.test(Array("-s=49a846e52b813972", "-n=1", "--no-redirect-out", "modbat.examples.SimpleModel"), (() => ModbatTestHarness.setExamplesJar()), td)
  }

  "SimpleModelTest3" should "pass" in { td =>
    ModbatTestHarness.test(Array("-s=88af43883571af0c", "-n=1", "--no-redirect-out", "modbat.examples.SimpleModel"), (() => ModbatTestHarness.setExamplesJar()), td)
  }

  "SimpleModelTest4" should "pass" in { td =>
    ModbatTestHarness.test(Array("-s=88af43883571af0c", "-n=1", "--no-redirect-out", "modbat.examples.SimpleModel", "--print-stack-trace"), (() => ModbatTestHarness.setExamplesJar()), td)
  }
}
