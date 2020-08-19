package modbat.examples

import org.scalatest._
import modbat.mbt.ModbatTestHarness

class JavaNioServerSocketTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {

  "JavaNioServerSocketTest1" should "pass" in { td =>
    ModbatTestHarness.test(Array("-n=5", "-s=1", "--no-redirect-out", "--log-level=fine", "modbat.examples.JavaNioServerSocket"), (() => ModbatTestHarness.setExamplesJar()), td)
  }

  "JavaNioServerSocketTest2" should "pass" in { td =>
    ModbatTestHarness.test(Array("-n=10", "-s=1", "--no-redirect-out", "--log-level=fine", "modbat.examples.JavaNioServerSocket"), (() => ModbatTestHarness.setExamplesJar()), td)
  }

  "JavaNioServerSocketTest3" should "pass" in { td =>
    ModbatTestHarness.test(Array("-n=20", "-s=1", "--no-redirect-out", "--log-level=fine", "modbat.examples.JavaNioServerSocket"), (() => ModbatTestHarness.setExamplesJar()), td)
  }

  "JavaNioServerSocketTest4" should "pass" in { td =>
    ModbatTestHarness.test(Array("-n=50", "-s=1", "--no-redirect-out", "--log-level=fine", "modbat.examples.JavaNioServerSocket"), (() => ModbatTestHarness.setExamplesJar()), td)
  }

  "JavaNioServerSocketTest5" should "pass" in { td =>
    ModbatTestHarness.test(Array("-n=100", "-s=1", "--no-redirect-out", "--log-level=fine", "modbat.examples.JavaNioServerSocket"), (() => ModbatTestHarness.setExamplesJar()), td)
  }
}
