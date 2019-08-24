package modbat.mbt

import org.scalatest._

class JavaNioSocket extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "JavaNioSocket1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=10","-s=1","--no-redirect-out","--loop-limit=5","modbat.examples.JavaNioSocket3"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "JavaNioSocket2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=100","-s=1","--no-redirect-out","--loop-limit=5","modbat.examples.JavaNioSocket3"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "JavaNioSocket3" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-n=1000","-s=1","--no-redirect-out","--loop-limit=5","modbat.examples.JavaNioSocket3"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "JavaNioSocket4" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1000","modbat.examples.JavaNioSocket"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }
}
