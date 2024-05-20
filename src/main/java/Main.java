import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import edu.princeton.cs.algs4.Point2D;

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

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // READ AGENTS
        System.out.println("Please give the number of available agents.");
        String line = br.readLine();
        int numAgents = Integer.parseInt(line);

        List<Point2D> sources = new ArrayList<>();
        List<Point2D> destinations = new ArrayList<>();
        String[] parts;

        AgentDistribution agents = new AgentDistribution(numAgents);


        // READ SOURCE AND DESTINATION POINTS
        System.out.println("Please give the source and destination coordinates (lat, lon) in pairs. Press q to stop.");
        line = br.readLine();
        while (line.compareTo("q") != 0){
            // ##### SOURCE ####
            parts = line.split(",");
            float startLat = Float.parseFloat(parts[0]);
            float startLon = Float.parseFloat(parts[1]);
            Point2D startPoint = new Point2D(startLon, startLat);
            line = br.readLine();

            if(line.compareTo("q") == 0){
                break;
            }

            // ##### DESTINATION ####
            parts = line.split(",");
            float endLat = Float.parseFloat(parts[0]);
            float endLon = Float.parseFloat(parts[1]);
            Point2D endPoint = new Point2D(endLon, endLat);

            agents.assignNewPackage(startPoint, endPoint);
            line = br.readLine();

        }

        agents.displayAgents();
        // Instantiate Model
        RouteModel model = new RouteModel(osmDataList);

        for(List<KdTree.XYZPoint> agent: agents.getAgents()){
            // Split the points in source and destination points. Every pkg has the form of source, destination.
            for(int i=0; i < agent.size(); i++){
                if(i % 2 == 0)
                    sources.add(new Point2D(agent.get(i).x, agent.get(i).y));
                else
                    destinations.add(new Point2D(agent.get(i).x, agent.get(i).y));
            }

            // Apply routePlanner in agent list separately
            RoutePlanning routePlanner = new RoutePlanning(model, sources, destinations);

            // clear before the next iteration
            for(int i=0; i < sources.size(); i++){
                RouteModel.RMNode node = model.findClosestNode((float) sources.get(i).x(), (float) destinations.get(i).y());
                node.isStartNode = false;
                node.isEndNode = false;
                node.visited = false;
            }

            sources.clear();
            destinations.clear();
        }

    }
}