package classes.visuals;

import classes.data_types.BoundingBox;
import classes.data_types.RGB;
import classes.data_types.Vector2;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import static machine.Renderer.getTileSize;
public class Sprite {
    public BufferedImage Image;
    public Vector2 Size = new Vector2();
    public BoundingBox Bounds;

    public String name = "";
    public Vector2 Scale = new Vector2(1.0,1.0);
    public Sprite(URL ImageURL, String name) {
        this.name = name;
        try {
            this.Image = ImageIO.read(ImageURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.Size.x = Image.getWidth();
        this.Size.y = Image.getHeight();
        if (this.Size.y > this.Size.x) Scale.y = this.Size.y/this.Size.x;
        else if (this.Size.x > this.Size.y) Scale.x = this.Size.x/this.Size.y;
        scaleToResolution();
        getTrueSize();
    }

    private void scaleToResolution() {
        int w = (int) (getTileSize()*this.Scale.x);
        int h = (int) (getTileSize()*this.Scale.y);
        double s_X = (getTileSize()*this.Scale.x)/(this.Size.x), s_Y = (getTileSize()*this.Scale.y)/(this.Size.y);
        AffineTransform at = AffineTransform.getScaleInstance(s_X,s_Y);
        AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage scaledImage = new BufferedImage(w,h, BufferedImage.TYPE_INT_ARGB);
        ato.filter(Image, scaledImage);
        this.Image = scaledImage;
        this.Size.x = Image.getWidth();
        this.Size.y = Image.getHeight();
    }
    public static BufferedImage getMirrored(BufferedImage src, int axis) {
        BufferedImage mir = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        if (axis == 0) {
            for (int y = 0; y < src.getHeight(); y++) {
                for (int lx = 0, rx = src.getWidth() - 1; lx < src.getWidth(); lx++, rx--) {
                    int l = src.getRGB(lx, y);
                    int r = src.getRGB(rx, y);
                    mir.setRGB(lx, y, r);
                    mir.setRGB(rx, y, l);
                }
            }
        } else if (axis == 1) {
            for (int x = 0; x < src.getWidth(); x++) {
                for (int by = 0, ty = src.getHeight() - 1; by < src.getHeight() - 1; by++, ty--) {
                    int t = src.getRGB(x, ty);
                    int b = src.getRGB(x, by);
                    mir.setRGB(x, ty, b);
                    mir.setRGB(x, by, t);
                }
            }
        }
        return mir;
    }
    public static BufferedImage getTinted(BufferedImage src, RGB tint) {
        BufferedImage tin = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < src.getWidth() - 1; x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                int[] rgb = RGB.fromInt(src.getRGB(x, y));
                tin.setRGB(x, y, (
                        (((rgb[0] + tint.R) >> 1) << 24) |
                                (((rgb[1] + tint.G) >> 1) << 16) |
                                (((rgb[2] + tint.B) >> 1) << 8) |
                                (rgb[3])
                ));
            }
        }
        return tin;
    }
    private void getTrueSize() {
        BoundingBox bounds = new BoundingBox();
        int xLow = (int) Size.y;
        int yLow = (int) Size.y;
        int xHigh = -1;
        int yHigh = -1;
        for (int x = 0; x < Size.x; x++) {
            for (int y = 0; y < Size.y; y++) {
                int alpha = (0xFF & (Image.getRGB(x, y) >> 24));
                if (alpha == 0xFF) {
                    if (x < xLow) xLow = x;
                    if (yLow > y) yLow = y;
                    if (x > xHigh) xHigh = x;
                    if (yHigh < y) yHigh = y;
                }
            }
        }
        bounds.x0 = xLow;
        bounds.x1 = xHigh;
        bounds.y0 = -1+(Size.y-yHigh);
        bounds.y1 = (Size.y-yLow);
        Bounds = bounds;
    }
}
