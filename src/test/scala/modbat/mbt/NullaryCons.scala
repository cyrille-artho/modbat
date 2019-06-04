package modbat.mbt

import org.scalatest._

class NullaryCons extends FlatSpec with Matchers {
  "NullaryCons1" should "fail" in {
    val result = ModbatTestHarness.testMain(Array("--mode=dot","modbat.test.NullaryCons"), ModbatTestHarness.setTestJar)
    result._1 should be(1)
    result._3 should not be empty
  }



  "NullaryCons2" should "fail" in {
    val result = ModbatTestHarness.testMain(Array("--mode=dot","modbat.test.NullaryCons","--print-stack-trace"), ModbatTestHarness.setTestJar)
    result._1 should be(1)
    result._3 should not be empty
  }



}