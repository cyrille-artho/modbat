package modbat.genran

import java.util
import java.util.function.Predicate

import randoop.generation.{ForwardGenerator, _}
import randoop.main.GenInputsAbstract.require_classname_in_test
import randoop.main._
import randoop.operation.TypedOperation
import randoop.reflection.VisibilityPredicate.IS_PUBLIC
import randoop.reflection._
import randoop.sequence.{ExecutableSequence, Sequence}
import randoop.test.{ContractSet, TestCheckGenerator}
import randoop.types.Type
import randoop.util.MultiMap

import scala.collection.JavaConverters._

/**
  *   Black box logic of running randoop
  *   TODO potentially move to java
  */
class RandoopManager  {

  object RandoopManager{
     var forwardGenerator : ForwardGenerator = _
  }

  def init(classes: Seq[String]): Unit = {

    val optionsCache = new OptionsCache
    optionsCache.saveState()

    GenInputsAbstract.deterministic = true
    GenInputsAbstract.minimize_error_test = false
    GenInputsAbstract.time_limit = 600
    GenInputsAbstract.generated_limit = 1000
    GenInputsAbstract.output_limit = 500
    GenInputsAbstract.silently_ignore_bad_class_names = false
    GenInputsAbstract.testclass = classes.toList.asJava
    GenInputsAbstract.require_classname_in_test = null
    GenInputsAbstract.require_covered_classes = null

    createForwardGenerator()
  }

  def run(): Unit = RandoopManager.forwardGenerator.createAndClassifySequences()

  def getForwardGenerator: ForwardGenerator = RandoopManager.forwardGenerator

  def createForwardGenerator(): Unit = {

    val operationModel: OperationModel = OperationModel.createModel(IS_PUBLIC, new DefaultReflectionPredicate, GenInputsAbstract.omitmethods, GenInputsAbstract.getClassnamesFromArgs, new util.LinkedHashSet[String], new util.HashSet[String](), new ThrowClassNameError, GenInputsAbstract.literals_file)

    val components: util.Set[Sequence] = new util.LinkedHashSet[Sequence]
    components.addAll(SeedSequences.defaultSeeds)
    components.addAll(operationModel.getAnnotatedTestValues)
    components.addAll(GenranUtils.createSequencesForObjectHolder())

    val componentMgr: ComponentManager = new ComponentManager(components)
    operationModel.addClassLiterals(componentMgr, GenInputsAbstract.literals_file, GenInputsAbstract.literals_level)

    val observerMap: MultiMap[Type, TypedOperation] = operationModel.getObservers(new util.HashSet[String])

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