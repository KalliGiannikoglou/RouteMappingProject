import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoutePlanning {
    private final List<RouteModel.RMNode> openList = new ArrayList<>();
    private final RouteModel.RMNode startNode;
    private final RouteModel.RMNode endNode;
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

    public RouteModel.RMNode findNextNode() {
        // Sort with a decreasing order
        this.openList.sort((v1, v2) -> Float.compare(v2.hVal + v2.gVal, v1.hVal + v1.gVal));
        return this.openList.remove(this.openList.size() - 1);
    }

    public List<RouteModel.RMNode> constructFinalPath(RouteModel.RMNode currNode) {

        // dist of the found path
        // Create pathFound list
        List<RouteModel.RMNode> pathFound = new ArrayList<>();

        pathFound.add(currNode);
        while (currNode.getLon() != this.startNode.getLon() && currNode.getLat() != this.startNode.getLat()) {
            pathFound.add(currNode.prev);
            currNode = currNode.prev;
        }
        Collections.reverse(pathFound);



        return pathFound;
    }

    public void AStarSearch() {

        this.startNode.visited = true;
        this.openList.add(this.startNode);
        while (!this.openList.isEmpty()) {
            RouteModel.RMNode nextNode = findNextNode();
            if (nextNode.getLon() == this.endNode.getLon() && nextNode.getLat() == this.endNode.getLat()) {
                rmModel.path = constructFinalPath(nextNode);
                MapDisplay map = new MapDisplay();
                map.googleMapsDisplay(rmModel.path);
                break;
            } else {
                addNeighbors(nextNode);
            }
        }
    }
}
