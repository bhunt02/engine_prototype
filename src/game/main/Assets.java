package main;

import classes.visuals.Sprite;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
public class Assets {
    public static final Hashtable<String, Sprite> Sprites = new Hashtable<>();
    public static final Hashtable<String, File> Stages = new Hashtable<>();
    private boolean ACCEPT(String name, boolean sprite) {
        return ((name.endsWith(".png") && sprite) || (name.endsWith(".xml") && !sprite));
    }
    private ArrayList<File> GET_FILE_LIST(File dir, boolean sprite) {
        ArrayList<File> compiled = new ArrayList<>();;
        File[] list = dir.listFiles();
        if (list != null) {
            for (File file : list) {
                if (file.isDirectory()) {
                    compiled.addAll(GET_FILE_LIST(file,sprite));
                } else {
                    if (ACCEPT(file.getName(),sprite)) {
                        compiled.add(file);
                    }
                }
            }
        }
        return compiled;
    }

    private void LOAD_SPRITES(ArrayList<File> SPRITE_FILES) {
        for (File f : SPRITE_FILES) {
            try {
                String name = f.getName().substring(0, f.getName().indexOf(".png"));
                if (f.getCanonicalPath().contains("player")) {
                   name = "player_"+name;
                }
                Sprites.put(name,new Sprite(f.toURI().toURL(),name));
            } catch(IOException ignored) {}
        }
    }

    private void LOAD_STAGES(ArrayList<File> STAGE_FILES) {
        for (File f : STAGE_FILES) {
            String name = f.getName().substring(0, f.getName().indexOf(".xml"));
            Stages.put(name,f);
        }
    }
    private void LOAD_ASSETS(ClassLoader ASSET_LOADER) {
        try {
            URL spURL = ASSET_LOADER.getResource("resources/Sprites/");
            URL stURL = ASSET_LOADER.getResource("resources/Stages/");
            File spritesDirectory = (spURL != null ? new File(spURL.toURI()) : null);
            File stagesDirectory = (stURL != null ? new File(stURL.toURI()) : null);

            if (spritesDirectory != null) {
                LOAD_SPRITES(GET_FILE_LIST(spritesDirectory,true));
            }
            if (stagesDirectory != null) {
                LOAD_STAGES(GET_FILE_LIST(stagesDirectory,false));
            }
        } catch(URISyntaxException | NullPointerException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void init() {
        final ClassLoader ASSET_LOADER = getClass().getClassLoader();
        LOAD_ASSETS(ASSET_LOADER);
    }
}
