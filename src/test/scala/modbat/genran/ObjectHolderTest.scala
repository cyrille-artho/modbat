package modbat.genran

import modbat.examples.ControlCounter
import modbat.trace.RecordedTransition
import org.scalatest.FunSuite
import randoop.{DummyVisitor, NormalExecution}
import randoop.sequence.{ExecutableSequence, Sequence}
import randoop.test.DummyCheckGenerator

import scala.collection.immutable.List

class ObjectHolderTest extends FunSuite {

  test("givenControlCounter_whenCreateSequencesForObject_expectEqualObject") {

    val testVal = new ControlCounter
    testVal.inc2()
    testVal.toggleSwitch()
    testVal.inc()

    assertCreateSequencesObject(testVal)
  }

  def assertCreateSequencesObject(o: Object): Unit = {
    ObjectHolder.add(o, List.empty[RecordedTransition])
    val s = GenranUtils.createSequencesForObjectHolder()
    val newCC = getObjectFromSequence(s.get(0))

    newCC equals o
  }

  def getObjectFromSequence(seq: Sequence): AnyRef = {

    val es = new ExecutableSequence(seq)
    es.execute(new DummyVisitor, new DummyCheckGenerator)

    val result = es.getResult(2)

    result match {
      case execution: NormalExecution => execution.getRuntimeValue
      case _ => fail()
    }
  }

}
