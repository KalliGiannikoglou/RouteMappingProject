import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class MapDisplay {
    protected final RouteModel rmModel;

    // Google Static Maps API URL (Define window size)
    String apiUrl = "https://maps.googleapis.com/maps/api/staticmap?size=750x750&";
    // my API Key
    String apiKey = "";

    // Default Constructor
    public MapDisplay(RouteModel model) {
        this.rmModel = model;
    }

    public void googleMapsDisplay(List<String> coordinates) {

        StringBuilder markers = new StringBuilder();
        StringBuilder path = new StringBuilder();
        Character k = 'A';

        for (String coord : coordinates) {
            RouteModel.RMNode coordNode = rmModel.getRMNode(coord);
            // Form the coord string in Google Format
            String coordStr = coordNode.getLat() + "," + coordNode.getLon();

            // Mark the routing with blue
            path.append("|").append(coordStr);

            if (coordNode.isStartNode && coordNode.isEndNode) {
                markers.append("&markers=color:green%7Clabel:").append(k).append("%7C").append(coordStr);
                coordStr = coordNode.getLat() + "," + coordNode.getLon()*1.00001;
                markers.append("&markers=color:red%7C").append(coordStr);
                k++;

            }
            else if (coordNode.isStartNode) {
                markers.append("&markers=color:green%7Clabel:").append(k).append("%7C").append(coordStr);
                k++;

            }
            else if (coordNode.isEndNode) {
                markers.append("&markers=color:red%7Clabel:").append(k).append("%7C").append(coordStr);
                k++;
            }
        }


        path.insert(0, "&path=color:blue|weight:5");

        String finalUrl = apiUrl + markers + path + "&key=" + apiKey;
        System.out.println("Final URL: " + finalUrl);

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





