package modbat.dsl

final case class NextStatePred(
    pred: () => Boolean,
    target: State,
    maybe: Boolean,
    fullName: String,
    line: Int
)
