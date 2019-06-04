package modbat.mbt

import org.scalatest._

class ConfigTest extends FlatSpec with Matchers {
  "ConfigTest1" should "pass" in {
    val result = modbat.config.ConfigTest.main(Array("-cp","modbat.config.ConfigTest","-h"))
    result._1 should be(0)
    result._3 shouldBe empty
  }



  "ConfigTest2" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-cp","modbat.config.ConfigTest","-s"), ModbatTestHarness.setFooJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}