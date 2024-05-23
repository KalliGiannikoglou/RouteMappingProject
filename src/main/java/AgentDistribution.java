import java.util.*;

public class AgentDistribution {
    int numOfAgents;
    int totalPkgs;
    int loadFactor;
    int MAX_CAPACITY;

    KdTree<KdTree.XYZPoint> packages = new KdTree<>();
    SortedMap<KdTree.XYZPoint, Integer> assignedPkgs = new TreeMap<>();
    List<List<KdTree.XYZPoint>> agents = new ArrayList<>();

    public AgentDistribution(int nAgents, int max_capacity){
        this.numOfAgents = nAgents;
        this.totalPkgs = 0;
        this.loadFactor = 0;
        MAX_CAPACITY = max_capacity;
    }

    public List<List<KdTree.XYZPoint>> getAgents(){ return agents; }

    public void assignNewPackage(KdTree.XYZPoint xySource, KdTree.XYZPoint xyDestination){

        // get a copy of the current tree of packages
        KdTree tempTree = new KdTree(packages);
        List<KdTree.XYZPoint> nearestPkg = (List<KdTree.XYZPoint>) tempTree.nearestNeighbourSearch(1, xySource);

        boolean added = false;
        while (!added){
            // for the first pkg, assign it to the first agent
            if(nearestPkg.isEmpty()){
                // TO BE ADJUSTED
                if(agents.size() == numOfAgents){
                    System.out.println("No agent is available at the moment!");
                    return;
                }
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
                loadFactor = totalPkgs / numOfAgents;
                // we have to apply /2, since every pkg has 2 points in the list (src and dest)
                int currPkgs = agentsPkgs.size()/2;
                if( currPkgs < MAX_CAPACITY && currPkgs <= loadFactor){
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

    public void addAgentsPosition(KdTree.XYZPoint initPoint){
        packages.add(initPoint);
    }

    public void displayAgents(){

        for(List<KdTree.XYZPoint> agent: agents)
            System.out.println("Agent " + agents.indexOf(agent) + ": " + agent);
    }

}
