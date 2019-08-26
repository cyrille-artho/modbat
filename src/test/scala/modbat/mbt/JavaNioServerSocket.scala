package modbat.mbt

import org.scalatest._

class JavaNioServerSocket extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "JavaNioServerSocket1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=5","-s=1","--no-redirect-out","--log-level=fine","modbat.examples.JavaNioServerSocket"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }

  "JavaNioServerSocket2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=10","-s=1","--no-redirect-out","--log-level=fine","modbat.examples.JavaNioServerSocket"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }

  "JavaNioServerSocket3" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=20","-s=1","--no-redirect-out","--log-level=fine","modbat.examples.JavaNioServerSocket"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }

  "JavaNioServerSocket4" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=50","-s=1","--no-redirect-out","--log-level=fine","modbat.examples.JavaNioServerSocket"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }

  "JavaNioServerSocket5" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=100","-s=1","--no-redirect-out","--log-level=fine","modbat.examples.JavaNioServerSocket"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }

  "JavaNioServerSocket6" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=200","-s=1","--no-redirect-out","--log-level=fine","modbat.examples.JavaNioServerSocket"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }

  "JavaNioServerSocket7" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=500","-s=1","--no-redirect-out","--log-level=fine","modbat.examples.JavaNioServerSocket"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }

  "JavaNioServerSocket8" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=1000","-s=1","--no-redirect-out","--log-level=fine","modbat.examples.JavaNioServerSocket"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }

  "JavaNioServerSocket9" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("--mode=dot","--auto-labels","modbat.examples.JavaNioServerSocket"), ModbatTestHarness.setExamplesJar, td, Some("modbat.examples.JavaNioServerSocket.dot","nioserv-auto.dot"))
    result._1 should be(0)
    result._3 shouldBe empty
  }

  "JavaNioServerSocket10" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("--mode=dot","--log-level=debug","--auto-labels","modbat.examples.JavaNioServerSocket"), ModbatTestHarness.setExamplesJar, td, Some("modbat.examples.JavaNioServerSocket.dot","nioserv-auto.dot"))
    result._1 should be(0)
    result._3 shouldBe empty
  }

  "JavaNioServerSocket11" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("--mode=dot","modbat.examples.JavaNioServerSocket3"), ModbatTestHarness.setExamplesJar, td, Some("modbat.examples.JavaNioServerSocket3.dot","nioserv3.dot"))
    result._1 should be(0)
    result._3 shouldBe empty
  }

  "JavaNioServerSocket12" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("--mode=dot","--show-choices","modbat.examples.JavaNioServerSocket3"), ModbatTestHarness.setExamplesJar, td, Some("modbat.examples.JavaNioServerSocket3.dot","nioserv3c.dot"))
    result._1 should be(0)
    result._3 shouldBe empty
  }

  "JavaNioServerSocket13" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=10","-s=1","--dotify-coverage","--auto-labels","modbat.examples.JavaNioServerSocket"), ModbatTestHarness.setExamplesJar, td, Some("modbat.examples.JavaNioServerSocket.dot","scov_10.dot"))
    result._1 should be(0)
    result._3 shouldBe empty
  }

  "JavaNioServerSocket14" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=100","-s=1","--dotify-coverage","--auto-labels","modbat.examples.JavaNioServerSocket"), ModbatTestHarness.setExamplesJar, td, Some("modbat.examples.JavaNioServerSocket.dot","scov_100.dot"))
    result._1 should be(0)
    result._3 shouldBe empty
  }

  "JavaNioServerSocket15" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=1","-s=321e7808","--no-redirect-out","modbat.examples.JavaNioServerSocket"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }

  "JavaNioServerSocket16" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=1000","-s=1","--no-redirect-out","modbat.examples.JavaNioServerSocket2"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }

  "JavaNioServerSocket17" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=10","-s=1","--no-redirect-out","--loop-limit=5","modbat.examples.JavaNioServerSocket3"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }

  "JavaNioServerSocket18" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=100","-s=1","--no-redirect-out","--loop-limit=5","modbat.examples.JavaNioServerSocket3"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }

  "JavaNioServerSocket19" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=200","-s=1","--no-redirect-out","--loop-limit=5","modbat.examples.JavaNioServerSocket3"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }

  "JavaNioServerSocket20" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=1","-s=c2c2567","--no-redirect-out","--loop-limit=5","modbat.examples.JavaNioServerSocket3"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }

  "JavaNioServerSocket21" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=1000","-s=1","--no-redirect-out","--loop-limit=5","modbat.examples.JavaNioServerSocket3"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }
}
