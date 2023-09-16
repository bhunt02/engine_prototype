package objects.entities;

import classes.objects.BaseObject;
import classes.objects.AbstractPlayer;
import main.Assets;
import objects.tiles.Fluid;

import java.awt.event.KeyEvent;
import java.util.Hashtable;

public class Player extends AbstractPlayer {
    protected boolean holdingSpace = false;
    public Player() {
        this(0,0);
    }
    public Player(int x, int y) {
        super(x,y,0.2,0.4,0.5,"Player");
        this.SpriteSet = new Hashtable<>();
        Assets.Sprites.forEach((st, sp) -> {
            if (st.startsWith("player_")) {
                this.SpriteSet.put(st.substring(st.indexOf("_"),st.length()-1), sp);
                if (st.toLowerCase().contains("idle")) this.Sprite = sp;
            }
        });
    }
    @Override
    public void update(double dt) {
        if (this.Anchored) return;

        // fluttering wip
        if (this.INPUT_HANDLER.getKeyStatus(KeyEvent.VK_SPACE) && jumping)
            if (this.Velocity.y < 0) this.Velocity.y /= 1.25;

        super.update(dt);

        // player specific updates
        if (floor != null && isGround(floor)) {
            jumping = false;
            canJump = true;
        }
    }

    @Override
    protected void animate() {

    }
    @Override
    public void Collision(BaseObject other) {
        super.Collision(other);

        if (other instanceof Fluid && other.Name != null) {
            if (other.Name.equals("spring")) {
                // healing
            } else if (other.Name.equals("magma")) {
                // damaging
            }
        }
    }
}
