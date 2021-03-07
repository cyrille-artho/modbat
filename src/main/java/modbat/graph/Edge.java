package modbat.graph;

import java.util.Objects;

/**
 * This class represents an edge used in {@link Graph}.
 */
public class Edge<NT, ET> {
    private Node<NT> from;
    private Node<NT> to;
    private ET data;

    public Edge(Node<NT> from, Node<NT> to) {
        this.from = from;
        this.to = to;
        this.data = null;
    }

    public Edge(Node<NT> from, Node<NT> to, ET data) {
        this.from = from;
        this.to = to;
        this.data = data;
    }


    public Node<NT> getDestination() {
        return to;
    }

    public Node<NT> getSource() {
        return from;
    }

    public ET getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge<?, ?> that = (Edge<?, ?>) o;

        return Objects.equals(this.from, that.from) &&
                Objects.equals(this.to, that.to) &&
                Objects.equals(this.data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, data);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
