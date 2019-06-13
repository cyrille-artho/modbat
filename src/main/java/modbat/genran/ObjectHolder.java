package modbat.genran;

import java.util.*;


//TODO change List to map and store every type of object
public class ObjectHolder
{
    private static HashMap<Class, Set<Object>> objectsMap = new HashMap<>();

    private static final SplittableRandom sr = new SplittableRandom(); // TODO use modbat way

    public synchronized static void add(Object newObject)
    {
        Set<Object> objectList = objectsMap.computeIfAbsent(newObject.getClass(), k -> new HashSet<>());

        objectList.add(newObject);
    }

    /**
     * Function need to be public for randoop to use it
     * @return
     */
    public synchronized static Object pop()
    {
        Set<Class> classSet = objectsMap.keySet();

        if(classSet.size() == 1)
            return pop(classSet.iterator().next());

        throw new IllegalArgumentException("More then 1 object");
    }

    public synchronized static Object pop(Class c) //TODO add param as String
    {
        Set<Object> objectSet = objectsMap.get(c);

        return objectSet.stream().skip(sr.nextInt(objectSet.size())).findAny().get(); //TODO add conditions
    }

    public static Class<?> getClassName() {

        Set<Class> classSet = objectsMap.keySet();

        if(classSet.size() == 1)
            return classSet.iterator().next().getClass();

        throw new IllegalArgumentException("More then 1 object");
    }
}
