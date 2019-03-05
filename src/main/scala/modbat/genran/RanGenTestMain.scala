package modbat.genran

import modbat.mbt.Main

object RanGenTestMain {

  def main(args: Array[String]): Unit = {

    Main.main(Array("--classpath=build/modbat-test.jar", "modbat.examples.SimpleRandomModel"))

    println("/n ---===>>> End <<<===---")
  }

}