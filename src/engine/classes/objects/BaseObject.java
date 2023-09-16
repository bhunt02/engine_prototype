package classes.objects;

import machine.Physics;
import machine.Renderer;
import classes.utilities.VMath;
import classes.data_types.BoundingBox;
import classes.data_types.RGB;
import classes.data_types.Vector2;
import classes.visuals.Sprite;

import java.util.*;

public abstract class BaseObject {
    protected static final double coord_Scale = (32.0/Renderer.getTileSize());
    public String Name;
    protected Vector2 Position = new Vector2();
    private final Vector2 previousPosition = new Vector2();
    /*protected*/ public Vector2 Velocity = new Vector2();
    protected Vector2 Acceleration = new Vector2();
    private double Density = 1.0;
    private double Elasticity = 1.0;
    private double Friction = 1.0;
    protected boolean Mirrored = false;
    protected boolean Flipped = false;
    protected RGB tint = null;
    public BoundingBox BB = new BoundingBox();
    protected boolean Anchored = true;
    protected boolean CanCollide = true;
    protected Hashtable<String, Sprite> SpriteSet = null;
    public Sprite Sprite = null;
    protected Terrain floor = null;
    protected BaseObject() {
        this(0, 0, 0, 0, true, "N/A");
    }
    protected BaseObject(double x, double y, double vx, double vy, boolean Anchored, String name) {
        this.Position.x = x;
        this.Position.y = y;
        this.Velocity.x = vx;
        this.Velocity.y = vy;
        this.Anchored = Anchored;
        this.Name = name;
    }

    protected BaseObject(double x, double y, double vx, double vy, double Elasticity, double Friction, double Density, boolean Anchored, boolean CanCollide, String name) {
        this.Position.x = x;
        this.Position.y = y;
        this.Velocity.x = vx;
        this.Velocity.y = vy;
        this.Elasticity = Elasticity;
        this.Friction = Friction;
        this.Density = Density;
        this.Anchored = Anchored;
        this.CanCollide = CanCollide;
        this.Name = name;
    }

    public void update(double dt) {
        if (this.Anchored) return;

        this.Velocity = VMath.add(this.Velocity, VMath.mult(this.Acceleration, dt));

        boolean midair = true;
        /*if (checkGrounded()) {
            terrainCollision(floor);
            midair = false;
        }*/

        this.Position = VMath.add(this.Position, VMath.mult(this.Velocity, dt));

        if (midair && Math.abs(this.Velocity.x) > 0)
            this.Velocity.x *= 0.95;

        if (this.Position.x != this.previousPosition.x || this.Position.y != previousPosition.y) {
            updateBoundingBox();
            this.previousPosition.x = this.Position.x;
            this.previousPosition.y = this.Position.y;
        }

        if (this.SpriteSet != null && !this.SpriteSet.isEmpty()) {
            this.animate();
        }
    }

    public void updateBoundingBox() {
        this.BB.refactor(new Double[]{
                this.Position.x + this.Sprite.Bounds.x0,
                this.Position.y + this.Sprite.Bounds.y0,
                this.Position.x + this.Sprite.Bounds.x1,
                this.Position.y + this.Sprite.Bounds.y1
        });
    }

    public boolean getMirrored() {
        return this.Mirrored;
    }

    public boolean getFlipped() {
        return this.Flipped;
    }

    public boolean getAnchored() {
        return this.Anchored;
    }

    public boolean getCanCollide() {
        return this.CanCollide;
    }

    public RGB getTint() {
        return null;
    }

    protected void animate() {
    }

    public int getTileX() {
        return (int) (this.Position.x / 32);
    }

    public int getTileY() {
        return (int) (this.Position.y / 32);
    }

    public void Collision(BaseObject other) {
        //if (this.Anchored || !other.getAnchored()) return;

        if (other instanceof Terrain t) terrainCollision(t);
    }

