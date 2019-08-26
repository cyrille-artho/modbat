package modbat.mbt

import org.scalatest._

class InvokeTransitionTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "InvokeTransition1" should "pass with one transition" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1", "-n=1", "--no-redirect-out", "-s",  "modbat.test.InvokeTransition1"), ModbatTestHarness.setTestJar, td)
    result._1.filter(_.contains("1 tests executed, 1 ok, 0 failed.")) should not be empty
    result._1.filter(_.contains("2 states covered (100 % out of 2),")) should not be empty
    result._1.filter(_.contains("2 transitions covered (100 % out of 2).")) should not be empty
    result._2 shouldBe empty
  }

  "InvokeTransition2" should "pass with three transitions" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1", "-n=1", "--no-redirect-out", "-s",  "modbat.test.InvokeTransition2"), ModbatTestHarness.setTestJar, td)
    result._1.filter(_.contains("1 tests executed, 1 ok, 0 failed.")) should not be empty
    result._1.filter(_.contains("3 states covered (100 % out of 3),")) should not be empty
    result._1.filter(_.contains("3 transitions covered (100 % out of 3).")) should not be empty
    result._2 shouldBe empty
  }

  "InvokeTransition3" should "pass with two transitions" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1", "-n=1", "--no-redirect-out", "-s",  "modbat.test.InvokeTransition3"), ModbatTestHarness.setTestJar, td)
    result._1.filter(_.contains("1 tests executed, 1 ok, 0 failed.")) should not be empty
    result._1.filter(_.contains("2 states covered (66 % out of 3),")) should not be empty
    result._1.filter(_.contains("2 transitions covered (66 % out of 3).")) should not be empty
    result._2 should not be empty
  }

  "InvokeTransition4" should "end with n==3" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1", "-n=1", "--no-redirect-out", "-s",  "modbat.test.InvokeTransition4"), ModbatTestHarness.setTestJar, td)
    result._1.filter(_.contains("1 tests executed, 1 ok, 0 failed.")) should not be empty
    result._1.filter(_.contains("3 states covered (100 % out of 3),")) should not be empty
    result._1.filter(_.contains("3 transitions covered (100 % out of 3).")) should not be empty
    result._2 shouldBe empty
  }
}

