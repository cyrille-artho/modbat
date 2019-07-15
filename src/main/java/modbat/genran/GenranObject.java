package modbat.genran;

import modbat.trace.RecordedTransition;
import scala.collection.immutable.List;

/**

 */
public class GenranObject {

    private Object object;
    private List<RecordedTransition> transitions;

    GenranObject(Object object, List<RecordedTransition> transitions) {
        this.object = object;
        this.transitions = transitions;
    }

    public Object getObject() {
        return object;
    }

    public List<RecordedTransition> getTransitions() {
        return transitions;
    }
}
