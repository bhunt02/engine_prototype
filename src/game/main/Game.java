package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Game {
    private static class Controller extends Game implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            //Game.keyTyped(e.getKeyCode());
        }

        @Override
        public void keyPressed(KeyEvent e) {
            Game.keyPressed(e.getKeyCode());
        }

        @Override
        public void keyReleased(KeyEvent e) {
            Game.keyReleased(e.getKeyCode());
        }
    }

    private static void keyPressed(int k) {
        switch(k) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> {}
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> {}
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> {}
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> {}
            case KeyEvent.VK_E -> {}
            case KeyEvent.VK_SPACE -> {}
            case KeyEvent.VK_ESCAPE -> {}
        }
    }
    private static void keyReleased(int k) {
        if (k == KeyEvent.VK_W || k == KeyEvent.VK_UP) {

        }
    }
}
