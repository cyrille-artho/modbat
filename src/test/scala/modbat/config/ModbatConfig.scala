package modbat.config

import modbat.log.Log

class TraitImpl extends modbat.config.Configuration {
  var data = 0
  var text = "HelloTest"
  var logLevel = Log.Info
}

object TraitImpl {
  def main(args: Array[String]) {

    val copy = new TraitImpl()
  
    val clone = copy.clone.asInstanceOf[TraitImpl]

    clone.data = 1
    clone.text = "ByeTest"
    clone.logLevel = Log.Fine

    assert(copy.data == clone.data)
    assert(copy.text == clone.text)
    assert(copy.logLevel == clone.logLevel)

  }
}