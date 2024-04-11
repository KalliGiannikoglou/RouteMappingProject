import java.util.ArrayList;
import java.util.List;

public class RoutePlanning {
    private List<RouteModel.RMNode> openList;
    private RouteModel.RMNode startNode;
    private RouteModel.RMNode endNode;
    private float distance = 0.0f;
    private RouteModel rmModel;

    public RoutePlanning(RouteModel model, float startX, float startY, float endX, float endY) {
        // Convert inputs to percentage
        startX *= 0.01f;
        startY *= 0.01f;
        endX *= 0.01f;
        endY *= 0.01f;

        // Initialize the RMModel attribute
        this.rmModel = model;
        System.out.println("StartX: " + startX + ", Start Y: " + startY);


        // Use the mModel.findClosestNode method to find the closest nodes to the starting and ending coordinates
        this.startNode = rmModel.findClosestNode( startX, startY);
        this.endNode = rmModel.findClosestNode(endX, endY);

        System.out.println(startNode.getRef());
    }

//    public float getDistance() {
//        // Getter for distance
//        return distance;
//    }
//
//    public void aStarSearch() {
//        // A* search implementation
//    }
//
//    public void addNeighbors(RouteModel.RMNode currentNode) {
//        // Method to add neighbors
//    }
//
//    public float calculateHValue(RouteModel.RMNode node) {
//        // Method to calculate heuristic value
//        return 0.0f;
//    }
//
//    public List<RouteModel.RMNode> constructFinalPath(RouteModel.RMNode current) {
//        // Method to construct final path
//        return new ArrayList<>();
//    }
//
//    public RouteModel.RMNode nextNode() {
//        // Method to find the next node
//        return null;
//    }
}