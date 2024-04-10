import java.util.List;

public class Water extends MultiPolygon {
    public Water(List<Integer> outer) {
        super(outer);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" (");
        for (Integer i : outer) {
            sb.append(i).append(" - ");
        }
        sb.append(")");
        return sb.toString();
    }
}
