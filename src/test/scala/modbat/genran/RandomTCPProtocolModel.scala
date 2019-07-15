package modbat.genran

import modbat.dsl.{Model, RandomSearch, Save}

@RandomSearch(Array("--testclass=modbat.genran.Host", "--stop-on-error-test=true", "--time-limit=50", "--generated-limit=500"))
class RandomTCPProtocolModel extends Model {

  @Save var alice = new Host
  @Save var bob = new Host
  @Save var timer = 0

  def synAlice(): Unit = {
    alice.syn(bob.getState)
    valid()
  }

  def ackAlice(): Unit = {
    alice.ack(bob.getState)
    valid()
  }

  def synackAlice(): Unit = {
    alice.syn_ack(bob.getState)
    valid()
  }

  def activeAlice(): Unit = {
    alice.active(bob.getState)
    valid()
  }

  def synBob(): Unit = {
    bob.syn(alice.getState)
    valid()
  }

  def ackBob(): Unit = {
    bob.ack(alice.getState)
    valid()
  }

  def synackBob(): Unit = {
    bob.syn_ack(alice.getState)
    valid()
  }

  def activeBob(): Unit = {
    bob.active(alice.getState)
    valid()
  }

  def connectionError(): Unit = {
    choose(
      { () => alice.connectionError(bob.getState) },
      { () => bob.connectionError(alice.getState) }
    )
    valid()
  }

  def valid(): Unit = {

    assert(alice.getState != State.CONNECTION_ERROR)
    assert(bob.getState != State.CONNECTION_ERROR)

    timer += 1
    if (timer % 5 == 0) {
      alice.idle()
      bob.idle()
    }
  }

  "int"         -> "syn"
  "syn"         -> "syn"            := synAlice
  "syn"         -> "syn"            := synBob
  "syn"         -> "synack"         := synackBob
  "syn"         -> "synack"         := synackAlice
  "synack"      -> "ack"            := ackAlice
  "synack"      -> "ack"            := ackBob
  "ack"         -> "active"         := activeBob
  "ack"         -> "active"         := activeAlice
}
