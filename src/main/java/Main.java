import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {
    public static Optional<byte[]> readFile(String filePath) {
        try {
            Path path = Path.of(filePath);
            return Optional.of(Files.readAllBytes(path));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return Optional.empty();
        }
    }

    public static void main(String[] args) throws IOException {

        // File name here
        String osmDataFile = "volos.osm";
        Optional<byte[]> osmDataOptional = readFile(osmDataFile);
        if (osmDataOptional.isEmpty()) {
            System.out.println("Failed to read.");
            return;
        }

        byte[] osmData = osmDataOptional.get();
        System.out.println("Reading OpenStreetMap data from the following file: " + osmDataFile);
        System.out.println("Read osm_data");

        // Convert byte array to list of bytes
        List<Byte> osmDataList = new ArrayList<>();
        for (byte b : osmData) {
            osmDataList.add(b);
        }
        // Instantiate Model
        RouteModel model = new RouteModel(osmDataList);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // ################## READ AGENTS #######################################
        System.out.println("Please give the number of available agents.");
        String line = br.readLine();
        int numAgents = Integer.parseInt(line);

        System.out.println("Please give the maximum amount of packets for each agent.");
        line = br.readLine();
        int maxCapacity = Integer.parseInt(line);

        List<KdTree.XYZPoint> sources = new ArrayList<>();
        List<KdTree.XYZPoint> destinations = new ArrayList<>();
        List<KdTree.XYZPoint> agentsPositions = new ArrayList<>();
        String[] parts;

        // initialize agentDistribution class
        AgentDistribution agentDistr = new AgentDistribution(numAgents, maxCapacity);

        // Add initial positions of all the available agents
        System.out.println("Please give the initial position of all agents.");
        for(int i = 0; i < numAgents; i++){

            line = br.readLine();
            parts = line.split(",");
            float initLat = Float.parseFloat(parts[0]);
            float initLon = Float.parseFloat(parts[1]);
            if(model.isInBounds(initLat, initLon)) {
                KdTree.XYZPoint initPosition = new KdTree.XYZPoint(initLon, initLat);
                agentsPositions.add(new KdTree.XYZPoint(initLon, initLat));

                agentDistr.addAgentsPosition(initPosition);
            }
            else{
                System.out.println("Agent's location " + initLat + "," + initLon + " is out of bounds!");
                return;
            }

        }

        // READ SOURCE AND DESTINATION POINTS
        System.out.println("Please give the source and destination coordinates (lat, lon) in pairs. Press q to stop.");
        while (true){

            line = br.readLine();
            if(line.compareTo("q") == 0){
                break;
            }
            // ##### SOURCE ####
            parts = line.split(",");
            float startLat = Float.parseFloat(parts[0]);
            float startLon = Float.parseFloat(parts[1]);
            if(!model.isInBounds(startLat, startLon)){
                System.out.println("Coordinates " + startLat + "," + startLon + " is out of bounds!");
                return;
            }
            // init the new point
            KdTree.XYZPoint startPoint = new KdTree.XYZPoint(startLon, startLat);

            // ##### DESTINATION ####
            line = br.readLine();
            parts = line.split(",");
            float endLat = Float.parseFloat(parts[0]);
            float endLon = Float.parseFloat(parts[1]);
            if(!model.isInBounds(endLat, endLon)){
                System.out.println("Coordinates " + endLat + "," + endLon + " is out of bounds!");
                return;
            }
            // init the new point
            KdTree.XYZPoint endPoint = new KdTree.XYZPoint(endLon, endLat);

            // add every new pkg to the tree and assign it to an agent
            agentDistr.assignNewPackage(startPoint, endPoint);
        }

        agentDistr.displayAgents();

        // Apply routePlanner for each one of the agents
        for(List<KdTree.XYZPoint> agentList: agentDistr.getAgents()){

            // get agent's initial position and add it as the first source
            int agentIdx = agentDistr.getAgents().indexOf(agentList);
            KdTree.XYZPoint agentPos = agentsPositions.get(agentIdx);
            sources.add(agentPos);
            // add agents position as the first destination
            destinations.add(agentList.get(0));

            // Split the points in source and destination points. Every pkg has the form of source, destination.
            // for i=0 we have the initial position of the agent, so we skip it
            for(int i=1; i < agentList.size(); i++){
                if(i % 2 == 1)
                    sources.add(new KdTree.XYZPoint(agentList.get(i).x, agentList.get(i).y));
                else
                    destinations.add(new KdTree.XYZPoint(agentList.get(i).x, agentList.get(i).y));
            }


            // Apply routePlanner in each agent's list separately
            RoutePlanning routePlanner = new RoutePlanning(model, sources, destinations, agentPos);

            sources.clear();
            destinations.clear();
        }
    }
}

