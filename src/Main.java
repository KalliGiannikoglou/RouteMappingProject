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
        System.out.println("Please give the source and destination coordinates (lat, lon) in pairs. Press q to stop.");

        List<Point2D> sources = new ArrayList<>();
        List<Point2D> destinations = new ArrayList<>();
        String line = br.readLine();
        String[] parts;
        while (line.compareTo("q") != 0){
            // ##### SOURCE ####
            parts = line.split(",");
            float startLat = Float.parseFloat(parts[0]);
            float startLon = Float.parseFloat(parts[1]);
            sources.add(new Point2D(startLon, startLat));
            line = br.readLine();

            // ##### DESTINATION ####
            parts = line.split(",");
            float endLat = Float.parseFloat(parts[0]);
            float endLon = Float.parseFloat(parts[1]);
            destinations.add(new Point2D(endLon, endLat));
            line = br.readLine();
        }

        // Instantiate Model
        RouteModel model = new RouteModel(osmDataList);
        RoutePlanning routePlanner = new RoutePlanning(model, sources, destinations);
    }
}