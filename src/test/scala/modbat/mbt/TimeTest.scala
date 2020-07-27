package modbat.mbt

import org.scalatest._

class TimeTest extends FlatSpec with Matchers {
  "Time1" should "pass with one transition" in {
    val result = ModbatTestHarness.testMain(Array("-s=1", "-n=1", "--no-redirect-out", "-s",  "modbat.test.Time1"), (() => ModbatTestHarness.setTestJar()))
    result._1 should be(0)
    result._2.filter(_.contains("1 tests executed, 1 ok, 0 failed.")) should not be empty
    result._2.filter(_.contains("2 states covered (100 % out of 2),")) should not be empty
    result._2.filter(_.contains("1 transitions covered (100 % out of 1).")) should not be empty
    result._3 shouldBe empty
  }

  "Time2" should "pass with three transitions" in {
    val result = ModbatTestHarness.testMain(Array("-s=1", "-n=1", "--no-redirect-out", "-s",  "modbat.test.Time2"), (() => ModbatTestHarness.setTestJar()))
    //for(str <- result._2) println(str)
    result._1 should be(0)
    result._2.filter(_.contains("1 tests executed, 1 ok, 0 failed.")) should not be empty
    result._2.filter(_.contains("2 states covered (100 % out of 2),")) should not be empty
    result._2.filter(_.contains("3 transitions covered (100 % out of 3).")) should not be empty
    result._3 shouldBe empty
  }

}
