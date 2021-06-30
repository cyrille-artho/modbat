package modbat.graph.testrequirements;

import modbat.graph.Edge;

import java.util.Objects;

/**
 * Edge test requirement. Two edge test requirements are considered equal
 * if the edges stored in them are equal regardless of their "covered" status.
 */
public class EdgeTestRequirement<NT, ET> extends AbstractTestRequirement {
    private final Edge<NT, ET> edge;

    public EdgeTestRequirement(Edge<NT, ET> edge) {
        super();
        if (edge == null) {
            throw new IllegalArgumentException("edge is null");
        }

        this.edge = edge;
    }

    public Edge<NT, ET> edge() {
        return edge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeTestRequirement<?, ?> that = (EdgeTestRequirement<?, ?>) o;
        return Objects.equals(this.edge, that.edge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edge);
    }
}
