package modbat.mbt

import org.scalatest._

class SimpleModel extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "SimpleModel1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=30","--no-redirect-out","--log-level=fine","modbat.examples.SimpleModel"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "SimpleModel2" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=49a846e52b813972","-n=1","--no-redirect-out","modbat.examples.SimpleModel"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "SimpleModel3" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=88af43883571af0c","-n=1","--no-redirect-out","modbat.examples.SimpleModel"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "SimpleModel4" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=88af43883571af0c","-n=1","--no-redirect-out","modbat.examples.SimpleModel","--print-stack-trace"), ModbatTestHarness.setExamplesJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}
