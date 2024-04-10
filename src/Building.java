import java.util.List;

public class Building extends MultiPolygon {
    public Building(List<Integer> outer) {
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
