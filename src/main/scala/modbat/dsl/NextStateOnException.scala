package modbat.dsl

import scala.util.matching.Regex

class NextStateOnException (val exception: Regex, val target: Transition)
  extends NextStateOverride
