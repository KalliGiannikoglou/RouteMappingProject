public class Road {
    private final int wayNum;
    private final Type type;
    private final String wayRef;

    public Road(int wayNum, Type type, String ref) {
        this.wayNum = wayNum;
        this.wayRef = ref;
        this.type = type;
    }

    @Override
    public String toString() {
        return " (" + type + ", " + wayNum +  ", " + wayRef + ")";
    }

    // Define the Type enum here
    public enum Type {
        Cycleway, Footway, Invalid, Motorway, Primary, Residential,
        Secondary, Service, Tertiary, Trunk, Unclassified
    }

    public Type getType() {
        return type;
    }

    public int getId() {
        return wayNum;
    }

    public String getRef(){return wayRef;}
}
