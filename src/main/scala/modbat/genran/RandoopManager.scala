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

    val rTests = RandoopManager.forwardGenerator.getRegressionSequences
    val eTests = RandoopManager.forwardGenerator.getErrorTestSequences

    for(i <- 0 to rTests.size()){

      var kdas123 = eTests.get(i).sequence.toCodeString

      var ontest = "as"

    }

    assertTrue("should have some regression tests", !rTests.isEmpty)
    assertTrue("should have some error tests", eTests.isEmpty)
  }

  def createForwardGenerator(objects: Seq[AnyRef], observers: Seq[String], methods: Seq[String]): Unit = {

    var operationModel: OperationModel = null
    try
      operationModel = OperationModel.createModel(IS_PUBLIC, new DefaultReflectionPredicate, GenInputsAbstract.omitmethods, GenInputsAbstract.getClassnamesFromArgs, new util.LinkedHashSet[String], methods.toSet[String].asJava, new ThrowClassNameError, GenInputsAbstract.literals_file, null)
    catch {
      case e: SignatureParseException =>
        throw new Error("dead code")
      case e: NoSuchMethodException =>
        throw new Error("dead code")
    }
    assert(operationModel != null)

    val components: util.Set[Sequence] = new util.LinkedHashSet[Sequence]
    components.addAll(SeedSequences.defaultSeeds) //TODO here mabe its a place for adding sequence, the moment where we add the trait
    components.addAll(operationModel.getAnnotatedTestValues)

    objects.foreach(f => components.add(RandoopUtils.createSequenceForObject(f)))


    val componentMgr: ComponentManager = new ComponentManager(components)
    operationModel.addClassLiterals(componentMgr, GenInputsAbstract.literals_file, GenInputsAbstract.literals_level)

    val observerSignatures: util.Set[String] = GenInputsAbstract.getStringSetFromFile(GenInputsAbstract.observers, "observer", "//.*", null)
    // Maps each class type to the observer methods in it.
    var observerMap: MultiMap[Type, TypedOperation] = null
    try
      observerMap = operationModel.getObservers(observers.toSet[String].asJava)
    catch {
      case e: OperationParseException =>
        System.out.printf("Parse error while reading observers: %s%n", e)
        System.exit(1)
        throw new Error("dead code")
    }
    assert(observerMap != null)
    val observersL: util.Set[TypedOperation] = new util.LinkedHashSet[TypedOperation]
    import scala.collection.JavaConversions._
    for (keyType <- observerMap.keySet) {
      observersL.addAll(observerMap.getValues(keyType))
    }

    val testGenerator: ForwardGenerator = new ForwardGenerator(operationModel.getOperations, observersL, new GenInputsAbstract.Limits, componentMgr, new RandoopListenerManager, operationModel.getClassTypes)
    val genTests: GenTests = new GenTests
    var objectConstructor: TypedOperation = null
    try
      objectConstructor = TypedOperation.forConstructor(classOf[Any].getConstructor())
    catch {
      case e: NoSuchMethodException =>
        throw new Error("dead code")
    }
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