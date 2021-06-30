package modbat.graph;

import java.util.Objects;

/**
 * This class represents a node used in {@link Graph}.
 */
public class Node<NT> {
    private NT data;

    public Node(NT data) {
        this.data = data;
    }

    public NT getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> that = (Node<?>) o;
        return Objects.equals(this.data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return data.toString() ;
    }
}
