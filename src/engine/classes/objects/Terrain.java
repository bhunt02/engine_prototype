package classes.objects;

import main.Assets;

public class Terrain extends BaseObject {
    public Terrain() {
        this(0,0,0,0,"N/A");
    }
    public Terrain(double x, double y, double vx, double vy, String name) {
        super(x,y,vx,vy,true,name);
        if (Assets.Sprites.containsKey(name)) {
            this.Sprite = Assets.Sprites.get(name);
            updateBoundingBox();
        }
    }

    public Terrain(double x, double y, double vx, double vy, boolean Anchored, boolean CanCollide, double Elasticity, double Friction, double Density, String name) {
        super(x,y,vx,vy,Elasticity,Friction,Density,Anchored,CanCollide,name);
        if (Assets.Sprites.containsKey(name)) {
            this.Sprite = Assets.Sprites.get(name);
            updateBoundingBox();
        }
    }
}
