import edu.princeton.cs.algs4.Point2D;

import java.util.*;

public class AgentDistribution {
    int numOfAgents;
    int totalPkgs;
    int maxCapacity;
    KdTree<KdTree.XYZPoint> packages = new KdTree<>();
    SortedMap<KdTree.XYZPoint, Integer> assignedPkgs = new TreeMap<>();
    List<List<KdTree.XYZPoint>> agents = new ArrayList<>();

    public AgentDistribution(int nAgents){
        this.numOfAgents = nAgents;
        this.totalPkgs = 0;
        this.maxCapacity = 0;
    }

    public List<List<KdTree.XYZPoint>> getAgents(){ return agents; }

    public void assignNewPackage(Point2D source, Point2D destination){

        KdTree.XYZPoint xySource = new KdTree.XYZPoint(source.x(), source.y());
        KdTree.XYZPoint xyDestination = new KdTree.XYZPoint(destination.x(), destination.y());

        // get a copy of the current tree of packages
        KdTree tempTree = new KdTree(packages);
        List<KdTree.XYZPoint> nearestPkg = (List<KdTree.XYZPoint>) tempTree.nearestNeighbourSearch(1, xySource);

        Boolean added = false;
        while (!added){
            // for the first pkg, assign it to the first agent
            if(nearestPkg.isEmpty()){
                // create new list
                List<KdTree.XYZPoint> list = new ArrayList<>();
                addPkg(xySource, list);
                addPkg(xyDestination, list);
                totalPkgs++;
                added = true;
            }
            else{
                int agentNum = assignedPkgs.get(nearestPkg.get(0));
                List<KdTree.XYZPoint> agentsPkgs = agents.get(agentNum);

                // we define a maxCapacity which changes dynamically every time we add pakgs
                maxCapacity = totalPkgs / numOfAgents;
                // we have to apply /2, since every pkg has 2 points in the list (src and dest)
                if( (agentsPkgs.size()/2) <= maxCapacity){
                    addPkg(xySource, agentsPkgs);
                    addPkg(xyDestination, agentsPkgs);
                    totalPkgs++;
                    added = true;
                }

                // find all the packets that belong to current agent and remove them from the list
                for (KdTree.XYZPoint point : agentsPkgs)
                    tempTree.remove(point);

                nearestPkg = (List<KdTree.XYZPoint>) tempTree.nearestNeighbourSearch(1, xySource);
            }
        }
    }

    public void addPkg(KdTree.XYZPoint xySource, List<KdTree.XYZPoint> agentList){

        packages.add(xySource);
        agentList.add(xySource);

        // if size = 1, this is the first element added. thus the list must be inserted in list of lists
        if(agentList.size() == 1){
            agents.add(agentList);
        }
        assignedPkgs.put(xySource, agents.indexOf(agentList));
    }

    public void displayAgents(){

        for(List<KdTree.XYZPoint> agent: agents)
            System.out.println("Agent " + agents.indexOf(agent) + ": " + agent);
    }

}
