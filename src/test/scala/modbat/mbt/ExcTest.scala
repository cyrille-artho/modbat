package modbat.mbt

import org.scalatest._

class ExcTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "ExcTest1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=9999","-n=1","--no-redirect-out","--abort-probability=0.2","modbat.test.ExcTest"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ExcTest2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.ExcTest2"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ExcTest3" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.ExcTest3"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ExcTest4" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.ExcTest4"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ExcTest5" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.ExcTest5"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ExcTest6" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.ExcTest6"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ExcTest7" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.ExcTestA3"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ExcTest8" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.ExcTestA4"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ExcTest9" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.ExcTestA5"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ExcTest10" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.ExcTestA5","--log-level=fine"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}
