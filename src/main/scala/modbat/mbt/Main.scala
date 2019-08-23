package modbat.mbt

import modbat.config.ConfigMgr
import modbat.config.Version
import modbat.log.Log
import modbat.util.CloneableRandom

object Main {
  def main(args: Array[String]) {
    val config = new Configuration()
    val modbat = new Modbat(config)
    modbat.isUnitTest = false
    try {
        run(modbat, args, config)
    } catch {
      case e: Exception => {
        System.exit(1)
      }
    }
    System.exit(0)
  }

  def run(modbat: Modbat, args: Array[String], config: Configuration) {
    var modelClassName: String = null
    val c = new ConfigMgr("scala modbat.jar", "CLASSNAME",
			  config, new Version ("modbat.mbt"))
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

//    modbat.init

    setup(modbat, config, modelClassName)

    /* execute */
    config.mode match {
      case "dot" =>
	new Dotify(config, MBT.launch(null), modelClassName + ".dot").dotify()
      case _ => modbat.explore(config.nRuns)
    }
  }

  def setup(modbat: Modbat, config: Configuration, modelClassName: String) {
    /* configure components */
    Log.setLevel(config.logLevel)
    MBT.enableStackTrace = config.printStackTrace
    MBT.maybeProbability = config.maybeProbability

    MBT.configClassLoader(config.classpath)
    MBT.loadModelClass(modelClassName)
    MBT.setRNG(config.randomSeed)
    MBT.isOffline = false
    MBT.runBefore = config.setup
    MBT.runAfter = config.cleanup
    MBT.precondAsFailure = config.precondAsFailure
    modbat.setup
  }
}
