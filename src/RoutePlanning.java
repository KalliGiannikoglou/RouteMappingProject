import edu.princeton.cs.algs4.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class RoutePlanning {
    private final List<RouteModel.RMNode> openList = new ArrayList<>();
    private final RouteModel.RMNode startNode;
    private final RouteModel.RMNode endNode;
    private float distance = 0.0f;
    protected final RouteModel rmModel;

    public RoutePlanning(RouteModel model, float startX, float startY, float endX, float endY) {
        // Convert inputs to percentage
//        startX *= 0.01f;
//        startY *= 0.01f;
//        endX *= 0.01f;
//        endY *= 0.01f;

        // Initialize the RMModel attribute
        this.rmModel = model;

        // Use the mModel.findClosestNode method to find the closest nodes to the starting and ending coordinates
        this.startNode = rmModel.findClosestNode( startX, startY);
        this.endNode = rmModel.findClosestNode(endX, endY);

        AStarSearch();
    }

    //The H value of every node is their distance from the end node
    public double calculateHValue(RouteModel.RMNode node) {
        return node.manhattanDist(this.endNode);
    }

    public void addNeighbors(RouteModel.RMNode currentNode) {

        currentNode.findNeighbors();

        for (RouteModel.RMNode neighbor : currentNode.neighbors) {
            neighbor.prev = currentNode;
            neighbor.gVal = (float) (currentNode.gVal + neighbor.manhattanDist(currentNode));
            neighbor.hVal = (float) calculateHValue(neighbor);
            neighbor.visited = true;
            openList.add(neighbor);
        }
    }

    public RouteModel.RMNode nextNode() {
        // Sort with a decreasing order
        this.openList.sort((v1, v2) -> Float.compare(v2.hVal + v2.gVal, v1.hVal + v1.gVal));

        return this.openList.remove(this.openList.size() - 1);
    }

    public List<RouteModel.RMNode> constructFinalPath(RouteModel.RMNode currNode) {

        System.out.println("Final Path!");
        // dist of the found path
        distance = 0.0f;
        // Create pathFound list
        List<RouteModel.RMNode> pathFound = new ArrayList<>();

        pathFound.add(currNode);
        while (currNode.getLon() != this.startNode.getLon() &&
                currNode.getLat() != this.startNode.getLat()) {
            distance += currNode.manhattanDist( currNode.prev);
            pathFound.add(currNode.prev);
            currNode = currNode.prev;
        }
        Collections.reverse(pathFound);

        // Multiply the distance by the scale of the map to get meters
        distance *= rmModel.getMetricScale();

        System.out.println("Path found: " + pathFound);
        System.out.println("Distance calculated: " + distance);
        return pathFound;
    }

    public void AStarSearch() {

        this.startNode.visited = true;
        this.openList.add(this.startNode);

        while (!this.openList.isEmpty()) {
            RouteModel.RMNode next_node = nextNode();
            if (next_node.getLon() == this.endNode.getLon() &&
                    next_node.getLat() == this.endNode.getLat()) {
                rmModel.path = constructFinalPath(next_node);
                MapDisplay map = new MapDisplay();
                map.googleMapsDisplay(rmModel.path);
                break;
            } else {
                addNeighbors(next_node);
            }
        }
    }
}
