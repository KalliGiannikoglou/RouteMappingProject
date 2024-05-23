import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Model {

    private final Map<String, Node> nodes = new HashMap<>();
    private final Map<String, Way> ways = new HashMap<>();
    private final Map<Integer, Road> roads = new HashMap<>();
    private final Map<Integer, Railway> railways = new HashMap<>();
    private final Map<Integer, Building> buildings = new HashMap<>();
    private final Map<Integer, Leisure> leisures = new HashMap<>();
    private final Map<Integer, Water> waters = new HashMap<>();
    private final Map<Integer, Landuse> landuses = new HashMap<>();
    protected final Map<String, Integer> wayIdHashMap = new HashMap<>();

    public Model(List<Byte> xml) {
        LoadData(xml);
    }

    public double getMetricScale() {
        return 1.0;
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }


    private void adjustCoordinates() {
        // Implementation of adjustCoordinates method
    }

    private void buildRings() {
        // Implementation of buildRings method
    }

    public static Landuse.Type StringToLanduseType(String type) {
        if (type.equals("commercial"))
            return Landuse.Type.Commercial;
        else if (type.equals("construction"))
            return Landuse.Type.Construction;
        else if (type.equals("forest"))
            return Landuse.Type.Forest;
        else if (type.equals("grass") || (type.equals("greenfield") ||  type.equals("orchard")))
            return Landuse.Type.Grass;
        else if (type.equals("industrial"))
            return Landuse.Type.Industrial;
        else if (type.equals("railway"))
            return Landuse.Type.Railway;
        else if (type.equals("residential"))
            return Landuse.Type.Residential;
        else
            return Landuse.Type.Invalid;
    }

    public static Road.Type StringToHighwayType(String type) {
        if ("cycleway".equals(type) || "primary_link".equals(type) ||
                "primary".equals(type))
            return Road.Type.Primary;
        else if ("residential".equals(type))
            return Road.Type.Residential;
        else if ("secondary".equals(type) ||
                "secondary_link".equals(type))
            return Road.Type.Secondary;
        else if ("service".equals(type))
            return Road.Type.Service;
        else if ("steps".equals(type) || "pedestrian".equals(type) ||
                "footway".equals(type) || "path".equals(type) ||
                "living_street".equals(type))
            return Road.Type.Footway;
        else if ("tertiary".equals(type) ||
                "tertiary_link".equals(type))
            return Road.Type.Tertiary;
        else if ("trunk".equals(type) || "trunk_link".equals(type))
            return Road.Type.Trunk;
        else if ("unclassified".equals(type))
            return Road.Type.Unclassified;
        else
            return Road.Type.Invalid;
    }

    public Map<Integer, Road> getRoads() {
        return roads;
    }
    public Map<String, Way> getWays() {
        return ways;
    }

    public void LoadData(List<Byte> xml) {
        try {
            // Convert list of bytes to a string
            StringBuilder xmlString = new StringBuilder();
            for (byte b : xml) {
                xmlString.append((char) b);
            }

            // Parse XML document using Jsoup
            Document doc = Jsoup.parse(xmlString.toString());

            // Parse bounds
            Element boundsElement = doc.selectFirst("osm > bounds");
            if (boundsElement != null) {
                double minLat = Double.parseDouble(boundsElement.attr("minlat"));
                double minLon = Double.parseDouble(boundsElement.attr("minlon"));
                double maxLat = Double.parseDouble(boundsElement.attr("maxlat"));
                double maxLon = Double.parseDouble(boundsElement.attr("maxlon"));
            } else {
                throw new IllegalStateException("Error in map bounds");
            }

            // Parse nodes
            Elements nodeElements = doc.select("osm > node");
            for (Element nodeElement : nodeElements) {
                String id = nodeElement.attr("id");
                double lat = Double.parseDouble(nodeElement.attr("lat"));
                double lon = Double.parseDouble(nodeElement.attr("lon"));
                nodes.put(id, new Node(lon, lat, id));
            }

            // Parse ways
            // Assuming xml_doc is already parsed using Jsoup
            Elements wayElements = doc.select("osm > way");

            for (Element way : wayElements) {
                // Save id as string
                String id = way.attr("id");
                // Get the last current int as number in the map
                double wayNum = ways.size();
                try{
                    Way newWay = new Way(id, (int)wayNum);
                    // Add the new way in Ways map
                    ways.put(id, newWay);
                    wayIdHashMap.put(id, (int) wayNum);

                    // Iterate through children nodes
                    for (Element child : way.children()) {
                        // Get "nd" or "tag" name
                        String name = child.tagName();
                        if (name.equals("nd")) {
                            // Get ref numbers
                            String ref = child.attr("ref");
                            // Place them at the back of nodes vector
                            newWay.nodes.add(ref);
                        } else if (name.equals("tag")) {
                            String category = child.attr("k");
                            String type = child.attr("v");

                            // Set Direction
                            if(category.equals("oneway") && type.equals("yes")){
                                newWay.setDirection(true);
                            }

                            //Set road type
                            if (category.equals("highway")) {
                                Road.Type road_type = StringToHighwayType(type);
                                if (road_type != Road.Type.Invalid) {
                                    // Create new road and add it in Roads
                                    Road new_road = new Road((int) wayNum, road_type, id);
                                    roads.put((int) wayNum, new_road);
                                }
                            }

                            if (category.equals("railway")) {
                                Railway new_railway = new Railway((int) wayNum);
                                railways.put((int) wayNum, new_railway);
                            } else if (category.equals("building")) {
                                List<Integer> outer = new ArrayList<>();
                                outer.add((int) wayNum);
                                Building new_building = new Building(outer);
                                buildings.put((int) wayNum, new_building);
                            } else if (category.equals("leisure") ||
                                    (category.equals("natural") && (type.equals("wood") || type.equals("tree_row") ||
                                            type.equals("scrub") || type.equals("grassland"))) ||
                                    (category.equals("landcover") && type.equals("grass"))) {
                                List<Integer> outer = new ArrayList<>();
                                outer.add((int) wayNum);
                                Leisure new_leisure = new Leisure(outer);
                                leisures.put((int) wayNum, new_leisure);
                            } else if (category.equals("natural") && type.equals("water")) {
                                List<Integer> outer = new ArrayList<>();
                                outer.add((int) wayNum);
                                Water new_water = new Water(outer);
                                waters.put((int) wayNum, new_water);
                            } else if (category.equals("landuse")) {
                                Landuse.Type landuse_type = StringToLanduseType(type);
                                if (landuse_type != Landuse.Type.Invalid) {
                                    List<Integer> outer = new ArrayList<>();
                                    outer.add((int) wayNum);
                                    Landuse new_landuse = new Landuse(outer, landuse_type);
                                    landuses.put((int) wayNum, new_landuse);
                                }
                            }
                        }

                    }
                } catch (OutOfMemoryError e) {
                    System.err.println("Out of memory error: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
