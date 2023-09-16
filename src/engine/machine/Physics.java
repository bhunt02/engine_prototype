package machine;

import classes.objects.Terrain;
import main.Assets;
import classes.objects.BaseObject;
import classes.objects.Entity;
import objects.entities.Player;
import objects.tiles.Tile;
import scene.StageReader;
import classes.data_structures.ConcurrentSet;
import classes.data_structures.DynamicAreaTree;

import java.util.ArrayList;
import java.util.Hashtable;

public class Physics {
    private Engine ENGINE;
    protected Physics(Engine ENGINE) {
        this.ENGINE = ENGINE;
    }
    private static final Hashtable<Integer,String> tile_Codes = new Hashtable<>();
    private static final Hashtable<Integer,String> decor_Codes = new Hashtable<>();
    static {
        tile_Codes.put(0, "3_FORM_BRICK");
        tile_Codes.put(1, "BRICK");
        tile_Codes.put(2, "GROUND");
        tile_Codes.put(3, "PLATFORM");
        tile_Codes.put(4, "STONE");
        tile_Codes.put(5, "STUD_BRICK");
        tile_Codes.put(6, "WARPED");
        tile_Codes.put(8, "CLOUD");
        tile_Codes.put(9, "CLOUD_CORNER");
        tile_Codes.put(10, "ENEMY_POT");
        tile_Codes.put(11, "GLOSSY_PLATFORM");
        tile_Codes.put(12, "GLOSSY_PLATFORM_CORNER");
        tile_Codes.put(13, "ICE_CORNER");
        tile_Codes.put(14, "ICE_TOP");
        tile_Codes.put(15, "ICEY_PLATFORM");
        tile_Codes.put(16, "ICEY_PLATFORM_CORNER");
        tile_Codes.put(17, "LADDER");
        tile_Codes.put(18, "SPECIAL_BRICK");
        tile_Codes.put(19, "STUD_PLATFORM");
        tile_Codes.put(20, "STUDDED_BRICK");
        tile_Codes.put(21, "ICE_BOTTOM");
        tile_Codes.put(22, "TEMPLE_BRICK");
        tile_Codes.put(25, "TEMPLE_ROOF");
        tile_Codes.put(26, "TEMPLE_ROOF_INNER_CORNER");
        decor_Codes.put(3,"BUSH");
        decor_Codes.put(4,"COLUMN");
        decor_Codes.put(5,"DOOR_CLOSED");
        decor_Codes.put(6,"DOOR_OPEN");
        decor_Codes.put(7,"GRASS_COVER");
        decor_Codes.put(8,"HANGING_COLUMN_A");
        decor_Codes.put(9,"PILLAR");
        decor_Codes.put(10,"CLOUDTOP");
        decor_Codes.put(11,"CLOUDTOP_CORNER");
        decor_Codes.put(13,"TEMPLE_ROOF_CORNER");
        decor_Codes.put(15,"TOMBSTONE");
        decor_Codes.put(16,"LAVA");
        decor_Codes.put(17,"WATER_TOP");
        decor_Codes.put(18,"WATER");
        decor_Codes.put(19,"SPRING");
        decor_Codes.put(20,"CLOUD_LONG");
        decor_Codes.put(21,"CLOUD_MEDIUM");
        decor_Codes.put(22,"CLOUD_HUGE");
        decor_Codes.put(23,"TREE");
        decor_Codes.put(26,"COLUMN_CORINTHIAN_BASE");
        decor_Codes.put(27,"COLUMN_CORINTHIAN_TOP");
        decor_Codes.put(28,"COLUMN_IONIC_BASE");
        decor_Codes.put(29,"COLUMN_IONIC_TOP");
        decor_Codes.put(30,"COLUMN_MIDDLE");
        decor_Codes.put(31,"PILLAR_BOTTOM");
        decor_Codes.put(32,"PILLAR_BOTTOM_BROKEN_A");
        decor_Codes.put(33,"PILLAR_BOTTOM_BROKEN_B");
        decor_Codes.put(34,"PILLAR_BOTTOM_BROKEN_C");
        decor_Codes.put(35,"PILLAR_MIDDLE");
        decor_Codes.put(36,"PILLAR_MIDDLE_BROKEN_A");
        decor_Codes.put(37,"PILLAR_MIDDLE_BROKEN_B");
        decor_Codes.put(38,"PILLAR_MIDDLE_BROKEN_C");
        decor_Codes.put(39,"PILLAR_TOP");
        decor_Codes.put(40,"PILLAR_TOP_BROKEN_A");
        decor_Codes.put(41,"PILLAR_TOP_BROKEN_B");
        decor_Codes.put(42,"PILLAR_TOP_BROKEN_C");
        decor_Codes.put(99,"STATUE_UNDERWORLD");
        decor_Codes.put(100,"LIONGOYLE");
    }

