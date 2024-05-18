
import java.util.List;

public class Landuse extends MultiPolygon {
    private final Type type;

    public Landuse(List<Integer> outer, Type type) {
        super(outer);
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" (").append(type).append(" : ");
        for (Integer i : outer) {
            sb.append(i).append(" - ");
        }
        sb.append(")");
        return sb.toString();
    }

    // Define the Type enum here
    public enum Type {
        Commercial, Construction, Forest, Grass, Industrial, Invalid, Railway, Residential
    }
}
