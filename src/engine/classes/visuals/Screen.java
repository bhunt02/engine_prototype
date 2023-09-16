package classes.visuals;

import javax.swing.*;
import java.awt.*;

public abstract class Screen extends JPanel {
    protected Screen() {
        setIgnoreRepaint(true);
        setDoubleBuffered(true);
        this.setFocusable(true);
        this.setBackground(Color.BLACK);
    }

    protected void addUIElement(UIComponent UI) {
        if (UI == null) return;
        this.add(UI);
    }

    protected void removeUIElement(UIComponent UI) {
        if (UI == null) return;
        this.remove(UI);
    }
}
