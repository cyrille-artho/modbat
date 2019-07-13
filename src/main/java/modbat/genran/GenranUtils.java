package modbat.genran;

import modbat.trace.RecordedTransition;
import randoop.generation.*;
import randoop.main.*;
import randoop.operation.OperationParseException;
import randoop.operation.TypedOperation;
import randoop.reflection.DefaultReflectionPredicate;
import randoop.reflection.OperationModel;
import randoop.reflection.ReflectionPredicate;
import randoop.reflection.VisibilityPredicate;
import randoop.sequence.ExecutableSequence;
import randoop.sequence.Sequence;
import randoop.test.ContractSet;
import randoop.test.TestCheckGenerator;
import randoop.types.JavaTypes;
import randoop.types.Type;
import randoop.util.MultiMap;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static randoop.main.GenInputsAbstract.require_classname_in_test;
import static randoop.reflection.VisibilityPredicate.IS_PUBLIC;

public class GenranUtils {

    private GenranUtils() {
    }

    public static final String ERROR_MSG = "Error found during random search:\n" +
            "[Modbat] transitions:\n" +
            "%s\n" +
            "--== switching to random testing ==--\n" +
            "[Randoop] stack trace:\n" +
            "%s\n";

    /**
     * Function that creates a randoop sequence for each type of Objects in @ObjectHolder memory
     *
     * @return a list of sequences for each type of objects
     * @throws Exception
     */
    public static List<Sequence> createSequencesForObjectHolder() throws Exception {

        List<Sequence> sequenceList = new ArrayList<>();

        TypedOperation pop = TypedOperation.forMethod(ObjectHolder.class.getMethod("pick", String.class, int.class));

        Set<Class> objectsMapKeys = ObjectHolder.getObjectsMapKeys();

        for (Class c : objectsMapKeys) {
            Sequence sBase = new Sequence();

            TypedOperation className = TypedOperation.createPrimitiveInitialization(JavaTypes.STRING_TYPE, c.getName());
            sBase = sBase.extend(className);

            for (int i = 0; i < ObjectHolder.getSizeOfKeySupSet(c); i++) {

                TypedOperation randomSeed = TypedOperation.createPrimitiveInitialization(JavaTypes.INT_TYPE, i);
                Sequence sSub = sBase.extend(randomSeed);

                sSub = sSub.extend(pop, sSub.getVariable(0), sSub.getVariable(1));

                TypedOperation cast = TypedOperation.createCast(Type.forClass(Object.class), Type.forClass(c));
                sSub = sSub.extend(cast, sSub.getVariable(2));

                sequenceList.add(sSub);
            }
        }

        return sequenceList;
    }


    public static scala.collection.immutable.List<RecordedTransition> getRecordedTransitions(String text) throws ClassNotFoundException {

        Pattern pattern = Pattern.compile("modbat\\.genran\\.ObjectHolder\\.pick\\(\"(.*?)\", ([0-9]*)\\);");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return ObjectHolder.getRecordedTransitions(matcher.group(1), Integer.valueOf(matcher.group(2)));
        }

        return null;
    }


    public static ForwardGenerator randomSearch(List<String> classes) throws Exception {


        OptionsCache optionsCache = new OptionsCache();
        optionsCache.saveState();

        GenInputsAbstract.deterministic = true;
        GenInputsAbstract.minimize_error_test = false;
        GenInputsAbstract.time_limit = 600;
        GenInputsAbstract.generated_limit = 1000;
        GenInputsAbstract.output_limit = 500;
        GenInputsAbstract.silently_ignore_bad_class_names = false;
        GenInputsAbstract.testclass = classes;
        GenInputsAbstract.require_classname_in_test = null; // TODO
        GenInputsAbstract.require_covered_classes = null; //TODO

        VisibilityPredicate visibility = IS_PUBLIC;
        Set<String> classnames = GenInputsAbstract.getClassnamesFromArgs();

        Set<String> coveredClassnames =
                GenInputsAbstract.getStringSetFromFile(
                        GenInputsAbstract.require_covered_classes, "coverage class names");
        Set<String> omitFields =
                GenInputsAbstract.getStringSetFromFile(GenInputsAbstract.omit_field_list, "field list");
        ReflectionPredicate reflectionPredicate = new DefaultReflectionPredicate(omitFields);
        ClassNameErrorHandler classNameErrorHandler = new ThrowClassNameError();

        OperationModel operationModel =
                    OperationModel.createModel(
                            visibility,
                            reflectionPredicate,
                            GenInputsAbstract.omitmethods,
                            classnames,
                            coveredClassnames,
                            new HashSet<>(),
                            classNameErrorHandler,
                            GenInputsAbstract.literals_file,
                            null);

        List<TypedOperation> model = operationModel.getOperations();
        Set<Sequence> components = new LinkedHashSet<>();
        components.addAll(SeedSequences.defaultSeeds());
        components.addAll(operationModel.getAnnotatedTestValues());
        components.addAll(GenranUtils.createSequencesForObjectHolder());

        ComponentManager componentMgr = new ComponentManager(components);
        operationModel.addClassLiterals(
                componentMgr, GenInputsAbstract.literals_file, GenInputsAbstract.literals_level);

        // Maps each class type to the observer methods in it.
        MultiMap<Type, TypedOperation> observerMap;
        try {
            observerMap = operationModel.getObservers(new HashSet<>());
        } catch (OperationParseException e) {
            System.out.printf("Parse error while reading observers: %s%n", e);
            System.exit(1);
            throw new Error("dead code");
        }
        assert observerMap != null;
        Set<TypedOperation> observers = new LinkedHashSet<>();
        for (Type keyType : observerMap.keySet()) {
            observers.addAll(observerMap.getValues(keyType));
        }

        RandoopListenerManager listenerMgr = new RandoopListenerManager();
        ForwardGenerator testGenerator =
                new ForwardGenerator(
                        model,
                        observers,
                        new GenInputsAbstract.Limits(),
                        componentMgr,
                        listenerMgr,
                        operationModel.getClassTypes());
        GenTests genTests = new GenTests();

        TypedOperation objectConstructor;
        try {
            objectConstructor = TypedOperation.forConstructor(Object.class.getConstructor());
        } catch (NoSuchMethodException e) {

            throw new Error("dead code");
        }

        Sequence newObj = new Sequence().extend(objectConstructor);
        Set<Sequence> excludeSet = new LinkedHashSet<>();
        excludeSet.add(newObj);

        Predicate<ExecutableSequence> isOutputTest =
                genTests.createTestOutputPredicate(
                        excludeSet, operationModel.getCoveredClassesGoal(), require_classname_in_test);
        testGenerator.setTestPredicate(isOutputTest);

        ContractSet contracts = operationModel.getContracts();
        TestCheckGenerator checkGenerator =
                GenTests.createTestCheckGenerator(visibility, contracts, observerMap);
        testGenerator.setTestCheckGenerator(checkGenerator);
        //testGenerator.setExecutionVisitor(
        //        new CoveredClassVisitor(operationModel.getCoveredClassesGoal()));

        //TestUtils.setAllLogs(testGenerator); //TODO check it

        testGenerator.createAndClassifySequences();


        return testGenerator;
    }
}
