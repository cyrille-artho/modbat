package modbat.mbt

import org.scalatest._

class LoopTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "LoopTest1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","--log-level=fine","--loop-limit=5","modbat.test.LoopTest"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "LoopTest2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","--log-level=fine","--loop-limit=5","modbat.test.LoopTest2"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "LoopTest3" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","--log-level=fine","--loop-limit=0","modbat.test.LoopTest"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "LoopTest4" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","--log-level=fine","--loop-limit=0","modbat.test.LoopTest2"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "LoopTest5" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","--log-level=fine","--loop-limit=1","modbat.test.LoopTest"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "LoopTest6" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","--no-redirect-out","--log-level=fine","--loop-limit=1","modbat.test.LoopTest2"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "LoopTest7" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--log-level=fine","--no-redirect-out","--loop-limit=3","modbat.test.LoopTestWithLaunch"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "LoopTest8" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=20","--no-redirect-out","--loop-limit=4","modbat.test.LoopTestWithLaunch"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}
