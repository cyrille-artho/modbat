package modbat.genran

import modbat.examples.ControlCounter
import org.scalatest.FunSuite
import randoop.sequence.{ExecutableSequence, Sequence}
import randoop.test.DummyCheckGenerator
import randoop.{DummyVisitor, NormalExecution}

import scala.collection.JavaConverters._

class RandoopUtilsTest extends FunSuite {

  test("testCreateSequenceForObject-ControlCounter") {

    val cc =  new ControlCounter
    cc.inc2()
    cc.toggleSwitch()
    cc.inc()

    ObjectHolder.add(cc)
    val s = RandoopUtils.createSequencesForObject()
    val newCC = getObjectFromSequence(s.get(0))

    newCC equals cc

  }

  def getObjectFromSequence(seq : Sequence): AnyRef = {

        val es = new ExecutableSequence(seq)
        es.execute(new DummyVisitor, new DummyCheckGenerator)

        val result = es.getResult(0)

        result match {
          case execution: NormalExecution => execution.getRuntimeValue
          case _ => fail()
        }
      }

//  Deprecated
//  test("testCreateSequenceForObject") {
//
//    val cc =  new ControlCounter
//    cc.inc2()
//    cc.toggleSwitch()
//    cc.inc()
//
//    val s = RandoopUtils.createSequenceForObject(cc,0 );
//    val newCC = getObjectFromSequence(s)
//
//    newCC equals cc
//  }
//
//  def getObjectFromSequence(seq : Sequence) = {
//
//    val es = new ExecutableSequence(seq)
//    es.execute(new DummyVisitor, new DummyCheckGenerator)
//
//    val result = es.getResult(3)
//
//    result match {
//      case execution: NormalExecution => execution.getRuntimeValue
//      case _ => fail()
//    }
//  }

}
