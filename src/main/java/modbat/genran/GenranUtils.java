package modbat.genran;

import modbat.trace.RecordedTransition;
import randoop.ExecutionVisitor;
import randoop.Globals;
import randoop.condition.RandoopSpecificationError;
import randoop.condition.SpecificationCollection;
import randoop.generation.*;
import randoop.main.*;
import randoop.operation.OperationParseException;
import randoop.operation.TypedClassOperation;
import randoop.operation.TypedOperation;
import randoop.org.plumelib.options.Options;
import randoop.reflection.*;
import randoop.sequence.ExecutableSequence;
import randoop.sequence.Sequence;
import randoop.sequence.Variable;
import randoop.test.ContractSet;
import randoop.test.TestCheckGenerator;
import randoop.types.ClassOrInterfaceType;
import randoop.types.JavaTypes;
import randoop.types.Type;
import randoop.util.Log;
import randoop.util.MultiMap;
import randoop.util.ReflectionExecutor;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static randoop.main.GenInputsAbstract.*;

public class GenranUtils {

    private GenranUtils() {
    }

    public static final String ERROR_MSG = "Error found during random search:\n" +
            "\n" +
            "[Modbat] transitions:\n" +
            "%s\n" +
            "--== switching to random testing ==--\n" +
            "\n" +
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

    /**
     * Bridge to randoop functionality from randoop.main.GenTests.handle(String[] args)
     * - blackbox
     * - TODO needs refactor
     * @param args
     * @return
     * @throws Exception
     */
    public static ForwardGenerator randomSearch(List<String> args) throws Exception {
        Options options =
                new Options(
                        GenTests.class,
                        GenInputsAbstract.class,
                        ReflectionExecutor.class,
                        ForwardGenerator.class,
                        AbstractGenerator.class);

        options.parse(args.toArray(new String[0]));

        GenTests genTests = new GenTests();
        genTests.checkOptionsValid();

        Set<String> classnames = GenInputsAbstract.getClassnamesFromArgs();
        Set<String> coveredClassnames = GenInputsAbstract.getStringSetFromFile(require_covered_classes, "coverage class names");
        Set<String> omitFields = GenInputsAbstract.getStringSetFromFile(omit_field_list, "field list");
        omitFields.addAll(omit_field);
        Object visibility;
        if (GenInputsAbstract.junit_package_name == null) {
            visibility = VisibilityPredicate.IS_PUBLIC;
        } else if (GenInputsAbstract.only_test_public_members) {
            visibility = VisibilityPredicate.IS_PUBLIC;
            if (GenInputsAbstract.junit_package_name != null) {
                System.out.println("Not using package " + GenInputsAbstract.junit_package_name + " since --only-test-public-members is set");
            }
        } else {
            visibility = new VisibilityPredicate.PackageVisibilityPredicate(GenInputsAbstract.junit_package_name);
        }

        ReflectionPredicate reflectionPredicate = new DefaultReflectionPredicate(omitFields);
        ClassNameErrorHandler classNameErrorHandler = new ThrowClassNameError();
        if (silently_ignore_bad_class_names) {
            classNameErrorHandler = new WarnOnBadClassName();
        }

        String classpath = Globals.getClassPath();
        SpecificationCollection operationSpecifications = null;

        try {
            operationSpecifications = SpecificationCollection.create(GenInputsAbstract.specifications);
        } catch (RandoopSpecificationError var40) {
            System.out.println("Error in specifications: " + var40.getMessage());
            System.exit(1);
        }

        OperationModel operationModel = null;

        try {
            operationModel = OperationModel.createModel((VisibilityPredicate) visibility, reflectionPredicate, omitmethods, classnames, coveredClassnames, (ClassNameErrorHandler) classNameErrorHandler, GenInputsAbstract.literals_file, operationSpecifications);
        } catch (SignatureParseException var42) {
            System.out.printf("%nError: parse exception thrown %s%n", var42);
            System.out.println("Exiting Randoop.");
            System.exit(1);
        } catch (NoSuchMethodException var43) {
            System.out.printf("%nError building operation model: %s%n", var43);
            System.out.println("Exiting Randoop.");
            System.exit(1);
        } catch (RandoopClassNameError var44) {
            RandoopClassNameError e = var44;
            System.out.printf("Error: %s%n", var44.getMessage());
            if (var44.getMessage().startsWith("No class with name \"")) {
                System.out.println("More specifically, none of the following files could be found:");
                StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);

                while (tokenizer.hasMoreTokens()) {
                    String classPathElt = tokenizer.nextToken();
                    String classFileName;
                    if (classPathElt.endsWith(".jar")) {
                        classFileName = e.className.replace(".", "/") + ".class";
                        System.out.println("  " + classFileName + " in " + classPathElt);
                    } else {
                        classFileName = e.className.replace(".", File.separator) + ".class";
                        if (!classPathElt.endsWith(File.separator)) {
                            classPathElt = classPathElt + File.separator;
                        }

                        System.out.println("  " + classPathElt + classFileName);
                    }
                }

                System.out.println("Correct your classpath or the class name and re-run Randoop.");
            }

            System.exit(1);
        } catch (RandoopSpecificationError var45) {
            System.out.printf("Error: %s%n", var45.getMessage());
            System.exit(1);
        }

