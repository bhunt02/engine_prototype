package machine;

import classes.visuals.Screen;
import classes.visuals.UIComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Display extends Screen {
    private Renderer RENDERER = null;
    private JFrame WINDOW = null;
    protected boolean RENDER_ENABLED = false;
    JButton temp = null;
    Display(JFrame WINDOW) {
        super();
        this.WINDOW = WINDOW;
        LayoutManager lm = new OverlayLayout(this);
        this.setLayout(lm);
    }

    protected void paintComponent(Graphics g) {
        if (RENDER_ENABLED && RENDERER != null) {
            BufferedImage RENDER = RENDERER.Render(WINDOW.getSize());
            int xDiff = WINDOW.getWidth() - RENDER.getWidth(), yDiff = WINDOW.getHeight() - RENDER.getHeight();
            g.drawImage(RENDER, xDiff / 2, yDiff / 2, null);
        }
    }

    public void addUIElement(UIComponent UI) {
        super.addUIElement(UI);
    }

    public void removeUIElement(UIComponent UI) {
        super.removeUIElement(UI);
    }

    void setRenderer(Renderer RENDERER) {
        this.RENDERER = RENDERER;
    }
}
