package modbat.log

object Log {
  val All = 0
  val Debug = 1
  val Fine = 2
  val Info = 3
  val Warning = 4
  val Error = 5
  val None = 10
  //var log = new java.io.PrintStream(System.out, true, "UTF-8")
  var log = new java.io.PrintStream(Console.out, true, "UTF-8")
  var err = new java.io.PrintStream(System.err, true, "UTF-8")

  private var level = Info
  // errLevel can currently not be set - this can be added later
  // if a use case for changing it exists
  private var errLevel = Warning

  def setLevel(level: Int) {
    Log.level = level
  }

  def isLogging(level: Int): Boolean = (this.level <= level)

  def log(msg: String, level: Int) {
    if (isLogging(level)) {
      if (errLevel <= level) {
	err.println(msg)
      } else {
	log.println(msg)
      }
    }
  }

  def debug(msg: String) = {
    log("[DEBUG] " + msg, Debug)
  }

  def fine(msg: String) = {
    log("[FINE] " + msg, Fine)
  }

  def info(msg: String) = {
    log("[INFO] " + msg, Info)
  }

  def warn(msg: String) = {
    log("[WARNING] " + msg, Warning)
  }

  def error(msg: String) = {
    log("[ERROR] " + msg, Error)
  }
}
