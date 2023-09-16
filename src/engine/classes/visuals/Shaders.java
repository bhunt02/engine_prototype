package classes.visuals;

import machine.Renderer;
import classes.data_types.RGB;
import classes.data_types.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Shaders {
    Renderer RENDERER = null;
    public Shaders(Renderer RENDERER) {
        this.RENDERER = RENDERER;
    }

    public BufferedImage sceneShader(BufferedImage SCENE, BufferedImage NORMAL) {
        BufferedImage OUTPUT = new BufferedImage(SCENE.getWidth(),SCENE.getHeight(), BufferedImage.TYPE_INT_ARGB);
        return OUTPUT;
    }

    private BufferedImage generateSpecular(BufferedImage SCENE, BufferedImage NORMAL, ArrayList<LightSource> SPECULAR) {
        BufferedImage Output = new BufferedImage(SCENE.getWidth(), SCENE.getHeight(), BufferedImage.TYPE_INT_ARGB);
        BufferedImage[] DISTRIBUTIVES = new BufferedImage[SPECULAR.size()];
        for (int i = 0; i < DISTRIBUTIVES.length; i++) {
            DISTRIBUTIVES[i] = new BufferedImage(SCENE.getWidth(), SCENE.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        for (int x = 0; x < SCENE.getWidth(); x++) {
            for (int y = 0; y < SCENE.getHeight(); y++) {
                int nRGB = (NORMAL.getRGB(x, y));
                int cRGB = (SCENE.getRGB(x, y));
                Vector2 pos = new Vector2(x, SCENE.getHeight() - Renderer.getTileSize() - y);
                for (int i = 0; i < SPECULAR.size(); i++) {
                    LightSource s = SPECULAR.get(i);
                    BufferedImage specularLayer = DISTRIBUTIVES[i];
                    Graphics2D g = specularLayer.createGraphics();
                    g.drawImage(SCENE, 0, 0, null);
                    double lightIntensity = s.intensity * s.getRadialFalloff(pos) * s.getAngularFalloff(pos) * s.getNormalFalloff(pos);
                    int lightColor = RGB.mult(s.lightTint,lightIntensity);
                    int shadedColor = RGB.mult(cRGB,lightColor) + RGB.mult(lightColor,s.volumetricIntensity);
                    specularLayer.setRGB(x,y,shadedColor);
                }
            }
        }
        Graphics2D g = Output.createGraphics();

        for (int x = 0; x < Output.getWidth(); x++) {
            for (int y = 0; y < Output.getHeight(); y++) {
                int sumR = 0, sumG = 0, sumB = 0, sumA = 0;
                for (int i = 0; i < DISTRIBUTIVES.length; i++) {
                    int[] rgb = RGB.fromInt(DISTRIBUTIVES[i].getRGB(x,y));
                    sumR += rgb[0]; sumG += rgb[1]; sumB += rgb[2]; sumA += rgb[3];
                }
                int nRGB = RGB.toInt(sumR/DISTRIBUTIVES.length,sumG/DISTRIBUTIVES.length,sumB/DISTRIBUTIVES.length,sumA/DISTRIBUTIVES.length);
                Output.setRGB(x,y,nRGB);
            }
        }
        return null;
    }
}
