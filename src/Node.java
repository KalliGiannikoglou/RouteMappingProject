public class Node {
    private double lon;
    private double lat;

    public Node(double longitude, double latitude) {
        this.lon = longitude;
        this.lat = latitude;
    }

    public Node(Node node) {
        this.lon = node.getLon();
        this.lat = node.getLat();
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "(" + lon + ", " + lat + ")";
    }
}
