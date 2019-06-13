package modbat.genran;

import java.util.List;
import java.util.SplittableRandom;


//TODO change List to map and store every type of object
class ObjectHolder
{
    private static List<Object> objectList;
    private static final SplittableRandom sr = new SplittableRandom(); // TODO use modbat way

    public static void set(List<Object> newObject)
    {
        objectList = newObject;
    }

    /**
     * Function need to be public for randoop to use it
     * @return
     */
    public synchronized static Object pop()
    {
        return objectList.get(sr.nextInt(objectList.size())); //TODO add conditions
    }
}
