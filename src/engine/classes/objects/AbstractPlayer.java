package classes.objects;

import classes.input.InputHandler;
import main.Main;

import java.awt.event.KeyEvent;

/**
 * Class intended to build player classes off of with basic methods & input functionality
 */
public abstract class AbstractPlayer extends Entity {
    protected InputHandler INPUT_HANDLER = null;
    protected int[] INPUT_KEYS = new int[] {
            KeyEvent.VK_W,
            KeyEvent.VK_UP,
            KeyEvent.VK_S,
            KeyEvent.VK_DOWN,
            KeyEvent.VK_A,
            KeyEvent.VK_LEFT,
            KeyEvent.VK_D,
            KeyEvent.VK_RIGHT,
            KeyEvent.VK_SPACE
    };
    protected AbstractPlayer(double x, double y, double Elasticity, double Friction, double Density, String name) {
        super(x,y,Elasticity,Friction,Density,name);
        this.INPUT_HANDLER = Main.Engine.createInputHandler(INPUT_KEYS);
    }

    @Override
    public void update(double dt) {
        if (this.Anchored) return;
        handleInput();
        super.update(dt);
    }

    protected void handleInput() {
        boolean up = INPUT_HANDLER.getKeyStatus(KeyEvent.VK_W) || INPUT_HANDLER.getKeyStatus(KeyEvent.VK_UP);
        boolean down = INPUT_HANDLER.getKeyStatus(KeyEvent.VK_S) || INPUT_HANDLER.getKeyStatus(KeyEvent.VK_DOWN);
        if (up && !down) {
            // up
        } else if (down && !up) {
            // down
        } else {
            // neither or both
        }

        boolean l = INPUT_HANDLER.getKeyStatus(KeyEvent.VK_A) || INPUT_HANDLER.getKeyStatus(KeyEvent.VK_LEFT);
        boolean r = INPUT_HANDLER.getKeyStatus(KeyEvent.VK_D) || INPUT_HANDLER.getKeyStatus(KeyEvent.VK_RIGHT);

        if (l && !r) {
            move(-1);
            // left
        } else if (r && !l) {
            move(1);
            // right
        } else {
            move(0);
            // neither or both
        }

        if (INPUT_HANDLER.getKeyStatus(KeyEvent.VK_SPACE)) {
            if (!jumping) jump();
        } else {
            // not holding space
        }
    }
}
