package classes.input;

import classes.data_structures.ConcurrentCollection;
import classes.data_structures.ConcurrentSet;
import machine.Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.util.Hashtable;

public class InputCapture {
    Hashtable<Integer, ConcurrentSet<InputListener>> LISTENERS = new Hashtable<>();
    Display DISPLAY = null;
    ActionMap ACTION_MAP = null;
    InputMap INPUT_MAP = null;
    public InputCapture(Display DISPLAY) {
        this.DISPLAY = DISPLAY;
        this.ACTION_MAP = DISPLAY.getActionMap();
        this.INPUT_MAP = DISPLAY.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        connectKeyBindings();
    }

    private void connectKeyBindings() {
        Field[] fields = KeyEvent.class.getDeclaredFields();
        for (Field f : fields) {
            try {
                if (f.getName().startsWith("VK_")) {
                    int keyCode = f.getInt(f.getName());
                    String s_keyCode = String.valueOf(keyCode);
                    INPUT_MAP.put(KeyStroke.getKeyStroke(keyCode, 0, false), s_keyCode);
                    INPUT_MAP.put(KeyStroke.getKeyStroke(keyCode, 0, true), s_keyCode+"R");
                    ACTION_MAP.put(s_keyCode, new keyAction(keyCode, false));
                    ACTION_MAP.put(s_keyCode+"R", new keyAction(keyCode, true));
                    LISTENERS.put(keyCode,new ConcurrentSet<>());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addInputListener(InputListener e) {
        LISTENERS.forEach((k, v) -> {
            v.add(e);
        });
    }

    public void removeInputListener(InputListener e) {
        LISTENERS.forEach((k, v) -> {
            v.remove(e);
        });
    }

    public void addInputListener(InputListener e, int keyCode) {
        if (!LISTENERS.containsKey(keyCode)) return;
        LISTENERS.get(keyCode).add(e);
    }

    public void removeInputListener(InputListener e, int keyCode) {
        if (!LISTENERS.containsKey(keyCode)) return;
        LISTENERS.remove(keyCode).add(e);
    }

    public void addInputListener(InputListener e, int[] keyCodes) {
        for (int keyCode : keyCodes) {
            if (!LISTENERS.containsKey(keyCode)) continue;
            LISTENERS.get(keyCode).add(e);
        }
    }

    public void removeInputListener(InputListener e, int[] keyCodes) {
        for (int keyCode : keyCodes) {
            if (!LISTENERS.containsKey(keyCode)) continue;
            LISTENERS.get(keyCode).remove(e);
        }
    }

    /*private void connectMouseBindings() {
        Field[] fields = KeyStroke.class.getDeclaredFields();
        for (Field f : fields) {
            try {
                if (f.getName().startsWith("BUTTON_")) {
                    int keyCode = f.getInt(f.getName());
                    String s_keyCode = String.valueOf(keyCode);
                    INPUT_MAP.put(Mouse.getKeyStroke(keyCode, 0), s_keyCode);
                    ACTION_MAP.put(s_keyCode, new keyAction(keyCode));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }*/

    /*{

        fields = MouseEvent.class.getDeclaredFields();
        for (Field f : fields) {
            try {
                if (f.getName().startsWith("BUTTON_")) {
                    int keyCode = f.getInt(f.getName());
                    KEY_STATUSES.put(keyCode, KeyEvent.KEY_RELEASED);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }*/
    private class keyAction extends AbstractAction {
        int keyCode = -1;
        boolean releaseAction = false;
        private keyAction(int keyCode, boolean releaseAction) {
            this.keyCode = keyCode;
            this.releaseAction = releaseAction;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!LISTENERS.containsKey(keyCode)) return;
            LISTENERS.get(keyCode).forEach((l) -> {
               l.keyEvent((releaseAction ? KeyEvent.KEY_RELEASED : KeyEvent.KEY_PRESSED), keyCode);
            });
        }
    }
}
