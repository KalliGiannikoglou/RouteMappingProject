import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Way {
    protected final List<String> nodes = new ArrayList<>();
    private final String wayId;
    private int wayNum;

    public Way(String id, int num) {

        this.wayId = id;
        this.wayNum = num;
    }

    public List<String> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(wayId).append(" / ").append(wayNum).append(" (");
        for (String nodeId : nodes) {
            sb.append(nodeId).append(" - ");
        }
        sb.append(")");
        return sb.toString();
    }
}
