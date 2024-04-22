import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        System.out.print("Please give the start coordinates (lat, lon): ");
        String start = br.readLine();
        String[] parts = start.split(",");
        float startLat = Float.parseFloat(parts[0]);
        float startLon = Float.parseFloat(parts[1]);

        System.out.print("Please give the end coordinates (lat, lon): ");
        String end = br.readLine();
        parts = end.split(",");
        float endLat = Float.parseFloat(parts[0]);
        float endLon = Float.parseFloat(parts[1]);

        System.out.println("Start: " + startLat + ", " + startLon);
        System.out.println("End: " + endLat + ", " + endLon);

        // Instantiate Model
        RouteModel model = new RouteModel(osmDataList);
        RoutePlanning routePlanner = new RoutePlanning(model, startLon, startLat, endLon, endLat);
    }
}