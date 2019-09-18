package modbat.mbt

import modbat.config.Choice
import modbat.config.Doc
import modbat.config.Footnote
import modbat.config.Hex
import modbat.config.Range
import modbat.config.Requires
import modbat.config.Shorthand
import modbat.config.Test
import modbat.log.Log

class Configuration extends modbat.config.Configuration {
  @Doc("redirect output to log file")
  var redirectOut: Boolean = true

  @Doc("run initialization code")
  var init: Boolean = true

  @Doc("run shutdown code")
  var shutdown: Boolean = true

  @Doc("execute setup methods before each test")
  var setup: Boolean = true

  @Doc("execute cleanup methods after each test")
  var cleanup: Boolean = true

  @Doc("remove empty log files after test")
  var deleteEmptyLog: Boolean = true

  @Doc("remove non-empty log files on success")
  var removeLogOnSuccess: Boolean = false

  @Doc("show coverage in dot file format")
  var dotifyCoverage: Boolean = false

  @Doc("show path coverage in dot file format")
  var dotifyPathCoverage: Boolean = false

  @Doc("path coverage graph mode: abstracted or full  graph")
  @Choice(Array("abstracted", "full"))
  var pathCoverageGraphMode = "abstracted"

  @Doc("show detailed label in path coverage graphs")
  var pathLabelDetail: Boolean = false

  @Doc("use user-defined search function of path coverage graphs")
  var bfsearchFun: Boolean = false

  @Doc("output directory for dot files")
  var dotDir: String = "."

  @Doc("stop model exploration after a test failed")
  var stopOnFailure: Boolean = false

  @Doc("test fails if scala.Predef.requires fails")
  var precondAsFailure: Boolean = false

  @Doc("print stack trace of uncaught exception")
  var printStackTrace: Boolean = true

  @Doc("show choices inside transitions")
  @Footnote(Array(
    "Error trace shows internal choices; dot output shows choices and launches.",
    "Note: Nested choices and coverage of choices currently not supported."))
  var showChoices = true

  @Doc("overrides environment variable CLASSPATH if set")
  var classpath: String = "."

  @Doc("output path for traces")
  var logPath: String = "."
  @Doc("level at which messages are logged")
  @Choice(value =
            Array("none", "error", "warning", "info", "fine", "debug", "all"),
          definedIn = "modbat.log.Log")
  var logLevel = Log.Info
  // for messages from modbat

  @Doc("random seed for initial test")
  @Test(longval = 0xe9232493f22057dL) @Hex @Shorthand('s')
  @Range(ulmin = 1)
  var randomSeed = new java.util.Date().getTime()

  @Doc("probability of aborting test sequence")
  @Range(dmin = 0.0, dmax = 1.0)
  var abortProbability = 0.0

  @Doc("probability of executing \"maybe\" statement")
  @Range(dmin = 0.0, dmax = 1.0)
  var maybeProbability = 0.5

  @Doc("number of test runs")
  @Range(min = 1) @Shorthand('n')
  var nRuns = 50

  @Doc("usage mode (execute tests or generate dot file)")
  @Choice(Array("dot", "exec"))
  var mode = "exec"

  @Doc("search mode (for usage mode=exec)")
  @Choice(Array("random", "heur"))
  var search = "random"

  @Doc("bandit trade off value (for usage search=heur)")
  var banditTradeoff = 2

  @Doc("backtrack transition reward (for usage search=heur)")
  var backtrackTReward = 0.8d

  @Doc("self-loop transition reward (for usage search=heur)")
  var selfTReward = 0.4d

  @Doc("good and successful transition reward (for usage search=heur)")
  var goodTReward = 0.6d

  @Doc("failed transition reward (for usage search=heur)")
  var failTReward = 0.5d

  @Doc("passed precondition reward (for usage search=heur)")
  var precondPassReward = 0.7d

  @Doc("failed precondition reward (for usage search=heur)")
  var precondFailReward = 0.7d

  @Doc("passed assertion reward (for usage search=heur)")
  var assertPassReward = 0.7d

  @Doc("failed assertion reward (for usage search=heur)")
  var assertFailReward = 0.7d

  @Doc("limit times same state is visited; 0 = no limit")
  @Range(min = 0)
  var loopLimit = 0

  @Doc("use auto-generated labels if no label given")
  var autoLabels = true

  /*  @Doc("apply delta debugging to traces (WORK IN PROGRESS)")
  var shrinkTraces: Boolean = false */
}
