package modbat.genran;

import randoop.operation.TypedOperation;
import randoop.sequence.Sequence;
import randoop.types.Type;

import java.util.ArrayList;
import java.util.List;

public class RandoopUtils {


    public static List<Sequence> createSequencesForObject() throws Exception {

        List<Sequence> sequenceList = new ArrayList<Sequence>();

        TypedOperation pop = TypedOperation.forMethod(ObjectHolder.class.getMethod("pop"));

        Sequence sBase = new Sequence();
        sBase = sBase.extend(pop);

        TypedOperation cast = TypedOperation.createCast(Type.forClass(Object.class),Type.forClass(ObjectHolder.getClassName()));
        sBase = sBase.extend(cast, sBase.getVariable(0));

        sequenceList.add(sBase);


        return sequenceList;
    }

    /**
     *       Randoop Sequence logic of:
     *
     *       val objenesis = new ObjenesisStd
     *       val newOb = objenesis.newInstance(ob.getClass)
     *
     *       val allFields : Array[Field] = FieldUtils.getAllFields(ob.getClass)
     *       for(f <- allFields)
     *      *       {
     *      *         f.setAccessible(true)
     *      *         f.set(newOb,f.get(ob))
     *      *       }
     * @param object
     * @return
     * @throws Exception
     */
//    @Deprecated
//    public static Sequence createSequenceForObject(Object object, int nest) throws Exception {
//
//        if (object == null) {
//            return new Sequence().extend(TypedOperation.createNullOrZeroInitializationForType(Type.forValue(Object.class)));
//        } else if (nest >= 5)
//        {
//            return new Sequence().extend(TypedOperation.createNullOrZeroInitializationForType(Type.forValue(object)));
//        }else {
//
//            Type type = Type.forValue(object);
//
//            if(object.getClass().equals(MBT.class))
//            {
//                return new Sequence().extend(TypedOperation.createNullOrZeroInitializationForType(Type.forValue(MBT.class)));
//            }
//
//            if (TypedOperation.isNonreceiverType(type)) {
//                if ((type).isBoxedPrimitive()) {
//                    type = ((NonParameterizedType)type).toPrimitive();
//                }
//                if (type.equals(JavaTypes.STRING_TYPE) && !Value.stringLengthOK((String)object)) {
//                    throw new IllegalArgumentException("value is a string of length > " + GenInputsAbstract.string_maxlen);
//                } else {
//                    return (new Sequence()).extend(TypedOperation.createPrimitiveInitialization(type, object));
//                }
//
//            } else {
//
//                String className = object.getClass().getCanonicalName();
//
//                TypedOperation TobjenesisStd = TypedOperation.forConstructor(ObjenesisStd.class.getConstructor());
//                TypedOperation TclassName = TypedOperation.createPrimitiveInitialization(JavaTypes.STRING_TYPE, className);
//                TypedOperation TobjectClassReflection = TypedOperation.forMethod(Class.class.getMethod("forName", String.class));
//                TypedOperation TnewOb = TypedOperation.forMethod(RandoopUtils.class.getMethod("newInstance", ObjenesisStd.class, Class.class));
//                TypedOperation Tfields = TypedOperation.forMethod(FieldUtils.class.getMethod("getAllFields", Class.class));
//
//                int index = 0;
//
//                Sequence sBase = new Sequence();
//
//                sBase = sBase.extend(TobjenesisStd);//0
//                int indexTobjenesisStd = index++;
//
//                sBase = sBase.extend(TclassName);//1
//                int indexTclassName = index++;
//
//                sBase = sBase.extend(TobjectClassReflection, sBase.getVariable(indexTclassName));//2 -> 1
//                int indexTobjectClassReflection = index++;
//
//                sBase = sBase.extend(TnewOb, sBase.getVariable(indexTobjenesisStd), sBase.getVariable(indexTobjectClassReflection));//3
//                int indexTnewOb = index++;
//
//                sBase = sBase.extend(Tfields, sBase.getVariable(indexTobjectClassReflection));//4
//                int indexTfields = index++;
//
//                Field[] fields = FieldUtils.getAllFields(object.getClass());
//
//                for(int i = 0; i < fields.length; i++)
//                {
//
//                    if(Modifier.isFinal(fields[i].getModifiers()))
//                    {
//                        continue;
//                    }
//
//                    fields[i].setAccessible(true);
//                    Object o = fields[i].get(object);
//
//                    TypedOperation Telem1 = TypedOperation.createPrimitiveInitialization(JavaTypes.INT_TYPE, i);
//                    TypedOperation Tgetfield = TypedOperation.forMethod(RandoopUtils.class.getMethod("getField", java.lang.reflect.Field[].class, int.class));
//                    TypedOperation Ttrue = TypedOperation.createPrimitiveInitialization(JavaTypes.BOOLEAN_TYPE, true);
//                    TypedOperation TsetAccessible = TypedOperation.forMethod(Field.class.getMethod("setAccessible", boolean.class));
//                    TypedOperation TfieldSet = TypedOperation.forMethod(Field.class.getMethod("set", Object.class, Object.class));
//
//                    sBase = sBase.extend(Telem1);//5
//                    int indexTelem1 = index++;
//
//                    sBase = sBase.extend(Tgetfield, sBase.getVariable(indexTfields), sBase.getVariable(indexTelem1));//6
//                    int indexTgetfield = index++;
//
//                    sBase = sBase.extend(Ttrue);//7
//                    int indexTtrue = index++;
//
//                    sBase = sBase.extend(TsetAccessible, sBase.getVariable(indexTgetfield), sBase.getVariable(indexTtrue));//8
//                    index++;
//
//                    Sequence sInner = createSequenceForObject(o, nest+1);
//
//                    sBase = Sequence.concatenate(Arrays.asList(sBase,sInner));
//                    index = sBase.statements.size();
//
//                    sBase = sBase.extend(TfieldSet, sBase.getVariable(indexTgetfield), sBase.getVariable(indexTnewOb) , sBase.getVariable(index-1)  );//10
//                    index++;
//
//                }
//
//                TypedOperation tCast = TypedOperation.createCast(Type.forClass(Object.class),Type.forClass(object.getClass()));
//                sBase = sBase.extend(tCast, sBase.getVariable(indexTnewOb));
//
//                return sBase;
//            }
//        }
//    }
//
//
//    public static Object newInstance(ObjenesisStd objenesisStd, Class<?> lass)
//    {
//        return objenesisStd.newInstance(lass);
//    }
//
//    public static Field getField(Field[] field, int id)
//    {
//        return field[id];
//    }
}
