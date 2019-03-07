package modbat.genran;

import org.objenesis.ObjenesisStd;
import randoop.main.GenInputsAbstract;
import randoop.operation.TypedOperation;
import randoop.org.apache.commons.lang3.reflect.FieldUtils;
import randoop.sequence.Sequence;
import randoop.sequence.Value;
import randoop.sequence.Variable;
import randoop.types.JavaTypes;
import randoop.types.NonParameterizedType;
import randoop.types.Type;

import java.lang.reflect.Field;
import java.util.Arrays;

public class RandoopUtils {


    public static Sequence createSequenceForObject(Object object) throws Exception {

        if (object == null) {
            throw new IllegalArgumentException("object is null");
        } else {

            Type type = Type.forValue(object);
            if (TypedOperation.isNonreceiverType(type)) {
                if ((type).isBoxedPrimitive()) {
                    type = ((NonParameterizedType)type).toPrimitive();
                }
                if (type.equals(JavaTypes.STRING_TYPE) && !Value.stringLengthOK((String)object)) {
                    throw new IllegalArgumentException("value is a string of length > " + GenInputsAbstract.string_maxlen);
                } else {
                    return (new Sequence()).extend(TypedOperation.createPrimitiveInitialization((Type)type, object));
                }

            } else {

                String className = object.getClass().getCanonicalName();

                TypedOperation TobjenesisStd = TypedOperation.forConstructor(ObjenesisStd.class.getConstructor());
                TypedOperation TclassName = TypedOperation.createPrimitiveInitialization(JavaTypes.STRING_TYPE, className);
                TypedOperation TobjectClassReflection = TypedOperation.forMethod(Class.class.getMethod("forName", String.class));
                TypedOperation TnewOb = TypedOperation.forMethod(RandoopUtils.class.getMethod("newInstance", ObjenesisStd.class, Class.class));
                TypedOperation Tfields = TypedOperation.forMethod(FieldUtils.class.getMethod("getAllFields", Class.class));

                int index = 0;

                Sequence sBase = new Sequence();

                sBase = sBase.extend(TobjenesisStd);//0
                int indexTobjenesisStd = index++;

                sBase = sBase.extend(TclassName);//1
                int indexTclassName = index++;

                sBase = sBase.extend(TobjectClassReflection, sBase.getVariable(indexTclassName));//2 -> 1
                int indexTobjectClassReflection = index++;

                sBase = sBase.extend(TnewOb, sBase.getVariable(indexTobjenesisStd), sBase.getVariable(indexTobjectClassReflection));//3
                int indexTnewOb = index++;

                sBase = sBase.extend(Tfields, sBase.getVariable(indexTobjectClassReflection));//4
                int indexTfields = index++;

                Field[] fields = FieldUtils.getAllFields(object.getClass());


                for(int i = 0; i < fields.length; i++)
                {
                    fields[i].setAccessible(true);
                    Object o = fields[i].get(object);


                    TypedOperation Telem1 = TypedOperation.createPrimitiveInitialization(JavaTypes.INT_TYPE, i);
                    TypedOperation Tgetfield = TypedOperation.forMethod(RandoopUtils.class.getMethod("getFiled", java.lang.reflect.Field[].class, int.class));
                    TypedOperation Ttrue = TypedOperation.createPrimitiveInitialization(JavaTypes.BOOLEAN_TYPE, true);
                    TypedOperation TsetAccessible = TypedOperation.forMethod(Field.class.getMethod("setAccessible", boolean.class)); //TODO maybe we dont need it if we did it already from JAVA code?
                    TypedOperation Telem8 = TypedOperation.createPrimitiveInitialization(JavaTypes.INT_TYPE, 8);
                    TypedOperation TfieldSet = TypedOperation.forMethod(Field.class.getMethod("set", Object.class, Object.class));

                    sBase = sBase.extend(Telem1);//5
                    int indexTelem1 = index++;

                    sBase = sBase.extend(Tgetfield, sBase.getVariable(indexTfields), sBase.getVariable(indexTelem1));//6
                    int indexTgetfield = index++;

                    sBase = sBase.extend(Ttrue);//7
                    int indexTtrue = index++;

                    sBase = sBase.extend(TsetAccessible, sBase.getVariable(indexTgetfield), sBase.getVariable(indexTtrue));//8
                    int indexTsetAccessible = index++;

                    Sequence sInner = createSequenceForObject(o);

                    sBase = Sequence.concatenate(Arrays.asList(sBase,sInner));

                    Variable vv = sBase.getLastVariable();
                    index = sBase.statements.size();

                  //  sBase = sBase.extend(Telem8);//9 //TODO here concate
                    sBase = sBase.extend(TfieldSet, sBase.getVariable(indexTgetfield), sBase.getVariable(indexTnewOb) , sBase.getVariable(index-1)  );//10
                    index++;

                }
                return sBase;

            }

        }
    }

    public static Object newInstance(ObjenesisStd objenesisStd, Class<?> lass)
    {
        return objenesisStd.newInstance(lass);
    }

    public static Field getFiled(Field[] fields, int id)
    {
        return fields[id];
    }
}
