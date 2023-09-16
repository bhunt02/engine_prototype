package classes.data_types;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BoundingBox implements Comparable<BoundingBox> {
    public PropertyChangeSupport Support = new PropertyChangeSupport(this);
    public double x0,y0,x1,y1;

    public BoundingBox() {this(0,0,0,0);}

    public BoundingBox(double x0, double y0, double x1, double y1) {
        this.x0 = x0; this.y0 = y0; this.x1 = x1; this.y1 = y1;
    }
    public static BoundingBox Union(BoundingBox a, BoundingBox b) {
        if (b == null && a != null)
            return new BoundingBox(a.x0,a.y0,a.x1,a.y1);

        if (a == null && b != null)
            return new BoundingBox(b.x0,b.y0,b.x1,b.y1);

        if (a != null)
            return new BoundingBox(
                Math.min(a.x0,b.x0),
                Math.min(a.y0,b.y0),
                Math.max(a.x1,b.x1),
                Math.max(a.y1,b.y1)
            );
        else
            return null;
    }
    public static boolean RayCast(Vector2 src, Vector2 dir) {
        return false;
    }
    public double surfaceArea() {
        return (this.x1-this.x0)*(this.y1-this.y0);
    }

    public void refactor(Double[] newBounds) {
        Double[] oldBounds = Bounds();
        this.x0 = newBounds[0];
        this.y0 = newBounds[1];
        this.x1 = newBounds[2];
        this.y1 = newBounds[3];
        Support.firePropertyChange(new PropertyChangeEvent(this,"Bounds",oldBounds,newBounds));
    }

    private Double[] Bounds() {
        return new Double[] {this.x0, this.y0, this.x1, this.y1};
    }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        Support.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        Support.removePropertyChangeListener(listener);
    }

    @Override
    public int compareTo(BoundingBox o) {
        if (o == null) return 1;
        return (int) (o.surfaceArea() - this.surfaceArea());
    }
}