    // Units/frame acceleration downward
    public static final double GRAVITY = -0.98;
    protected final ConcurrentSet<BaseObject> PhysicalObjects = new ConcurrentSet<>();
    public final ConcurrentSet<BaseObject> testForegroundObjects = new ConcurrentSet<>();
    private final DynamicAreaTree CollisionTree = new DynamicAreaTree();
    {
        StageReader s = new StageReader(Assets.Stages.get("1-1").getPath());
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 32; y++) {
                int[] res = s.getTileData(x, y);
                if (res == null) continue;
                if (tile_Codes.containsKey(res[0])) {
                    boolean anchored = true; //(y < 4);
                    Terrain t = new Terrain(
                            x*Renderer.getTileSize(),
                            y*Renderer.getTileSize(),
                            0,
                            0,
                            anchored,
                            true,
                            0.5,
                            1.0,
                            1.0,
                            tile_Codes.get(res[0])
                    );
                    addObject(t);
                }
                if (decor_Codes.containsKey(res[1])) {
                    testForegroundObjects.add(new Tile(x*Renderer.getTileSize(),y*Renderer.getTileSize(),decor_Codes.get(res[1])));
                }
            }
        }
        Player p = new Player(3*Renderer.getTileSize(),5*Renderer.getTileSize());
        //Entity e1 = new Entity(5*Renderer.getTileSize(),12*Renderer.getTileSize(),2,0.5,0.2,"STONE");
        //e1.Velocity.x = -4;
        //Entity e2 = new Entity(4*Renderer.getTileSize(),5*Renderer.getTileSize(),2,0.1,0.5,"3_FORM_BRICK");
        //e2.Velocity.x = 4;
        //e2.Velocity.y = 12;
        addObject(p);
        //addObject(e1);
        //addObject(e2);
    }
    protected void update(double dt) {
        Hashtable<BaseObject,ArrayList<BaseObject>> ignoreLists = new Hashtable<>();
        for (BaseObject b : PhysicalObjects) {
            if (b.getAnchored()) continue;
            b.gravityAcceleration();
            b.update(dt);

            ArrayList<BaseObject> ignoreList = null;
            if (ignoreLists.containsKey(b)) ignoreList = ignoreLists.get(b);

            ArrayList<BaseObject> touched = Collisions(b,ignoreList);

            for (BaseObject c : touched) {
                ArrayList<BaseObject> list;
                if (!ignoreLists.containsKey(c)) {
                    list = new ArrayList<>();
                    ignoreLists.put(c,list);
                } else {
                    list = ignoreLists.get(c);
                }
                list.add(b);
            }
        }
    }
    protected void discardObject(BaseObject object) {
        PhysicalObjects.remove(object);
        CollisionTree.remove(object);
    }
    protected void addObject(BaseObject object) {
        boolean added = (PhysicalObjects.add(object));
        if (added && object.getCanCollide()) {
            CollisionTree.add(object);
        }
    }
    protected ConcurrentSet<BaseObject> getObjects() {
        return PhysicalObjects;
    }
    protected DynamicAreaTree getCollisionTree() {
        return CollisionTree;
    }
    private ArrayList<BaseObject> Collisions(BaseObject obj, ArrayList<BaseObject> ignoreList) {
        ArrayList<BaseObject> collisions = CollisionTree.getCollisionPairs(obj,ignoreList);
        ArrayList<BaseObject> touched = new ArrayList<>();

        for (BaseObject other: collisions) {
            if (!other.getAnchored()) {
                other.Collision(obj);
                touched.add(other);
            }
            obj.Collision(other);
        }

        return touched;
    }
}

