package modbat.genran;

import modbat.examples.ControlCounter;
import org.objenesis.ObjenesisStd;
import randoop.DummyVisitor;
import randoop.ExecutionOutcome;
import randoop.Globals;
import randoop.NormalExecution;
import randoop.operation.TypedOperation;
import randoop.org.apache.commons.lang3.reflect.FieldUtils;
import randoop.sequence.ExecutableSequence;
import randoop.sequence.Sequence;
import randoop.test.DummyCheckGenerator;
import randoop.types.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class RandoopUtils {

    /** TODO Maybe we can change the reflection code into sequence
     *
     * @param object
     * @return
     * @throws IllegalAccessException
     */
    public static Sequence createSequenceForObject(Object object) throws Exception {

        if(object == null)
        {
            throw new IllegalArgumentException("object is null");
        } else
        {
            ObjenesisStd objenesisStd = new ObjenesisStd();
            Class<?> objectClass = object.getClass();

            String className = objectClass.getCanonicalName();

            Class<?> objectClassReflection = Class.forName(className);

            Object newOb = objenesisStd.newInstance(objectClassReflection);
            Field[] fields = FieldUtils.getAllFields(objectClassReflection);

            for(int i = 0; i< fields.length; i++)
            {
                Field field = fields[i];
                field.setAccessible(true);
                Object of = field.get(object);

                field.set(newOb,of);
            }

            Method[] te12 =  RandoopUtils.class.getMethods();

            TypedOperation TobjenesisStd = TypedOperation.forConstructor(ObjenesisStd.class.getConstructor());

            TypedOperation TclassName = TypedOperation.createPrimitiveInitialization(JavaTypes.STRING_TYPE, className);

            TypedOperation TobjectClassReflection = TypedOperation.forMethod(Class.class.getMethod("forName", String.class));

            TypedOperation TnewOb = TypedOperation.forMethod(RandoopUtils.class.getMethod("newInstance", ObjenesisStd.class, Class.class));

            TypedOperation Tfields = TypedOperation.forMethod(FieldUtils.class.getMethod("getAllFields", Class.class));

            TypedOperation Telem1 = TypedOperation.createPrimitiveInitialization(JavaTypes.INT_TYPE, 0);

            TypedOperation Tgetfield = TypedOperation.forMethod(RandoopUtils.class.getMethod("getFiled", java.lang.reflect.Field[].class, int.class));

            TypedOperation Ttrue = TypedOperation.createPrimitiveInitialization(JavaTypes.BOOLEAN_TYPE, true);

            TypedOperation TsetAccessible = TypedOperation.forMethod(Field.class.getMethod("setAccessible", boolean.class));

            TypedOperation Telem8 = TypedOperation.createPrimitiveInitialization(JavaTypes.INT_TYPE, 8);

            TypedOperation TfieldSet = TypedOperation.forMethod(Field.class.getMethod("set", Object.class, Object.class));

            Sequence s = new Sequence();
            s = s.extend(TobjenesisStd);//0
            s = s.extend(TclassName);//1
            s = s.extend(TobjectClassReflection, s.getVariable(1));//2
            s = s.extend(TnewOb, s.getVariable(0), s.getVariable(2));//3
            s = s.extend(Tfields, s.getVariable(2));//4
            s = s.extend(Telem1);//5
            s = s.extend(Tgetfield, s.getVariable(4), s.getVariable(5));//6
            s = s.extend(Ttrue);//7
            s = s.extend(TsetAccessible, s.getVariable(6), s.getVariable(7) );//8
            s = s.extend(Telem8);//9
            s = s.extend(TfieldSet, s.getVariable(6), s.getVariable(3) , s.getVariable(9)  );//10

            String par1 = s.toParsableString();
            String par2 = s.toCodeString();

            ExecutableSequence es = new ExecutableSequence(s);
           es.execute(new DummyVisitor(), new DummyCheckGenerator());

            // Assuming statement at index 3 returned normally, print the runtime value
            ExecutionOutcome resultAt3 = es.getResult(3);
            if (resultAt3 instanceof NormalExecution) {

                Object oo = ((NormalExecution)resultAt3).getRuntimeValue();

                if (oo instanceof ControlCounter)
                {
                    System.out.println(((ControlCounter) oo).value());
                }

                System.out.println(oo.toString());
            }
        }

        return null;
    }

    public static Object newInstance(ObjenesisStd objenesisStd, Class<?> lass)
    {
        return (Object) objenesisStd.newInstance(lass);
    }

    public static Field getFiled(Field[] fields, int id)
    {
        return fields[id];
    }


    public static void devDocExampleTest() {
        try {
            // Want constructor for LinkedList<String>
            InstantiatedType linkedListType =
                    JDKTypes.LINKED_LIST_TYPE.instantiate(JavaTypes.STRING_TYPE);
            Substitution<ReferenceType> substLL = linkedListType.getTypeSubstitution();
            TypedOperation newLL =
                    TypedOperation.forConstructor(LinkedList.class.getConstructor()).apply(substLL);

            // operations for string constant, and list method calls
            TypedOperation newOb =
                    TypedOperation.createPrimitiveInitialization(JavaTypes.STRING_TYPE, "hi!");
            TypedOperation addFirst =
                    TypedOperation.forMethod(LinkedList.class.getMethod("addFirst", Object.class))
                            .apply(substLL);
            TypedOperation size =
                    TypedOperation.forMethod(LinkedList.class.getMethod("size")).apply(substLL);

            // Call to operation with wildcard in TreeSet<String>
            InstantiatedType treeSetType = JDKTypes.TREE_SET_TYPE.instantiate(JavaTypes.STRING_TYPE);
            Substitution<ReferenceType> substTS = treeSetType.getTypeSubstitution();
            TypedOperation wcTS =
                    TypedOperation.forConstructor(TreeSet.class.getConstructor(Collection.class))
                            .apply(substTS)
                            .applyCaptureConversion();
            Substitution<ReferenceType> substWC =
                    Substitution.forArgs(wcTS.getTypeParameters(), (ReferenceType) JavaTypes.STRING_TYPE);
            TypedOperation newTS = wcTS.apply(substWC);

            // call to generic operation
            TypedOperation syncA =
                    TypedOperation.forMethod(Collections.class.getMethod("synchronizedSet", Set.class));
            Substitution<ReferenceType> substA =
                    Substitution.forArgs(syncA.getTypeParameters(), (ReferenceType) JavaTypes.STRING_TYPE);
            TypedOperation syncS = syncA.apply(substA);

            // Now, create the sequence by repeated extension.
            Sequence s = new Sequence();
            s = s.extend(newLL);
            s = s.extend(newOb);
            s = s.extend(addFirst, s.getVariable(0), s.getVariable(1));
            s = s.extend(size, s.getVariable(0));
            s = s.extend(newTS, s.getVariable(0));
            s = s.extend(syncS, s.getVariable(4));

            s.toParsableString();

            assertEquals(
                    "java.util.LinkedList<java.lang.String> strList0 = new java.util.LinkedList<java.lang.String>();"
                            + Globals.lineSep
                            + "strList0.addFirst(\"hi!\");"
                            + Globals.lineSep
                            + "int int3 = strList0.size();"
                            + Globals.lineSep
                            + "java.util.TreeSet<java.lang.String> strSet4 = new java.util.TreeSet<java.lang.String>((java.util.Collection<java.lang.String>)strList0);"
                            + Globals.lineSep
                            + "java.util.Set<java.lang.String> strSet5 = java.util.Collections.synchronizedSet((java.util.Set<java.lang.String>)strSet4);"
                            + Globals.lineSep,
                    s.toCodeString());
        } catch (NoSuchMethodException e) {
            fail("didn't find method: " + e.getMessage());
        }
    }

}
