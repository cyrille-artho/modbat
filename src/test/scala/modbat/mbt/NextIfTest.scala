package modbat.mbt

import org.scalatest._

class NextIfTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "NextIfTest1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","modbat.test.NextIfTest","--no-redirect-out"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NextIfTest2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.NextIfTest2"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NextIfTest3" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.NextIfTest3"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NextIfTest4" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.NextIfTest4"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NextIfTest5" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","modbat.test.NextIfTest5"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}
