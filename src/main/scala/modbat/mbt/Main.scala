package modbat.mbt

import modbat.config.ConfigMgr
import modbat.config.Version
import modbat.log.Log

object Main {
  val config = new Configuration()

  def main(args: Array[String]) {
    System.exit(run(args))
  }

  def run(args: Array[String]): Int = {
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
	    return 1
	  }
	  modelClassName = remainingArgs.next
	  if (remainingArgs.hasNext) {
	    Log.error("Extra arguments starting at \"" + remainingArgs.next() +
		      "\" are not supported.")
	    return 1
	  }
	}
	case None => // nothing
      }
    } catch {
      case e: IllegalArgumentException => {
	Log.error(e.getMessage())
	return 1
      }
    }

    setup(modelClassName) // TODO: refactor into case code below once needed

    /* execute */
    config.mode match {
      case "dot" =>
	new Dotify(MBT.launch(null), modelClassName + ".dot").dotify()
      case _ => Modbat.explore(config.nRuns)
    }
    // TODO (issue #27): Dotify.dotify() and Modbat.explore() should use return code
  }

  def setup(modelClassName: String) {
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
  }
}
