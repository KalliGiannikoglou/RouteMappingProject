import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MapDisplay {

    // Google Static Maps API URL (Define window size)
    String apiUrl = "https://maps.googleapis.com/maps/api/staticmap?size=750x750&";
    // my API Key
    String apiKey = "";

    // Default Constructor
    public MapDisplay() { }

    public void googleMapsDisplay(List<RouteModel.RMNode> coordinates){

        StringBuilder markers = new StringBuilder();
        System.out.println("Number of nodes: " + coordinates.size());
        for (int i = 0; i < coordinates.size(); i++) {
            RouteModel.RMNode coord = coordinates.get(i);
            // Form the coord string in Google Format
            String coordStr = coord.getLat() + "," + coord.getLon();

            // If we have more than 100 points, print on every 10
            if(coordinates.size() >= 100 && i % 10 == 0)
                markers.append("&markers=color:blue%7C").append(coordStr);
            else if(i == coordinates.size() - 1)
                markers.append("&markers=color:red%7C").append(coordStr);
            else if(i == 0)
                markers.append("&markers=color:green%7C").append(coordStr);

        }

        StringBuilder path = new StringBuilder();
        // Mark the routing with blue
        path.append("&path=color:blue|weight:5");

        //separate coords with "|"
        for (RouteModel.RMNode coord : coordinates) {
            String coordStr = coord.getLat() + "," + coord.getLon();
            path.append("|").append(coordStr);
        }
        String finalUrl = apiUrl + markers + path + "&key=" + apiKey;

        try {
            // Read image from URL
            BufferedImage image = ImageIO.read(new URL(finalUrl));

            // Save to file map.png
            File out = new File("map.png");
            ImageIO.write(image, "png", out);

            System.out.println("Map saved successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




