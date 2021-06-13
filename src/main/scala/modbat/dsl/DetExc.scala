package modbat.dsl

import scala.util.matching.Regex

final case class DetExc(excName: Regex, target: State, fullName: String, line: Int)

object DetExc {
  def apply(excName: Regex, target: State, fullName: String, line: Int): DetExc =
    new DetExc(excName, target, fullName, line)
}
