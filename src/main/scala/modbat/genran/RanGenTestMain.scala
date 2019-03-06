package modbat.genran

import modbat.examples.ControlCounter
import modbat.mbt.Main

object RanGenTestMain {

  def main(args: Array[String]): Unit = {

    var cc = new ControlCounter
    cc.inc()

    RandoopUtils.createSequenceForObject(cc)

    //Main.main(Array("--classpath=build/modbat-test.jar", "modbat.examples.SimpleRandomModel"))

  }
}