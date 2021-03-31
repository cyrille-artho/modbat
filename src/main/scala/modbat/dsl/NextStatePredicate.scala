package modbat.dsl

class NextStatePredicate (val /* predicate */ action: () => Boolean,
			  val target: Transition,
			  /* true for maybeNextIf */ val nonDet: Boolean)
  extends NextStateOverride
