import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteModel extends Model {

    public static class RMNode extends Node {
        protected int index;
        protected RouteModel parentModel;
        protected Node prev;
        protected float hVal = Float.MAX_VALUE;
        protected float gVal = 0.0f;
        protected boolean visited = false;
        protected List<Node> neighbors = new ArrayList<>();

        public int getIndex() {
            return index;
        }

        public RMNode(int idx, RouteModel searchModel, Node node) {
            super(node);
            index = idx;
            parentModel = searchModel;
        }

        public void findNeighbors() {
            List<Road> roads = parentModel.getNodeToRoad().get(this.index);
            if (roads == null) {
                return;
            }

            for (Road road : roads) {
                List<Integer> nodes = parentModel.getWays().get(road.way).nodes;
                Node newNeighbor = findNeighbor(nodes);
                if (newNeighbor != null) {
                    this.neighbors.add(newNeighbor);
                }
            }
        }

        public RMNode findNeighbor(List<Integer> nodeIndices) {
            RMNode closestNode = null;

            for (int nodeIndex : nodeIndices) {
                RMNode node = parentModel.getSNodes().get(nodeIndex);
                if (this.manhattanDist(node) != 0 && !node.visited) {
                    if (closestNode == null || this.manhattanDist(node) < this.manhattanDist(closestNode)) {
                        closestNode = parentModel.getSNodes().get(nodeIndex);
                    }
                }
            }
            return closestNode;
        }

        double manhattanDist(RMNode node){
            return Math.abs(node.getLon() - getLon()) + Math.abs(node.getLat() - getLat());
        }
    }

    private final List<Node> routeModelNodes = new ArrayList<>();
    private final Map<String, List<Road>> nodeToRoad = new HashMap<String, List<Road>>();

    public RouteModel(List<Byte> xml) {
        super(xml);
        createRouteModelNodes();
        createHashmap();
    }


    private void createRouteModelNodes() {
        int counter = 0;
        for (Map.Entry<String, Node> entry : getNodes().entrySet()) {
            Node node = entry.getValue();
            Node newRmNode = new RMNode(counter, this, node);
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



    public Node findClosestNode(float x, float y) {
        // Implement your logic for finding the closest node here
        return null;
    }

    //    public List<Node> getPath() {
    //        return path;
    //    }
}
