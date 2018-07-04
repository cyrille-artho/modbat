package modbat.config

object TestConfiguration {
  val Two = 2
  val Three = 3
  val Five = 5
  val Seven = 7
}

class TestConfiguration extends Configuration {
  @Doc("redirect output to log file")
  var redirectOut: Boolean = true

  @Doc("overrides environment variable CLASSPATH if set")
  var classpath: String = "."

  @Test(longval = 0xe9232493f22057dL) @Hex @Shorthand('s')
  var randomSeed = new java.util.Date().getTime()

  @Choice(value = Array("two", "three", "five", "seven"),
	  definedIn = "modbat.config.TestConfiguration")
  var smallPrime = TestConfiguration.Two

  @Range(dmin = 0.0, dmax = 1.0)
  var abortProbability = 0.0

  @Range(min = 1) @Shorthand('n')
  var nRuns = 50

  @Choice(Array("dot", "exec"))
  var mode = "exec"

  @Requires(opt = "redirectOut", equals = "true")
  var someFlag = false

  @Requires(opt = "smallPrime", notEquals = "two")
  var oddPrime = false

  @Requires(opt = "smallPrime", equals = "two")
  var evenPrime = false
}
