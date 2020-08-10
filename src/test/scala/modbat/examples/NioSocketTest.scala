package modbat.examples

import org.scalatest._
import modbat.mbt.ModbatTestHarness

class NioSocketTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "NioSocket1Test1" should "pass" in { td =>
    ModbatTestHarness.test(Array("-n=5", "-s=1", "--no-redirect-out", "--log-level=fine", "--no-init", "--no-shutdown", "modbat.examples.NioSocket1"), (() => ModbatTestHarness.setExamplesJar()), td)
  }

  "NioSocket1Test2" should "pass" in { td =>
    ModbatTestHarness.test(Array("-n=10", "-s=1", "--no-redirect-out", "--log-level=fine", "--no-init", "--no-shutdown", "modbat.examples.NioSocket1"), (() => ModbatTestHarness.setExamplesJar()), td)
  }

  "NioSocket1Test3" should "pass" in { td =>
    ModbatTestHarness.test(Array("-n=20", "-s=1", "--no-redirect-out", "--log-level=fine", "--no-init", "--no-shutdown", "modbat.examples.NioSocket1"), (() => ModbatTestHarness.setExamplesJar()), td)
  }

  "NioSocket1Test4" should "pass" in { td =>
    ModbatTestHarness.test(Array("-n=50", "-s=1", "--no-redirect-out", "--log-level=fine", "--no-init", "--no-shutdown", "modbat.examples.NioSocket1"), (() => ModbatTestHarness.setExamplesJar()), td)
  }
}
