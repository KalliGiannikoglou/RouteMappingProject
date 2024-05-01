import edu.princeton.cs.algs4.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoutePlanning {
    private final List<RouteModel.RMNode> openList = new ArrayList<>();
    private final RouteModel.RMNode startNode;
    private final List<RouteModel.RMNode> destPoints = new ArrayList<>();
    protected final RouteModel rmModel;

    public RoutePlanning(RouteModel model, Point2D start, List<Point2D> end) {

        // Initialize the RMModel attribute
        this.rmModel = model;

        // ################ not optimal at the moment, will change in phase 3 ################
        this.startNode = rmModel.findClosestNode( (float) start.x(), (float) start.y());
        for(Point2D node : end)
            this.destPoints.add(rmModel.findClosestNode((float) node.x(), (float) node.y()));

        // Create a list with start node first and all the destination nodes after
        List<RouteModel.RMNode> allNodes = new ArrayList<>();
        allNodes.add(this.startNode);
        allNodes.addAll(this.destPoints);

        for(RouteModel.RMNode currNode: allNodes) {
            currNode.isEndNode = true;
            // Find all the distances with one specific node
            for (RouteModel.RMNode other : allNodes) {
                currNode.distances.add((float) currNode.manhattanDist(other));
            }
        }

        // find optimal tour
        List<RouteModel.RMNode> tour = findTraversalOrder(allNodes);

        for(int i = 0; i < tour.size() - 1 ; i++){
            // Clear the list before starting a new iteration
            this.openList.clear();
            // make null first node's prev to mark separate tours
            tour.get(i).prev = null;

            AStarSearch(tour.get(i), tour.get(i+1));
        }
        MapDisplay map = new MapDisplay();
        map.googleMapsDisplay(rmModel.path);

    }

    // Heuristic approach on TSP. Find a nearly optimal traversal of all the destination points provided.
    //The TSP is equivalent to finding a minimum-weight perfect matching in a complete graph
    // where the weight of an edge is the distance between two destination points.
    public List<RouteModel.RMNode> findTraversalOrder(List<RouteModel.RMNode> nodes){

        // Empty list to store the final optimal tour
        List<RouteModel.RMNode> optTour = new ArrayList<>();
        // Duplicate of the initial list to keep track of the available destinations
        List<RouteModel.RMNode> toVisit = new ArrayList<>(nodes);

        RouteModel.RMNode minNode = null;
        RouteModel.RMNode curr  = toVisit.get(0);
        curr.checked = true;

        while( !toVisit.isEmpty()){
            Float minDist = Float.MAX_VALUE;
            for(Float temp: curr.distances){
                // Find min of curr nodes distances
                if(temp < minDist){
                    int index = curr.distances.indexOf(temp);
                    //Check if it is already added in the list
                    if( !nodes.get(index).checked){
                        minNode = nodes.get(index);
                        minDist = temp;
                    }
                }
            }
            // Add the node with min distance in the tour and check their distances with the remaining nodes
            optTour.add(curr);
            toVisit.remove(curr);
            curr = minNode;
            minNode.checked = true;
        }

        return optTour;
    }

    //The H value of every node is their distance from the end node
    public double calculateHValue(RouteModel.RMNode currNode, RouteModel.RMNode endNode) {
        return currNode.manhattanDist(endNode);
    }

    public void addNeighbors(RouteModel.RMNode currentNode, RouteModel.RMNode endNode) {

        currentNode.findNeighbors();

        for (RouteModel.RMNode neighbor : currentNode.neighbors) {
            neighbor.prev = currentNode;
            neighbor.gVal = (float) (currentNode.gVal + neighbor.manhattanDist(currentNode));
            neighbor.hVal = (float) calculateHValue(neighbor, endNode);
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

        // Create pathFound list
        List<RouteModel.RMNode> pathFound = new ArrayList<>();

        pathFound.add(currNode);
        while (currNode.prev != null) {
            pathFound.add(currNode.prev);
            currNode = currNode.prev;
        }
        Collections.reverse(pathFound);

        return pathFound;
    }

    public void AStarSearch(RouteModel.RMNode startNode, RouteModel.RMNode endNode) {

        System.out.println("Start: " + startNode + ", End: " + endNode);
        this.startNode.visited = true;
        this.openList.add(startNode);
        while (!this.openList.isEmpty()) {

            RouteModel.RMNode nextNode = findNextNode();
            if (nextNode.getLon() == endNode.getLon() && nextNode.getLat() == endNode.getLat()) {

                List<RouteModel.RMNode> shortPath = constructFinalPath(nextNode);
                rmModel.path.addAll(shortPath);
                System.out.println("Short path: " + shortPath);
                break;
            } else {
                addNeighbors(nextNode, endNode);
            }
        }

        System.out.println("Final pAth: " + rmModel.path);
    }
}
