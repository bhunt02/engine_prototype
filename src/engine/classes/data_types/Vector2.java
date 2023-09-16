package classes.data_types;

public class Vector2 implements Comparable {
    public double x, y;

    public Vector2() {
        this(0,0);
    }
    public Vector2(double x, double y) {
        this.x = x; this.y = y;
    }

    public double length() {
        return Math.sqrt(Math.pow(this.x,2)+Math.pow(this.y,2));
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Vector2) {
            return (int) -(this.x - ((Vector2) o).x);
        }
        return 0;
    }
}
