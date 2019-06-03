package modbat.mbt

import org.scalatest._

class NullaryCons extends FlatSpec with Matchers {
  "NullaryCons1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("--mode=dot","modbat.test.NullaryCons"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "NullaryCons2" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("--mode=dot","modbat.test.NullaryCons","--print-stack-trace"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}