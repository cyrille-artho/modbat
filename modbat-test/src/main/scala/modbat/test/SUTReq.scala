package modbat.test

import modbat.dsl._

class SUTReq extends Model {
  // transitions
  "ok" -> "ok" := {
    require(false)
  }
  "ok" -> "err" := {
    require(true)
    SUTWithFailedReq.fail
  }
}
