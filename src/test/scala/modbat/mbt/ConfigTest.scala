package modbat.mbt

import org.scalatest._

class ConfigTest extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "ConfigTest1" should "pass" in { td =>
    val result = modbat.config.ConfigTest.main(Array("modbat.config.ConfigTest","-h"))
    result._1 should be(0)
    result._3 shouldBe empty
  }

  "ConfigTest2" should "pass" in { td =>
    val result = modbat.config.ConfigTest.main(Array("modbat.config.ConfigTest","-s"))
    result._1 should be(0)
    result._3 shouldBe empty
  }



}
