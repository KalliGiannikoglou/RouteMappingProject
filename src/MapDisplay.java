import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MapDisplay {

    // Google Static Maps API URL
    String apiUrl = "https://maps.googleapis.com/maps/api/staticmap?size=600x400&";
    // API Key
    String apiKey = "AIzaSyCv3MQCBBotp_djJT9hAD4KsMfBMqQUyLY";


    public MapDisplay() { }

    public void googleMapsDisplay(List<RouteModel.RMNode> coordinates){

        StringBuilder markers = new StringBuilder();
        for (RouteModel.RMNode coord : coordinates) {
            String coordStr = coord.getLat() + "," + coord.getLon();
            markers.append("&markers=color:red%7C").append(coordStr);
        }

        StringBuilder path = new StringBuilder();
        path.append("&path=color:0x0000ff|weight:5");
        for (RouteModel.RMNode coord : coordinates) {
            String coordStr = coord.getLat() + "," + coord.getLon();
            path.append("|").append(coordStr);
        }

        String finalUrl = apiUrl + markers + path + "&key=" + apiKey;
        System.out.println(finalUrl);
        try {
            // Read the image from the URL
            BufferedImage image = ImageIO.read(new URL(finalUrl));

            // Save the image to a file
            File outputfile = new File("map.png");
            ImageIO.write(image, "png", outputfile);

            System.out.println("Map saved successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




