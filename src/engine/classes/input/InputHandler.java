package classes.input;

import machine.Engine;
import main.Main;

import java.awt.event.KeyEvent;
import java.util.Hashtable;

public class InputHandler implements InputListener {
    Hashtable<Integer, Boolean> KEY_STATUSES = new Hashtable<>();
    int[] keys = null;
    public InputHandler(int[] keys) {
        this.keys = keys;
        for (int key : keys) {
            KEY_STATUSES.put(key, false);
        }
    }
    public boolean getKeyStatus(int keyCode) {
        if (!KEY_STATUSES.get(keyCode)) return false;
        else return KEY_STATUSES.get(keyCode);
    }
    @Override
    public void keyEvent(int type, int keyCode) {
        if (KEY_STATUSES.containsKey(keyCode)) {
            if (type == KeyEvent.KEY_PRESSED)
                KEY_STATUSES.replace(keyCode,true);
            else if (type == KeyEvent.KEY_RELEASED)
                KEY_STATUSES.replace(keyCode,false);
        }
    }
}
