package machine;

import classes.input.InputCapture;
import classes.input.InputHandler;
import classes.input.InputListener;
import classes.visuals.UIComponent;
import main.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Engine {
    private JFrame WINDOW = null;
    private InputCapture INPUT_CAPTURE = null;
    private Physics PHYSICS = null;
    private Renderer RENDERER = null;
    private Display DISPLAY = null;
    private PhysicsThread PHC_Thread = null;
    private GraphicsThread GFX_Thread = null;
    private static final int TICK_RATE = 17; // 17 = APPROX. 60 FPS
    enum PROCESSES {
        DISPLAY,
        PHYSICS,
        RENDERER,
        DEBUG,
    }
    private static final boolean[] ENABLED_PROCESSES = new boolean[] {
            true, // SCREEN DISPLAY
            true, // PHYSICS ENGINE
            true, // RENDERING ENGINE
            true, // DEBUG MODE
    };
    private static class PhysicsThread implements Runnable {
        protected Cycle engine_cycle = null;
        protected PhysicsThread(Engine ENGINE) {
            engine_cycle = new Cycle(ENGINE, this);
        }
        private static class Cycle extends TimerTask {
            Engine eng = null;
            PhysicsThread parent = null;

            protected Cycle(Engine eng, PhysicsThread parent) {
                this.eng = eng;
                this.parent = parent;
            }

            @Override
            public void run() {
                if (ENABLED_PROCESSES[1]) {
                    eng.PHYSICS.update(TICK_RATE / 60.0);
                }
            }
        }

        @Override
        public void run() {
            Timer t = new Timer("engPHYSThread");
            t.scheduleAtFixedRate(engine_cycle, 0, TICK_RATE);
        }
    }
    private static class GraphicsThread implements Runnable {
        private Cycle gfxcyc = null;
        protected GraphicsThread(Engine ENGINE) {
            gfxcyc = new Cycle(ENGINE, this);
        }
        private static class Cycle extends TimerTask {
            Engine eng = null;
            GraphicsThread parent = null;

            protected Cycle(Engine eng, GraphicsThread parent) {
                this.eng = eng;
                this.parent = parent;
            }

            @Override
            public void run() {
                if (ENABLED_PROCESSES[2]) eng.DISPLAY.RENDER_ENABLED = true;
                else eng.DISPLAY.RENDER_ENABLED = false;
                if (ENABLED_PROCESSES[0]) {
                    eng.DISPLAY.repaint();
                }
            }
        }

        @Override
        public void run() {
            Timer t = new Timer("engGFXThread");
            t.scheduleAtFixedRate(gfxcyc,0,TICK_RATE+1);
        }
    }
    public void init(String TITLE, BufferedImage ICON) {
        WINDOW = new JFrame(Main.TITLE);
        WINDOW.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        WINDOW.setLocationRelativeTo(null);
        WINDOW.setIconImage(ICON);
        WINDOW.setMinimumSize(new Dimension(512,480));
        WINDOW.setExtendedState(WINDOW.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        WINDOW.setBackground(Color.BLACK);
        DISPLAY = new Display(WINDOW);
        WINDOW.setContentPane(DISPLAY);
        WINDOW.pack();
        WINDOW.setVisible(true);

        INPUT_CAPTURE = new InputCapture(DISPLAY);

        JButton temp = new JButton();
        temp.setText("poo");
        temp.setAlignmentX(0.5f);
        temp.setAlignmentY(0.5f);
        UIComponent temp1 = new UIComponent(temp);
        DISPLAY.addUIElement(temp1);

        PHYSICS = new Physics(this);
        PHC_Thread = new PhysicsThread(this);
        final Thread PHYSICS_THREAD = new Thread(PHC_Thread);
        PHYSICS_THREAD.start();

        RENDERER = new Renderer(this);
        DISPLAY.setRenderer(RENDERER);
        GFX_Thread = new GraphicsThread(this);
        final Thread GRAPHICS_THREAD = new Thread(GFX_Thread);
        GRAPHICS_THREAD.start();
    }

    protected Physics PhysicsEngine() {
        return this.PHYSICS;
    }

    protected Renderer RenderEngine() {
        return this.RENDERER;
    }

    public void addInputListener(InputListener il) {
        INPUT_CAPTURE.addInputListener(il);
    }

    public void addInputListener(int keyCode, InputListener il) {
        INPUT_CAPTURE.addInputListener(il, keyCode);
    }

    public void addInputListener(int[] keyCodes, InputListener il) {
        for (int keyCode : keyCodes) {
            INPUT_CAPTURE.addInputListener(il, keyCode);
        }
    }
    public void removeInputListener(InputListener il) {
        INPUT_CAPTURE.removeInputListener(il);;
    }
    public void removeInputListener(int keyCode, InputListener il) {
        INPUT_CAPTURE.removeInputListener(il, keyCode);
    }
    public void removeInputListener(int[] keyCodes, InputListener il) {
        for (int keyCode : keyCodes) {
            INPUT_CAPTURE.addInputListener(il, keyCode);
        }
    }

    public InputHandler createInputHandler(int[] keys) {
        InputHandler ih = new InputHandler(keys);
        addInputListener(ih);
        return ih;
    }

    public boolean getProcess(PROCESSES p) {
        return (ENABLED_PROCESSES[p.ordinal()]);
    }

    public void enableProcess(PROCESSES p) {
        ENABLED_PROCESSES[p.ordinal()] = true;
    }

    public void disableProcess(PROCESSES p) {
        ENABLED_PROCESSES[p.ordinal()] = false;
    }

    public void enableAllProcesses() {
        Arrays.fill(ENABLED_PROCESSES,true);
    }

    public void disableAllProcesses() {
        Arrays.fill(ENABLED_PROCESSES,false);
    }
}
