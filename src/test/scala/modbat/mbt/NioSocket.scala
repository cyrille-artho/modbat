package modbat.mbt

import org.scalatest._

class NioSocket extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "NioSocket1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=5","-s=1","--no-redirect-out","--log-level=fine","--no-init","--no-shutdown","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 should not be empty
  }



  "NioSocket2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=10","-s=1","--no-redirect-out","--log-level=fine","--no-init","--no-shutdown","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket3" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=20","-s=1","--no-redirect-out","--log-level=fine","--no-init","--no-shutdown","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket4" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=50","-s=1","--no-redirect-out","--log-level=fine","--no-init","--no-shutdown","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket5" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=10","-s=1","--no-redirect-out","--log-level=fine","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket6" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=20","-s=1","--no-redirect-out","--log-level=fine","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket7" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=50","-s=1","--no-redirect-out","--log-level=fine","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket8" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=100","-s=1","--no-redirect-out","--log-level=fine","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket9" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=200","-s=1","--no-redirect-out","--log-level=fine","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket10" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=500","-s=1","--no-redirect-out","--log-level=fine","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket11" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=1000","-s=1","--no-redirect-out","--log-level=fine","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket12" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("--mode=dot","--auto-labels","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td, false, Some("modbat.examples.NioSocket1.dot", "niosocket1.dot"))
    result._1 should be(0)
    result._3 shouldBe empty
  }


// special case 
  "NioSocket13" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("--mode=dot","--auto-labels","--dot-dir=dot_test","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td, false, Some("dot_test/modbat.examples.NioSocket1.dot", "niosocket1.dot"))
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket14" should "fail" in { td =>
    val result = ModbatTestHarness.testMain(Array("--mode=dot","--auto-labels","--dot-dir=dir_does_not_exit","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(1)
    result._3 should not be empty
  }



  "NioSocket15" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=10","-s=1","--dotify-coverage","--auto-labels","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td, false, Some("modbat.examples.NioSocket1.dot", "cov_10.dot"))
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket16" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=100","-s=1","--dotify-coverage","--auto-labels","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td, false, Some("modbat.examples.NioSocket1.dot", "cov_100.dot"))
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket17" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=500","-s=1","--dotify-coverage","--auto-labels","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td, false, Some("modbat.examples.NioSocket1.dot", "cov_500.dot"))
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket18" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=500","-s=1","--maybe-probability=0","--dotify-coverage","--auto-labels","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td, false, Some("modbat.examples.NioSocket1.dot", "cov_500-p0.dot"))
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket19" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=1","-s=61a342c60d18ff4d","--no-redirect-out","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NioSocket20" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=1000","-s=1","--no-redirect-out","--stop-on-failure","modbat.examples.NioSocket1"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}
