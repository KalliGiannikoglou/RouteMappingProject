public class Railway {
    private final int way;

    public Railway(int way) {
        this.way = way;
    }

    @Override
    public String toString() {
        return " (" + way + ")";
    }
}
