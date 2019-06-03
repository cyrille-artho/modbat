package modbat.mbt

import org.scalatest._

class IllArgTest extends FlatSpec with Matchers {
  "IllArgTest1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=1","modbat.test.IllArgTest","--no-redirect-out","--log-level=fine"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}