package classes.data_types;

import classes.utilities.VMath;

public class RGB {
    public int R;
    public int G;
    public int B;
    public int Color;
    public RGB(int R, int G, int B) {
        this.R = R;
        this.G = G;
        this.B = B;
        this.Color = ((R << 24) | (G << 16) | (B << 8) | 0xFF);
    }

    public RGB(int RGB) {
        int[] colors = fromInt(RGB);
        this.Color = RGB;
        this.R = colors[0]; this.G = colors[1]; this.B = colors[2];
    }

    public static int[] fromInt(int RGB) {
        return new int[] {
            (RGB >> 16) & 0xFF, // RED
            (RGB >> 8) & 0xFF, // GREEN
            (RGB) & 0xFF,  // BLUE
            (RGB >> 24) & 0xFF,         // ALPHA
        };
    }

    public static int toInt(int R, int B, int G, int A) {
        return ((R << 24) | (G << 16) | (B << 8) | A);
    }

    public static int mult(RGB color0, RGB color1) {
        return (((color0.R*color1.R >> 2) << 24) | ((color0.G*color1.G >> 2) << 16) | ((color0.B*color1.B >> 2) << 8) | 0xFF);
    }

    public static int mult(int c0, int c1) {
        return (
                (((c0 << 24) * (c1 << 24)) >> 2)
                | (((c0 << 16) * (c1 << 16)) >> 2)
                | (((c0 << 8) * (c1 << 8)) >> 2)
                | 0xFF
                );
    }
    public static int mult(RGB color0, double m) {
        m = VMath.clamp(0.0,1.0,m);
        return (((int) (color0.R*m) << 24) | ((int) (color0.G*m) << 16) | ((int) (color0.B*m) << 8) | 0xFF);
    }

    public static int mult(int c, double m) {
        m = VMath.clamp(0.0,1.0,m);
        return ((int) ((c << 24) * m)  | (int) ((c << 16) * m) | (int) ((c << 8) * m) | 0xFF);
    }
}
