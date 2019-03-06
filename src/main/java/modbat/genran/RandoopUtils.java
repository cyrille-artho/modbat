package modbat.genran;

import org.objenesis.ObjenesisStd;
import randoop.org.apache.commons.lang3.reflect.FieldUtils;
import randoop.sequence.Sequence;

import java.lang.reflect.Field;

public class RandoopUtils {

    /** TODO Maybe we can change the reflection code into sequence
     *
     * @param object
     * @return
     * @throws IllegalAccessException
     */
    public static Sequence createSequenceForObject(Object object) throws IllegalAccessException {

        if(object == null)
        {
            throw new IllegalArgumentException("object is null");
        } else
        {
            ObjenesisStd objenesisStd = new ObjenesisStd();
            Class<?> objectClass = object.getClass();
            Object newOb = objenesisStd.newInstance(objectClass);
            Field[] fields = FieldUtils.getAllFields(objectClass);

            for(int i = 0; i< fields.length; i++)
            {
                Field field = fields[i];
                field.setAccessible(true);
                Object of = field.get(object);
                field.set(newOb,of);
            }
        }

        return null;
    }

    /*
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
    }*/

}
