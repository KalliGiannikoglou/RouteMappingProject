import java.util.ArrayList;
import java.util.List;

public class Way {
    protected final List<String> nodes = new ArrayList<>();
    private final String wayId;
    private int wayNum;
    private boolean oneWay = false;

    public Way(String id, int num) {

        this.wayId = id;
        this.wayNum = num;
    }

    public void setDirection(boolean oneWay){ this.oneWay = oneWay; }
    public boolean isOneWay() { return this.oneWay; }

    public List<String> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(wayNum).append(" OneWay: ").append(oneWay).append(" (");
        for (String nodeId : nodes) {
            sb.append(nodeId).append(" - ");
        }
        sb.append(")");
        return sb.toString();
    }
}
