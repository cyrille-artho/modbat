package modbat.mbt

import org.scalatest._

class Hello extends fixture.FlatSpec with fixture.TestDataFixture with Matchers {
  "Hello1" should "pass" in { td =>
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=2","--remove-log-on-success","modbat.test.Hello"), ModbatTestHarness.setTestJar, td)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}
