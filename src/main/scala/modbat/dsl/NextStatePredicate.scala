package modbat.dsl

class transToNextStatePredicate (val /* predicate */ action: () => Boolean,
			  val target: Transition,
			  /* true for maybeNextIf */ val nonDet: Boolean)
  extends transToNextStateOverride
