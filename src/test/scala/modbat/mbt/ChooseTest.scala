package modbat.mbt

import org.scalatest._

class ChooseTest extends FlatSpec with Matchers with AppendedClues {
  "Choose00" should "pass with one transition" in {
    val result = ModbatTestHarness.testMain(Array("-s=1", "-n=1", "--no-redirect-out", "--classpath=build/modbat-test.jar", "-s", "modbat.test.Choose00"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._2.filter(_.contains("1 tests executed, 1 ok, 0 failed.")) should not be empty withClue(result._2.mkString("\n"))
    result._2.filter(_.contains("2 states covered (100 % out of 2),")) should not be empty
    result._2.filter(_.contains("1 transitions covered (100 % out of 1).")) should not be empty
    result._3 shouldBe empty
  }
}
