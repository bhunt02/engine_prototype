package objects.tiles;

import classes.objects.Terrain;

public class Tile extends Terrain {
    public Tile() {
        this(0,0,"N/A");
    }
    public Tile(double x, double y, String name) {
        super(x,y,0,0,name);
    }
}
