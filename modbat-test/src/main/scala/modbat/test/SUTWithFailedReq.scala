package modbat.test

object SUTWithFailedReq {
  def fail { require(false) }
}
