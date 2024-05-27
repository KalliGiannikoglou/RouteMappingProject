import java.util.*;

public class AgentDistribution {
    int numOfAgents;
    int totalPkgs;
    int loadFactor;
    int MAX_CAPACITY;

    // List with all the initial location of the agents
    List<KdTree.XYZPoint> agentsLocations = new ArrayList<>();
    //KdTree with all the existing packages
    KdTree<KdTree.XYZPoint> packages = new KdTree<>();
    // Sorted map where every point is mapped with all the agents that have this location in their list
    SortedMap<KdTree.XYZPoint, List<Integer>> assignedPkgs = new TreeMap<>();
    // List with the lists of all agents packages
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

            if(nearestPkg.isEmpty()){
                System.out.println("Sorry, no agents available!");
                break;
            }
            // if the packet is near any of the agents
            else if(agentsLocations.contains(nearestPkg.get(0))){
                // create new list and add new package
                List<KdTree.XYZPoint> list = new ArrayList<>();
                addPkg(xySource, list);
                addPkg(xyDestination, list);
                totalPkgs++;
                added = true;
                //System.out.println("Package assigned succesfully!");

                // remove agent's location from the tree and the agent's location list
                boolean removed = packages.remove(nearestPkg.get(0));
                agentsLocations.remove(nearestPkg.get(0));
            }
            else {
                // get the agent number from assignedPakgs list, get the first available item each time
                List<Integer> intList = assignedPkgs.get(nearestPkg.get(0));

                System.out.println("Int list: " + intList);
                for (int agentNum : intList) {

                    List<KdTree.XYZPoint> agentsPkgs = agents.get(agentNum);

                    // we define a loadFactor that changes dynamically every time we add pakgs
                    loadFactor = totalPkgs / numOfAgents;
                    // we have to apply /2, since every pkg has 2 points in the list (src and dest)
                    int currPkgs = agentsPkgs.size() / 2;
                    if (currPkgs < MAX_CAPACITY && currPkgs <= loadFactor) {
                        addPkg(xySource, agentsPkgs);
                        addPkg(xyDestination, agentsPkgs);
                        totalPkgs++;
                        added = true;
                        //System.out.println("Package assigned succesfully!");
                    }

                    // find all the packets that belong to current agent and remove them from the list
                    //System.out.println("Agents packages: " + agentsPkgs);
                    for (KdTree.XYZPoint point : agentsPkgs) {
                        tempTree.remove(point);
                    }
                    //System.out.println(tempTree);
                }
            }

            KdTree.XYZPoint prev = nearestPkg.get(0);
            nearestPkg = (List<KdTree.XYZPoint>) tempTree.nearestNeighbourSearch(2, xySource);
            if(nearestPkg != null && nearestPkg.size() >= 2 && nearestPkg.get(0) == prev){
                nearestPkg.set(0, nearestPkg.get(1));
            }

        }
    }

    public void addPkg(KdTree.XYZPoint newPoint, List<KdTree.XYZPoint> currList){

        packages.add(newPoint);
        currList.add(newPoint);

        // if size = 1, this is the first element added. thus the list must be inserted in list of lists
        if(currList.size() == 1){
            agents.add(currList);
        }

        List<Integer> existingInts;
        // if the point is already assigned to one agent, add the second occurrence in the list
        // else, create a new list for the new point and add it
        if(assignedPkgs.containsKey(newPoint)) {
            existingInts = assignedPkgs.get(newPoint);
            int currInt = agents.indexOf(currList);
            // if the mapping between point and agent does not exist, add it
            if(!existingInts.contains(currInt))
                assignedPkgs.get(newPoint).add(agents.indexOf(currList));
        }
        else{
            List<Integer> agentIndex = new ArrayList<>();
            agentIndex.add(agents.indexOf(currList));
            assignedPkgs.put(newPoint, agentIndex);
        }

    }

    public void addAgentsPosition(KdTree.XYZPoint initPoint){

        packages.add(initPoint);
        agentsLocations.add(initPoint);
    }

    public void displayAgents(){

        for(List<KdTree.XYZPoint> agent: agents)
            System.out.println("Agent " + agents.indexOf(agent) + ": " + agent);
    }

}
