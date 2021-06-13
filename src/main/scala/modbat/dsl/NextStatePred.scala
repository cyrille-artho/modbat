package modbat.dsl

final case class NextStatePred(
    pred: () => Boolean,
    target: State,
    maybe: Boolean,
    fullName: String,
    line: Int
)

object NextStatePred {
  def apply(pred: () => Boolean, target: State, maybe: Boolean, fullName: String, line: Int): NextStatePred =
    new NextStatePred(pred, target, maybe, fullName, line)
}
