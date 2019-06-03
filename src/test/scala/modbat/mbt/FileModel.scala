package modbat.mbt

import org.scalatest._

class FileModel extends FlatSpec with Matchers {
  "FileModel1" should "pass" in {
    val result = ModbatTestHarness.testMain(Array("-s=1","-n=500","modbat.test.FileModel","--stop-on-failure"), ModbatTestHarness.setTestJar)
    result._1 should be(0)
    result._3 shouldBe empty
  }



}