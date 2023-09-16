package classes.visuals;

import java.awt.*;
import java.util.ArrayList;

public class UIComponent extends Container implements Comparable<UIComponent> {
    int displayOrder = 0;
    public UIComponent(ArrayList<Component> elements) {
        for (Component e : elements) {
            this.add(e);
        }
    }
    public UIComponent(Component element) {
        this.add(element);
    }

    @Override
    public int compareTo(UIComponent o) {
        return this.displayOrder-o.displayOrder;
    }
}
