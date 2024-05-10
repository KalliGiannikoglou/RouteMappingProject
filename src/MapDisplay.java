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

    public void googleMapsDisplay(List<String> coordinates) {

        StringBuilder markers = new StringBuilder();
        StringBuilder path = new StringBuilder();
        Character k = 'A';
        for (int i = 0; i < coordinates.size(); i++) {
            String coordNode = coordinates.get(i);
            RouteModel.RMNode coord = rmModel.getRMNode(coordNode);
            // Form the coord string in Google Format
            String coordStr = coord.getLat() + "," + coord.getLon();

            // If we have more than 100 points, print on every 10
            if (coord.isStartNode && coord.isEndNode) {
                if( !coord.addedToMap){
                    markers.append("&markers=color:black%7Clabel:").append(k).append("%7C").append(coordStr);
                    k++;
                    coord.addedToMap = true;
                }
            }
            else if (coord.isStartNode) {
                markers.append("&markers=color:green%7Clabel:").append(k).append("%7C").append(coordStr);
                k++;
            }
            else if (coord.isEndNode) {
                markers.append("&markers=color:red%7Clabel:").append(k).append("%7C").append(coordStr);
                k++;
            }

            // Mark the routing with blue
            path.append("|").append(coordStr);
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




