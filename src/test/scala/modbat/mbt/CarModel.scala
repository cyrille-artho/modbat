package modbat.mbt

import org.scalatest._

class CarModel extends FlatSpec with Matchers {
  "CarModel1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=20","modbat.test.CarModel","--no-redirect-out"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}