package machine;

import classes.objects.BaseObject;
import classes.data_structures.ConcurrentSet;
import classes.data_structures.DynamicAreaTree.Node;
import classes.data_types.Vector2;
import classes.visuals.Sprite;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Renderer {
    private final Engine ENGINE;
    protected Renderer(Engine ENGINE) {
        this.ENGINE = ENGINE;
    }
    private static final int TILE_SIZE = 32;
    private static final Dimension DRAW_RESOLUTION = new Dimension(16*TILE_SIZE,15*TILE_SIZE);

    /* RENDER LAYERS
     * {0} - SKY
     * {1} - BACKGROUND II
     * {2} - BACKGROUND I
     * {3} - MAIN
     * {4} - FOREGROUND (STAGE)
     * {5} - FOREGROUND (SCENE)
    */
    final private double[] LAYER_SCALE_WEIGHTS = new double[] {
            0.7,
            0.8,
            0.9,
            1.0,
            1.0,
            1.1
    };
    public BufferedImage Render(Dimension SCREEN_SIZE) {
        BufferedImage RENDER = createLayer();
        Graphics2D g = RENDER.createGraphics();
        g.setColor(Color.black); //new Color(60,0,0));
        g.fillRect(0,0,RENDER.getWidth(),RENDER.getHeight());
        g.dispose();

        final BufferedImage[] LAYERS = new BufferedImage[6];
        if (ENGINE.PhysicsEngine() != null) {
            LAYERS[3] = drawTileLayers(ENGINE.PhysicsEngine().getObjects());
            LAYERS[4] = drawTileLayers(ENGINE.PhysicsEngine().testForegroundObjects);
        }
        // parse through drawing layers and composite them onto the render
        ParallaxPass(RENDER,LAYERS);

        if (ENGINE.getProcess(Engine.PROCESSES.DEBUG)) {
            drawBoundingBox(ENGINE.PhysicsEngine().getCollisionTree().ROOT, RENDER);
        }

        // scale rendered image to fit in screen
        double displayScale = Math.min((SCREEN_SIZE.getWidth()-128)/DRAW_RESOLUTION.getWidth(),(SCREEN_SIZE.getHeight()-128)/DRAW_RESOLUTION.getHeight());
        RENDER = getScaledInstance(RENDER,displayScale);

        return RENDER;
    }
    private void drawBoundingBox(Node n,BufferedImage RENDER) {
        if (n == null || n.element == null) return;
        int sizeX = (int) (n.element.x1-n.element.x0);
        int sizeY = (int) (n.element.y1-n.element.y0);

        Graphics2D g = RENDER.createGraphics();
        g.scale(1,-1);
        g.translate(0,-RENDER.getHeight());
        g.setColor(Color.red);
        g.drawRect((int) n.element.x0, (int) n.element.y0,sizeX,sizeY);
        g.dispose();

        drawBoundingBox(n.left,RENDER, 0.1F,1);
        drawBoundingBox(n.right,RENDER,0.1F,1);
    }
    private void drawBoundingBox(Node n, BufferedImage RENDER, float hue, double sc) {
        if (n == null || n.element == null) return;

        int sizeX = (int) ((n.element.x1-n.element.x0)*sc);
        int sizeY = (int) ((n.element.y1-n.element.y0)*sc);
        Graphics2D g = RENDER.createGraphics();
        g.scale(1,-1);
        g.translate(0,-RENDER.getHeight());
        g.setColor(Color.getHSBColor(hue,1,1));
        g.drawRect((int) n.element.x0, (int) n.element.y0,sizeX,sizeY);
        g.dispose();

        drawBoundingBox(n.left,RENDER,hue+0.1F,sc);
        drawBoundingBox(n.right,RENDER,hue+0.1F,sc);
    }
    private BufferedImage drawTileLayers(ConcurrentSet<BaseObject> Objects) {
        BufferedImage BUFFER = createLayer();
        Graphics2D g = BUFFER.createGraphics();
        for (BaseObject b : Objects) {
            if (b.Sprite == null || b.Sprite.Image == null) continue;
            BufferedImage image = b.Sprite.Image;

            double yRatio = 1.0;
            if (image.getHeight() > image.getWidth()) {
                yRatio = (image.getHeight()*1.0)/image.getWidth();
            }

            if (!b.getMirrored()) image = Sprite.getMirrored(image,0);
            if (b.getFlipped()) image = Sprite.getMirrored(image,1);
            if (b.getTint() != null) image = Sprite.getTinted(image,b.getTint());
            Vector2 pos = b.getPosition();

            g.drawImage(image,
                    (int) pos.x,
                    (int) Math.round((BUFFER.getHeight()-(TILE_SIZE*yRatio))-pos.y), // Convert Cartesian Y to Graphics Y
                    null
            );
        }
        g.dispose();
        return BUFFER;
    }

    public void drawStageLayers() {

    }

    private void ParallaxPass(BufferedImage BUFFER, BufferedImage[] LAYERS) {
        Graphics2D g = BUFFER.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        for (int i = 0; i < LAYERS.length; i++) {
            if (LAYERS[i] == null) continue;
            BufferedImage layer = LAYERS[i];
            double scale_weight = LAYER_SCALE_WEIGHTS[i];
            int w = (int) (layer.getWidth()*scale_weight), h = (int) (layer.getHeight()*scale_weight);
            int xDiff = w-BUFFER.getWidth(), yDiff = h-BUFFER.getHeight();
            g.drawImage(layer,-xDiff/2,-yDiff/2,w,h,null);
        }
        g.dispose();
    }

    private BufferedImage createLayer() {
        return new BufferedImage((int) DRAW_RESOLUTION.getWidth(),(int) DRAW_RESOLUTION.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }
    private BufferedImage getScaledInstance(BufferedImage LAYER, double scale) {
        int w = (int) (LAYER.getWidth()*scale);
        int h = (int) (LAYER.getHeight()*scale);
        double x_Ratio = (w * 1.0)/LAYER.getWidth();
        double y_Ratio = (h * 1.0)/LAYER.getHeight();
        AffineTransform at = AffineTransform.getScaleInstance(x_Ratio, y_Ratio);
        AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage NEW_LAYER = new BufferedImage(w,h, BufferedImage.TYPE_INT_ARGB);
        ato.filter(LAYER, NEW_LAYER);
        return NEW_LAYER;
    }

    public static Dimension getDrawResolution() {
        return DRAW_RESOLUTION;
    }
    public static int getTileSize() {
        return TILE_SIZE;
    }
}