    private void terrainCollision(Terrain t) {
        if (!t.CanCollide) return;
        Vector2 c0 = this.getCenterPoint(), c1 = t.getCenterPoint();
        Vector2 d0 = this.getDimensions(), d1 = t.getDimensions();
        double dx = c0.x - c1.x, dy = c0.y - c1.y;
        double sumW = (d0.x/2) + (d1.x/2), sumH = (d0.y/2) + (d1.y/2);
        double shift_x = sumW - Math.abs(dx), shift_y = sumH - Math.abs(dy);
        // ignore larger shift
        if (shift_x < shift_y && shift_x > 0) shift_y = 0;
        else if (shift_x > shift_y && shift_y > 0) shift_x = 0;

        // correct the signs
        if (dx < 0) shift_x = -shift_x;
        if (dy < 0) shift_y = -shift_y;

        double dis = Math.sqrt((shift_x * shift_x) + (shift_y * shift_y));
        double normal_x = shift_x / dis, normal_y = shift_y / dis;
        double vx = this.Velocity.x - t.Velocity.x, vy = this.Velocity.y - t.Velocity.y;
        double v_impact = vx * normal_x + vy * normal_y;

        if (v_impact <= 0) {
            if (intersectingY(t,true)) this.Position.x += shift_x;
            if (intersectingX(t,true)) this.Position.y += shift_y;

            dx = Math.abs(this.Position.x-t.Position.x);
            dy = Math.abs(this.Position.y-t.Position.y);

            double restitution_X = Math.abs(this.Velocity.x * getRecoilFactor(t)),
                   restitution_Y = Math.abs(this.Velocity.y * getRecoilFactor(t));

            boolean min_intX = (dx < sumW),
                    min_intY = (dy < sumH);

            if (intersectingY(t,true) && intersectingX(t,false) && min_intY) {
                if (shift_x > 0) this.Velocity.x = 1 + (restitution_X > 0.1 ? restitution_X : 0);
                else if (shift_x < 0) this.Velocity.x = -1 - (restitution_X > 0.1 ? restitution_X : 0);
                else this.Velocity.x = 0;
            }

            if (min_intX && intersectingX(t,true) && intersectingY(t,false)) {
                if (isRoof(t)) {
                    this.Velocity.y = -1 - (restitution_Y > 0.1 ? restitution_Y : 0);
                } else if (isGround(t)) {
                    this.Velocity.y = (Math.abs(restitution_Y) > 0.1 ? restitution_Y : 0);
                    //double friction = (this.Velocity.x * getFrictionFactor(t));
                    //this.Velocity.x = (Math.abs(friction) > 0.2 ? friction : 0);
                    floor = t;
                }
            }
        }
        updateBoundingBox();
    }

    protected double getMinX() {
        return (this.Position.x+this.Sprite.Bounds.x0);
    }

    protected double getMaxX() {
        return (this.Position.x+this.Sprite.Bounds.x1);
    }

    protected double getMinY() {
        return (this.Position.y+this.Sprite.Bounds.y0);
    }

    protected double getMaxY() {
        return (this.Position.y+this.Sprite.Bounds.y1);
    }

    protected Vector2 getCenterPoint() {
        Vector2 dim = this.getDimensions();
        return new Vector2(
                this.BB.x0+(dim.x/2),
                this.BB.y0+(dim.y/2)
        );
    }
    protected Vector2 getDimensions() {
        return new Vector2(
                (this.BB.x1-this.BB.x0),
                (this.BB.y1-this.BB.y0)
        );
    }
    protected boolean isGround(BaseObject other) {
        return getMinY() <= other.getMaxY() && getMaxY() > other.getMaxY();
    }
    protected boolean isRoof(BaseObject other) {
        return getMinY() < other.getMinY() && getMaxY() >= other.getMinY();
    }
    protected boolean intersectingX(BaseObject other, boolean hard) {
        if (hard) return getMaxX() > other.getMinX() && getMinX() < other.getMaxX();
        else return getMaxX() >= other.getMinX() && getMinX() <= other.getMaxX();
    }
    protected boolean intersectingY(BaseObject other, boolean hard) {
        if (hard) return getMaxY() > other.getMinY() && getMinY() < other.getMaxY();
        else return getMaxY() >= other.getMinY() && getMinY() <= other.getMaxY();
    }
    private boolean Intersection(BaseObject other, boolean hard) {
        return intersectingX(other,hard) && intersectingY(other,hard);
    }
    public boolean checkGrounded() {
        if (floor == null) return false;
        boolean con = !(isGround(floor) && intersectingX(floor,true));
        if (con) {floor = null; return false;}
        else return (isGround(floor) && intersectingX(floor,true));
    }
    private double getRecoilFactor(BaseObject other) {
        double factor = (other.Elasticity*this.Elasticity) - (this.Density-other.Density);
        return (Math.min(factor, 0.5));
    }

    private double getFrictionFactor(BaseObject other) {
        double factor = (1-(other.getFriction()*this.getFriction()));
        return (Math.min(factor,0.99));
    }
    public double getElasticity() {
        return this.Elasticity;
    }

    public double getFriction() {
        return this.Friction;
    }

    public double getDensity() {
        return this.Density;
    }

    public Vector2 getPosition() {
        return this.Position;
    }
    public void gravityAcceleration() {
        this.Acceleration.y = Physics.GRAVITY;//(this.Acceleration.y <= G ? G : this.Acceleration.y + G);
    }
}
