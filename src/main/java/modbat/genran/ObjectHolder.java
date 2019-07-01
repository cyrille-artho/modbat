package modbat.genran;

import com.sun.tools.javac.util.Pair;
import modbat.trace.RecordedTransition;
import scala.collection.mutable.ListBuffer;

import java.util.*;

/**
 * Class responsible for storing objects in memory. Bridge between modbat and randoop object shearing.
 */
public class ObjectHolder {

    private static final HashMap<Class, List<Pair<Object, ListBuffer<RecordedTransition>>>> objectsMap = new HashMap<>();

    /**
     * Functions responsible for adding a new object to memory
     *
     * @param newObject any object you want to store in memory
     */
    public synchronized static void add(Object newObject, ListBuffer<RecordedTransition> transisions) {
        if (newObject == null)
            return;

        List<Pair<Object, ListBuffer<RecordedTransition>>> objectList = objectsMap.computeIfAbsent(newObject.getClass(), k -> new ArrayList<>());
        objectList.add(new Pair<>(newObject, transisions));
    }

    /**
     * Pick a random object from memory
     *
     * @param className name of the class of the object you want to return from memory
     * @return a random object from a memory of a given type
     * @throws ClassNotFoundException
     */
    public synchronized static Object pick(String className, int id) throws ClassNotFoundException {

        List<Pair<Object, ListBuffer<RecordedTransition>>> objectSet = objectsMap.get(Class.forName(className)); //TODO add exceptions

        return objectSet.get(id).fst;
    }

    /**
     * @return a set of all different types Classes in memory
     */
    static Set<Class> getObjectsMapKeys() {
        return objectsMap.keySet();
    }

    /**
     *
     * @param c
     * @return
     */
    static int getSizeOfKeySupSet(Class c) {
        return objectsMap.get(c).size();
    }

    static ListBuffer<RecordedTransition> getRecordedTransitions(String className, int id) {
        return objectsMap.get(className).get(id).snd;
    }
}

