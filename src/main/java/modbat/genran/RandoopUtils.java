package modbat.genran;

import com.sun.tools.javac.util.Pair;
import randoop.operation.TypedOperation;
import randoop.sequence.Sequence;
import randoop.types.JavaTypes;
import randoop.types.Type;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandoopUtils {

    /**
     * Function that creates a randoop sequence for each type of Objects in @ObjectHolder memory
     * @return a list of sequences for each type of objects
     * @throws Exception
     */
    public static List<Sequence> createSequencesForObjectHolder() throws Exception {

        List<Sequence> sequenceList = new ArrayList<>();

        TypedOperation pop = TypedOperation.forMethod(ObjectHolder.class.getMethod("pick", String.class, int.class));

        Set<Class> objectsMapKeys = ObjectHolder.getObjectsMapKeys();

        for(Class c : objectsMapKeys)
        {
            Sequence sBase = new Sequence();

            TypedOperation className = TypedOperation.createPrimitiveInitialization(JavaTypes.STRING_TYPE, c.getName());
            sBase = sBase.extend(className);

            for(int i = 0; i < ObjectHolder.getSizeOfKeySupSet(c); i++) {

                TypedOperation randomSeed =  TypedOperation.createPrimitiveInitialization(JavaTypes.INT_TYPE, i);
                Sequence sSub = sBase.extend(randomSeed);

                sSub = sSub.extend(pop, sSub.getVariable(0), sSub.getVariable(1));

                TypedOperation cast = TypedOperation.createCast(Type.forClass(Object.class), Type.forClass(c));
                sSub = sSub.extend(cast, sSub.getVariable(2));

                sequenceList.add(sSub);
            }
        }

        return sequenceList;
    }


    public static Optional<Pair<String,Integer>> getId(String text) {
        Pattern pattern = Pattern.compile("modbat\\.genran\\.ObjectHolder\\.pick\\(\"(.*?)\", ([0-9]*)\\);");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return Optional.of(new Pair<>(matcher.group(1), Integer.valueOf(matcher.group(2))));
        }

        return Optional.empty();
    }
}
