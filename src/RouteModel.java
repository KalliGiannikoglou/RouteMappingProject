import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteModel extends Model {

    public class RMNode extends Node {
        protected int index;
        protected RouteModel parentModel;
        protected Node prev;
        protected float hVal = Float.MAX_VALUE;
        protected float gVal = 0.0f;
        protected boolean visited = false;
        protected List<Node> neighbors = new ArrayList<>();

        public int getIndex() { return index; }

        public RMNode( int idx, RouteModel parentModel, Node node) {
            super(node);
            this.parentModel = parentModel;
            this.index = idx;
        }

        public RMNode (double lon, double lat){
            super(lon, lat);
        }

        public void findNeighbors() {
            List<Road> roads = parentModel.getNodeToRoad().get(this.getRef());
            if (roads == null)
                return;

            for (Road road : roads) {
                List<String> nodes = parentModel.getWays().get(road.getRef()).nodes;
                Node newNeighbor = findNeighbor(nodes);
                if (newNeighbor != null)
                    this.neighbors.add(newNeighbor);
            }
        }

        public RMNode findNeighbor(List<String> nodeIndices) {
            RMNode closestNode = null;

            for (String nodeIndex : nodeIndices) {
                RMNode node = parentModel.getRouteModelNodes().get(getIndex());
                if (this.manhattanDist(node) != 0 && !node.visited) {
                    if (closestNode == null || this.manhattanDist(node) < this.manhattanDist(closestNode)) {
                        closestNode = parentModel.getRouteModelNodes().get(getIndex());;
                    }
                }
            }
            return closestNode;
        }

        double manhattanDist(RMNode node){
            return Math.abs(node.getLon() - getLon()) + Math.abs(node.getLat() - getLat());
        }

        @Override
        public String toString() {
            return getRef() +" (" + getLon() + ", " + getLat() + ")";
        }
    }

    private final List<RMNode> routeModelNodes = new ArrayList<>();
    private final Map<String, List<Road>> nodeToRoad = new HashMap<String, List<Road>>();

    public RouteModel(List<Byte> xml) {
        super(xml);
        createRouteModelNodes();
        createHashmap();
    }

    public Map<String, List<Road>> getNodeToRoad() { return nodeToRoad; }

    public List<RMNode> getRouteModelNodes(){ return routeModelNodes; }


    private void createRouteModelNodes() {
        int counter = 0;
        for (Map.Entry<String, Node> entry : getNodes().entrySet()) {
            Node node = entry.getValue();
            RMNode newRmNode = new RMNode(counter, this, node);
            routeModelNodes.add(newRmNode);
            counter++;
        }
    }

    protected void createHashmap() {
        // Iterate over all roads
        for (Road road : getRoads().values()) {
            if (road.getType() != Road.Type.Footway) {
                // Iterate over all nodes of the current road
                for (String node_idx : getWays().get(road.getRef()).nodes) {
                    // If the node is not in the hashmap, create a new list for it
                    if (!nodeToRoad.containsKey(node_idx)) {
                        nodeToRoad.put(node_idx, new ArrayList<>());
                    }
                    // Add the road to the list of roads for this node
                    nodeToRoad.get(node_idx).add(road);
                }
            }
        }
        // Iterate over nodeToRoad "node_id: (type, wayNum, wayId) , ( ) ..."
//        nodeToRoad.forEach((key, roadList) -> {
//            // Print key
//            System.out.print(key + ": ");
//
//            // Print list of roads using streams
//            roadList.forEach(road -> System.out.print(road.toString() + ", "));
//
//            // Move to the next line for the next entry
//            System.out.println();
//        });
    }

    public RMNode searchNode (String ref){
        for(RMNode node: getRouteModelNodes()){
            if(node.getRef().compareTo(ref) == 0)  {
                return node;
            }
        }
        return null;
    }

    public RMNode findClosestNode(float lon, float lat) {
        RMNode input = new RMNode(lon, lat);

        float minDist = Float.MAX_VALUE;
        double dist;
        int closestIdx = -1;

        for ( Road road : getRoads().values()) {
            if (road.getType() != Road.Type.Footway) {
                for (String nodeId : getWays().get(road.getRef()).getNodes()) {
                    RMNode temp = searchNode(nodeId);
                    dist = input.manhattanDist(temp);
                    if (dist < minDist) {
                        closestIdx = temp.getIndex();
                        minDist = (float) dist;
                        System.out.println("Min Distance: " + minDist);
                    }
                }
            }
        }
        return getRouteModelNodes().get(closestIdx);
    }

}
