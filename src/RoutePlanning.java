import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

public class RoutePlanning {
    private final List<RouteModel.RMNode> openList = new ArrayList<>();
    private final RouteModel.RMNode startNode;
    private final RouteModel.RMNode endNode;
    private float distance = 0.0f;
    private final RouteModel rmModel;

    public RoutePlanning(RouteModel model, float startX, float startY, float endX, float endY) {
        // Convert inputs to percentage
//        startX *= 0.01f;
//        startY *= 0.01f;
//        endX *= 0.01f;
//        endY *= 0.01f;

        // Initialize the RMModel attribute
        this.rmModel = model;
        System.out.println("StartX: " + startX + ", Start Y: " + startY);


        // Use the mModel.findClosestNode method to find the closest nodes to the starting and ending coordinates
        this.startNode = rmModel.findClosestNode( startX, startY);
        this.endNode = rmModel.findClosestNode(endX, endY);

        System.out.println(startNode.getRef());
        System.out.println(endNode.getRef());
        addNeighbors(startNode);
    }

    //The H value of every node is their distance from the end node
    public double calculateHValue(RouteModel.RMNode node) {
        return node.manhattanDist(this.endNode);
    }

    public void addNeighbors(RouteModel.RMNode currentNode) {
        currentNode.findNeighbors();
        System.out.println("Neighbours: " + currentNode.neighbors);
        for (RouteModel.RMNode neighbor : currentNode.neighbors) {
            neighbor.prev = currentNode;
            neighbor.gVal = (float) (currentNode.gVal + neighbor.manhattanDist(currentNode));
            neighbor.hVal = (float) calculateHValue(neighbor);
            neighbor.visited = true;
            openList.add(neighbor);
        }

        for (RouteModel.RMNode it : this.openList){
            System.out.println(it.getRef());
        }

        //########### TESTING ##########################
        nextNode();
    }

    public RouteModel.RMNode nextNode() {
        // Sort with a decreasing order
        this.openList.sort(new Comparator<>() {
            @Override
            public int compare(RouteModel.RMNode v1, RouteModel.RMNode v2) {
                return Float.compare(v2.hVal + v2.gVal, v1.hVal + v1.gVal);
            }
        });

        //########### TESTING ##########################
        for(RouteModel.RMNode r :openList)
            System.out.println("hVal: " + r.hVal + " ,gVal: " + r.gVal);
        RouteModel.RMNode next_node = this.openList.remove(this.openList.size() - 1);
        System.out.println(next_node);


        //########### TESTING #########################
        constructFinalPath(next_node);

        return next_node;
    }

    public List<RouteModel.RMNode> constructFinalPath(RouteModel.RMNode currNode) {
        // Create path_found list
        distance = 0.0f;
        List<RouteModel.RMNode> path_found = new ArrayList<>();

        path_found.add(currNode);
        while (currNode.getLon() != this.startNode.getLon() &&
                currNode.getLat() != this.startNode.getLat()) {
            distance += currNode.manhattanDist( currNode.prev);
            path_found.add(currNode.prev);
            currNode = currNode.prev;
        }
        Collections.reverse(path_found);

        // Multiply the distance by the scale of the map to get meters
        distance *= rmModel.getMetricScale();

        System.out.println("Path found: " + path_found);
        System.out.println("Distance calculated: " + distance);
        return path_found;
    }

//    public void aStarSearch() {
//        // A* search implementation
//    }



}
