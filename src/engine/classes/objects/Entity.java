package classes.objects;

import machine.Physics;
import main.Assets;
import objects.tiles.Fluid;

public abstract class Entity extends BaseObject {
    protected int health = 1;
    protected int max_health = health;
    protected boolean canJump = true;
    protected boolean canMove = true;
    protected boolean moving = false;
    protected boolean jumping = false;
    protected double jumpHeight = 14.0;
    protected double speed = 8.0;
    protected int direction_Facing = 0;

    public Entity() {
        this(0,0,"N/A");
    }
    public Entity(double x, double y, String name) {
        super(x,y,0,0,false,name);
        if (Assets.Sprites.containsKey(name)) {
            this.Sprite = Assets.Sprites.get(name);
            updateBoundingBox();
        }
    }
    public Entity(double x, double y, double Elasticity, double Friction, double Density, String name) {
        super(x,y,0,0,Elasticity,Friction,Density,false,true,name);
        if (Assets.Sprites.containsKey(name)) {
            this.Sprite = Assets.Sprites.get(name);
            updateBoundingBox();
        }
    }

    @Override
    public void update(double dt) {
        if (this.Anchored) return;
        super.update(dt);
    }
    protected void move(int dir) {
        if (!canMove) return;
        if (dir == 0) {moving = false; return;}
        if (Math.abs(this.Velocity.x) < speed/coord_Scale) {
            moving = true;
            int div = 4;
            if (jumping) div = 16;
            this.Velocity.x += (dir*speed/div)/coord_Scale;
        }
    }

    protected void jump() {
        if (!canJump) return;
        if (this.Velocity.y < jumpHeight/coord_Scale && floor != null && isGround(floor) && intersectingX(floor,true)) {
            canJump = false;
            jumping = true;
            this.Position.y += 1;
            this.Velocity.y = jumpHeight/coord_Scale;
        }
    }

    @Override
    public void Collision(BaseObject other) {
        super.Collision(other);
    }
}
