package modbat.genran

import modbat.examples.ControlCounter
import org.scalatest.FunSuite
import randoop.sequence.{ExecutableSequence, Sequence}
import randoop.test.DummyCheckGenerator
import randoop.{DummyVisitor, NormalExecution}

class RandoopUtilsTest extends FunSuite {

  test("testCreateSequenceForObject-ControlCounter") {

    val cc = new ControlCounter
    cc.inc2()
    cc.toggleSwitch()
    cc.inc()

    val s = RandoopUtils.createSequenceForObject(cc);
    val newCC = getObjectFromSequence(s)

    newCC equals cc

  }

  def getObjectFromSequence(seq : Sequence) = {

    val es = new ExecutableSequence(seq)
    es.execute(new DummyVisitor, new DummyCheckGenerator)

    val result = es.getResult(3)

    result match {
      case execution: NormalExecution => execution.getRuntimeValue
      case _ => fail()
    }
  }

}