        assert operationModel != null;

        List<TypedOperation> operations = operationModel.getOperations();
        Set<ClassOrInterfaceType> classesUnderTest = operationModel.getClassTypes();
        if (operations.size() <= 1) {
            System.out.println("There are no operations to test. Exiting.");
            operationModel.dumpModel(System.out);
            System.exit(1);
        }

        if (GenInputsAbstract.progressdisplay) {
            System.out.println("PUBLIC MEMBERS=" + operations.size());
        }

        Set<Sequence> components = new LinkedHashSet();
        components.addAll(SeedSequences.defaultSeeds());
        components.addAll(operationModel.getAnnotatedTestValues());
        components.addAll(GenranUtils.createSequencesForObjectHolder()); //<---- change
        ComponentManager componentMgr = new ComponentManager(components);
        operationModel.addClassLiterals(componentMgr, GenInputsAbstract.literals_file, GenInputsAbstract.literals_level);
        RandoopListenerManager listenerMgr = new RandoopListenerManager();

        MultiMap observerMap;
        try {
            observerMap = OperationModel.readOperations(GenInputsAbstract.observers, true);
        } catch (OperationParseException var39) {
            System.out.printf("Error parsing observers: %s%n", var39.getMessage());
            System.exit(1);
            throw new Error("dead code");
        }

        Set<TypedOperation> observers = new LinkedHashSet();
        Iterator var18 = observerMap.keySet().iterator();

        while (var18.hasNext()) {
            Type keyType = (Type) var18.next();
            observers.addAll(observerMap.getValues(keyType));
        }

        ForwardGenerator explorer = new ForwardGenerator(operations, observers, new GenInputsAbstract.Limits(), componentMgr, listenerMgr, classesUnderTest);
        operationModel.log();
        if (GenInputsAbstract.operation_history_log != null) {
            TestUtils.setOperationLog(new PrintWriter(GenInputsAbstract.operation_history_log), explorer);
        }

        TestUtils.setSelectionLog(GenInputsAbstract.selection_log);
        ContractSet contracts = operationModel.getContracts();
        TestCheckGenerator testGen = GenTests.createTestCheckGenerator((VisibilityPredicate) visibility, contracts, observerMap);
        explorer.setTestCheckGenerator(testGen);

        TypedClassOperation objectConstructor;
        try {
            objectConstructor = TypedOperation.forConstructor(Object.class.getConstructor());
        } catch (NoSuchMethodException var38) {
            throw new RandoopBug("failed to get Object constructor", var38);
        }

        Sequence newObj = (new Sequence()).extend(objectConstructor, new Variable[0]);
        Set<Sequence> excludeSet = new LinkedHashSet();
        excludeSet.add(newObj);
        Predicate<ExecutableSequence> isOutputTest = genTests.createTestOutputPredicate(excludeSet, operationModel.getCoveredClassesGoal(), GenInputsAbstract.require_classname_in_test);
        explorer.setTestPredicate(isOutputTest);
        List<ExecutionVisitor> visitors = new ArrayList();
//        if (GenInputsAbstract.require_covered_classes != null) {
//            visitors.add(new CoveredClassVisitor(operationModel.getCoveredClassesGoal()));
//        }

//        if (!GenInputsAbstract.visitor.isEmpty()) {
//            Iterator var26 = GenInputsAbstract.visitor.iterator();
//
//            while (var26.hasNext()) {
//                String visitorClsName = (String) var26.next();
//
//                try {
//                    Class<ExecutionVisitor> cls = Class.forName(visitorClsName);
//                    ExecutionVisitor vis = (ExecutionVisitor) cls.getDeclaredConstructor().newInstance();
//                    visitors.add(vis);
//                } catch (Exception var37) {
//                    throw new RandoopBug("Error while loading visitor class " + visitorClsName, var37);
//                }
//            }
//        }

        explorer.setExecutionVisitor(visitors);
        if (GenInputsAbstract.progressdisplay) {
            System.out.printf("Explorer = %s%n", explorer);
        }

        if (Log.isLoggingOn()) {
            Log.logPrintf("Initial sequences (seeds):%n", new Object[0]);
            componentMgr.log();
        }

        explorer.createAndClassifySequences();

        return explorer;

    }
}
