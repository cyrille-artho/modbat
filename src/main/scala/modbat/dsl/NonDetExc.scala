package modbat.dsl

import scala.util.matching.Regex

final case class NonDetExc(excName: Regex, target: State, fullName: String, line: Int)
