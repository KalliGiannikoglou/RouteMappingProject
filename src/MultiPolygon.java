import java.util.ArrayList;
import java.util.List;

public class MultiPolygon {
    protected final List<Integer> outer = new ArrayList<>();
    protected final List<Integer> inner = new ArrayList<>();

    public MultiPolygon(List<Integer> outer) {
        this.outer.addAll(outer);
    }

    public void insertOuter(int newOuter) {
        outer.add(newOuter);
    }

    public void insertInner(int newInner) {
        inner.add(newInner);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" Outer: (");
        for (Integer i : outer) {
            sb.append(i).append(" - ");
        }
        sb.append(")");
        return sb.toString();
    }
}
