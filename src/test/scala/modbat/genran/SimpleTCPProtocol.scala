package modbat.genran

class SimpleTCPProtocol extends RandomTCPProtocolModel {

  "int"         -> "syn"
  "syn"         -> "syn"            := synAlice
  "syn"         -> "syn"            := synBob
  "syn"         -> "synack"         := synackBob
  "syn"         -> "synack"         := synackAlice
  "synack"      -> "ack"            := ackAlice
  "synack"      -> "ack"            := ackBob
  "ack"         -> "active"         := activeBob
  "ack"         -> "active"         := activeAlice
  "active"      -> "connectionError":= connectionError

}
