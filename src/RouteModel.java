import edu.princeton.cs.algs4.Point2D;

import java.awt.*;
import java.util.*;
import java.util.List;

public class RouteModel extends Model {

    public static class RMNode extends Node {
        protected int index;
        protected RouteModel parentModel;
        protected RMNode prev;
        protected float hVal = Float.MAX_VALUE;
        protected float gVal = 0.0f;
        protected boolean visited = false;
        protected List<RMNode> neighbors = new ArrayList<>();

        public int getIndex() { return index; }

        public RMNode( int idx, RouteModel parentModel, Node node) {
            super(node);
            this.parentModel = parentModel;
            this.index = idx;
        }

        public RMNode(double lon, double lat){
            super(lon, lat);
        }

        public void findNeighbors() {
            // find all the Ways where the curr road belongs
            List<Road> roads = parentModel.getRoadToWay().get(this.getRef());
            if (roads == null)
                return;

            for (Road road : roads) {
                List<String> nodes = parentModel.getWays().get(road.getRef()).nodes;
                RMNode newNeighbor = findNeighbor(nodes);
                if (newNeighbor != null) {
                    this.neighbors.add(newNeighbor);
                }

            }
        }

        public List<String> nextListElements(List<String> nodeRefs){
            List<String> nextNodes = new ArrayList<>();
            boolean found = false;

            for(String ref: nodeRefs){
                if(found){
                    nextNodes.add(ref);
                }
                else if(this.getRef().compareTo(ref) == 0){
                    found = true;
                }
            }
            return nextNodes;
        }

        public RMNode findNeighbor(List<String> nodeRefs) {
            RMNode closestNode = null;

            List<String> nextNodes = nextListElements(nodeRefs);

            for (String ref : nextNodes) {
                RMNode node = parentModel.getRouteModelNodes().get(ref);
                if (this.getRef().compareTo(ref) != 0 && !node.visited) {
                    if (closestNode == null || this.manhattanDist(node) < this.manhattanDist(closestNode)) {
                        closestNode = parentModel.getRouteModelNodes().get(ref);
                    }
                }
            }
            return closestNode;
        }

        double manhattanDist(RMNode node){
//            return Math.sqrt(Math.pow(node.getLon() - getLon(), 2) + Math.pow(node.getLat() - getLat(), 2));
            return Math.abs(node.getLon() - getLon()) + Math.abs(node.getLat() - getLat());
        }

        @Override
        public String toString() {
            return getRef() +" (" + getLat() + ", " + getLon() + ")";
        }
    }

    protected List<RMNode> path = new ArrayList<>();
    // routeModelNodes is a map with all the existing RMNodes, sorted by their node_id
    private final SortedMap<String, RMNode> routeModelNodes = new TreeMap<>();
    // roadToNodes links a road id with all the ways it belongs
    private final Map<String, List<Road>> roadToWay = new HashMap<>();

    protected KDTree nodesTree = new KDTree();
    private final SortedMap<Point2D, String> pointToRef = new TreeMap<>();

    public RouteModel(List<Byte> xml) {
        super(xml);
        createRouteModelNodes();
        createHashmap();
    }

    public Map<String, List<Road>> getRoadToWay() { return roadToWay; }
    public SortedMap<String, RMNode> getRouteModelNodes(){ return routeModelNodes; }

    private void createRouteModelNodes() {
        int counter = 0;
        for (Map.Entry<String, Node> entry : getNodes().entrySet()) {
            Node node = entry.getValue();
            RMNode newRmNode = new RMNode(counter, this, node);
            routeModelNodes.put(newRmNode.getRef(), newRmNode);
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
                    if (!roadToWay.containsKey(node_idx)) {
                        roadToWay.put(node_idx, new ArrayList<>());
                    }
                    // Add the road to the list of roads for this node
                    roadToWay.get(node_idx).add(road);
                    RouteModel.RMNode roadNode = getRouteModelNodes().get(node_idx);
                    Point2D point = new Point2D(roadNode.getLon(), roadNode.getLat());
                    nodesTree.insert(point);
                    pointToRef.put(point, roadNode.getRef());
                }
            }
        }
    }

    public RMNode searchNode (String ref){
        for(RMNode node: getRouteModelNodes().values()){
            if(node.getRef().compareTo(ref) == 0)  {
                return node;
            }
        }
        return null;
    }

    public RMNode findClosestNode(float lon, float lat) {

        Point2D closestPoint = nodesTree.nearest(new Point2D(lon, lat));
        String ref = pointToRef.get(closestPoint);
        System.out.println("Nearest: " + closestPoint + " and ref: " + ref);
        RMNode node = routeModelNodes.get(ref);
        return node;
    }
}
