package modbat.test

object SUTWithFailedReq {
  def fail: Unit = { require(false) }
}
