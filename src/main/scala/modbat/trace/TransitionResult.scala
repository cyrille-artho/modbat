package modbat.trace

object TransitionResult {
  def isErr(res: TransitionResult) = {
    res match {
      case ExceptionOccurred(_) | ExpectedExceptionMissing => true
      case _ => false
    }
  }
}

sealed abstract class TransitionResult

case class Ok(sameInstanceAgain: Boolean = false) extends TransitionResult
case object Backtrack extends TransitionResult
case object Finished extends TransitionResult

case class ExceptionOccurred(exception: String) extends TransitionResult
// conversion from Exception to String necessary for case class
// String can distinguish between different exception messages for same class

case object ExpectedExceptionMissing extends TransitionResult

object ErrOrdering extends Ordering[(TransitionResult, String)] {
  def compare(a: (TransitionResult, String), b: (TransitionResult, String)) = {
    assert(TransitionResult.isErr(a._1))
    assert(TransitionResult.isErr(b._1))
    a match {
      case (ExceptionOccurred(e1), t1) =>
      b match {
	case (ExceptionOccurred(e2), t2) =>
	val ecmp = e1.compareTo(e2)
	if (ecmp == 0) {
	  t1.compareTo(t2)
	} else {
	  ecmp
	}
	case _ => -1
      }
      case _ =>
      b match {
	case (ExpectedExceptionMissing, _) => a._2.compareTo(b._2)
	case _ => 1
      }
    }
  }
}
