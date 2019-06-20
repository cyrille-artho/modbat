package modbat.mbt

import modbat.config.ConfigMgr
import modbat.config.Version
import modbat.log.Log

object Main {
  var config = new Configuration()

  def main(args: Array[String]) {
    Modbat.isUnitTest = false
    try {
        run(args) // TODO: do not call exit once exceptions are used
    } catch {
      case e: Exception => System.exit(1)
    }
    System.exit(0)
  }

  def run(args: Array[String]){
    var modelClassName: String = null
    val c = new ConfigMgr("scala modbat.jar",
                          "CLASSNAME",
                          config,
                          new Version("modbat.mbt"))
    /* delegate parsing args to config library */
    try {
      val remainder = c.parseArgs(args)
      remainder match {
        case Some(remainingArgs) => {
          if (!remainingArgs.hasNext) {
            Log.error(c.header)
            Log.error("Model class argument missing. Try --help.")
            throw new NoModelClassException(c.header)
          }
	        modelClassName = remainingArgs.next
	        if (remainingArgs.hasNext) {
          val remaining = remainingArgs.next()
	    Log.error("Extra arguments starting at \"" + remaining +
		      "\" are not supported.")
      throw new ExtraArgumentsException(remaining)
	  }
	}
	case None => // nothing
      }
    } catch {
      case e: IllegalArgumentException => {
	Log.error(e.getMessage())
	throw e
      }
    }

    setup(modelClassName) // TODO: refactor into case code below once needed

    Modbat.init
    /* execute */
    config.mode match {
      case "dot" =>
        new Dotify(MBT.launch(null), modelClassName + ".dot").dotify()
      case _ => Modbat.explore(config.nRuns)
    }
  }

  def setup(modelClassName: String) {
    /* configure components */
    Log.setLevel(config.logLevel)
    MBT.configClassLoader(config.classpath)
    MBT.loadModelClass(modelClassName)
    MBT.setRNG(config.randomSeed)
    MBT.isOffline = false
  }
}
