package modbat.genran;

import modbat.trace.RecordedTransition;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Class responsible for storing objects in memory. Bridge between modbat and randoop object shearing.
 */
public class ObjectHolder {

    private static final HashMap<Class, List<GenranObject>> objectsMap = new HashMap<>();

    /**
     * Functions responsible for adding a new object to memory
     *
     * @param newObject any object you want to store in memory
     */
    public synchronized static void add(Object newObject, scala.collection.immutable.List<RecordedTransition> transisions) {
        if (newObject == null)
            return;

        List<GenranObject> objectList = objectsMap.computeIfAbsent(newObject.getClass(), k -> new ArrayList<>());
        objectList.add(new GenranObject(newObject, transisions));
    }

    /**
     * Pick a random object from memory
     *
     * @param className name of the class of the object you want to return from memory
     * @return a random object from a memory of a given type
     * @throws ClassNotFoundException
     */
    public synchronized static Object pick(String className, int id) throws ClassNotFoundException {

        List<GenranObject> objectSet = objectsMap.get(Class.forName(className));

        validate(id, objectSet);

        return objectSet.get(id).getObject();
    }


    static scala.collection.immutable.List<RecordedTransition> getRecordedTransitions(String className, int id) throws ClassNotFoundException {

        List<GenranObject> objectSet = objectsMap.get(Class.forName(className));

        validate(id, objectSet);

        return objectSet.get(id).getTransitions();
    }

    private static void validate(int id, List<GenranObject> objectSet) {
        if (objectSet == null || id > objectSet.size() || id < 0) {
            throw new InvalidParameterException(String.format("Invalid parameter; className: %s id: %s", objectSet, id));
        }
    }

    static Set<Class> getObjectsMapKeys() {
        return objectsMap.keySet();
    }


    static int getSizeOfKeySupSet(Class c) {
        return objectsMap.get(c).size();
    }
}

