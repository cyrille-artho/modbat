package modbat.genran

import java.util
import java.util.function.Predicate

import org.junit.Assert.assertTrue
import randoop.generation.{ForwardGenerator, _}
import randoop.main.GenInputsAbstract.require_classname_in_test
import randoop.main._
import randoop.operation.{OperationParseException, TypedOperation}
import randoop.reflection.VisibilityPredicate.IS_PUBLIC
import randoop.reflection._
import randoop.sequence.{ExecutableSequence, Sequence}
import randoop.test.{ContractSet, TestCheckGenerator}
import randoop.types.Type
import randoop.util.MultiMap

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._


class RandoopManager extends RandomTestManager {

  object RandoopManager{

     var forwardGenerator : ForwardGenerator = _
  }

  override def init(classes: Seq[String], objects: Seq[AnyRef], observers: Seq[String], methods: Seq[String]): Unit = {

    val optionsCache = new OptionsCache
    optionsCache.saveState() //TODO is it necessary?

    GenInputsAbstract.deterministic = true
    GenInputsAbstract.minimize_error_test = false
    GenInputsAbstract.time_limit = 60
    GenInputsAbstract.generated_limit = 100
    GenInputsAbstract.output_limit = 50
    GenInputsAbstract.silently_ignore_bad_class_names = false
    GenInputsAbstract.testclass = classes.toList.asJava
    GenInputsAbstract.require_classname_in_test = null
    GenInputsAbstract.require_covered_classes = null

    createForwardGenerator(objects, observers, methods)
  }

  override def run: Unit = {

    RandoopManager.forwardGenerator.createAndClassifySequences()
  }

  override def validate: Unit = {

    for (e <- RandoopManager.forwardGenerator.getErrorTestSequences)
      {
          println(e.toCodeString)
      }

    println("validate:end")
  }

  def createForwardGenerator(objects: Seq[AnyRef], observers: Seq[String], methods: Seq[String]): Unit = {

    val operationModel: OperationModel = OperationModel.createModel(IS_PUBLIC, new DefaultReflectionPredicate, GenInputsAbstract.omitmethods, GenInputsAbstract.getClassnamesFromArgs, new util.LinkedHashSet[String], methods.toSet[String].asJava, new ThrowClassNameError, GenInputsAbstract.literals_file)

    val components: util.Set[Sequence] = new util.LinkedHashSet[Sequence]
    components.addAll(SeedSequences.defaultSeeds) //TODO here mabe its a place for adding sequence, the moment where we add the trait
    components.addAll(operationModel.getAnnotatedTestValues)

    objects.foreach(f => components.add(RandoopUtils.createSequenceForObject(f, 0))) //TODO put try catch for unexpected behavior


    val componentMgr: ComponentManager = new ComponentManager(components)
    operationModel.addClassLiterals(componentMgr, GenInputsAbstract.literals_file, GenInputsAbstract.literals_level)


    val observerMap: MultiMap[Type, TypedOperation] = operationModel.getObservers(observers.toSet[String].asJava)

    val observersL: util.Set[TypedOperation] = new util.LinkedHashSet[TypedOperation]

    import scala.collection.JavaConversions._
    for (keyType <- observerMap.keySet) {
      observersL.addAll(observerMap.getValues(keyType))
    }

    val testGenerator: ForwardGenerator = new ForwardGenerator(operationModel.getOperations, observersL, new GenInputsAbstract.Limits, componentMgr, new RandoopListenerManager, operationModel.getClassTypes)
    val genTests: GenTests = new GenTests

    val objectConstructor: TypedOperation =  TypedOperation.forConstructor(classOf[java.lang.Object].getConstructor())

    val newObj: Sequence = new Sequence().extend(objectConstructor)
    val excludeSet: util.Set[Sequence] = new util.LinkedHashSet[Sequence]
    excludeSet.add(newObj)
    val isOutputTest: Predicate[ExecutableSequence] = genTests.createTestOutputPredicate(excludeSet, operationModel.getCoveredClassesGoal, require_classname_in_test)
    testGenerator.setTestPredicate(isOutputTest)
    val contracts: ContractSet = operationModel.getContracts
    val checkGenerator: TestCheckGenerator = GenTests.createTestCheckGenerator(IS_PUBLIC, contracts, observerMap)
    testGenerator.setTestCheckGenerator(checkGenerator)

    RandoopManager.forwardGenerator = testGenerator
  }
}