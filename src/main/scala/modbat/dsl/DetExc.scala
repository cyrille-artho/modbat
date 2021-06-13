package modbat.dsl

import scala.util.matching.Regex

final case class DetExc(excName: Regex, target: State, fullName: String, line: Int)
