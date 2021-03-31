package modbat.dsl

import scala.util.matching.Regex

class transToNextStateOnException (val exception: Regex, val target: Transition)
  extends transToNextStateOverride
