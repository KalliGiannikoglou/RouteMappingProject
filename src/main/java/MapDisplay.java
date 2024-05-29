import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

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

    public void googleMapsDisplay(List<String> coordinates, KdTree.XYZPoint agentPos) {

        StringBuilder markers = new StringBuilder();
        StringBuilder path = new StringBuilder();
        Character k = 'A';
        String coordStr;

        System.out.println("Coordinates size: " + coordinates.size());

        // Add agent location with blue
        coordStr = String.format("%.4f", agentPos.y) + "," + String.format("%.4f", agentPos.x);
        markers.append("&markers=color:blue%7C").append(coordStr);

        int i = 0;
        for (String coord : coordinates) {
            RouteModel.RMNode coordNode = rmModel.getRMNode(coord);
            // Form the coord string in Google Format
            coordStr = String.format("%.4f", coordNode.getLat()) + "," + String.format("%.4f", coordNode.getLon());

            // Mark the routing with blue
            if(coordinates.size() < 1000 || i%2 == 0) {
                path.append("|").append(coordStr);
            }
            i++;

            if (coordNode.isStartNode && coordNode.isEndNode) {
                markers.append("&markers=color:green%7Clabel:").append(k).append("%7C").append(coordStr);
                coordStr = coordNode.getLat() + "," + coordNode.getLon()*1.000001;
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





