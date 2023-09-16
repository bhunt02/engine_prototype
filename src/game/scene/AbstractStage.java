package scene;

import objects.tiles.Tile;

abstract class AbstractStage {
    protected Tile[][] BASE_TILES = new Tile[16][15];
    protected Tile[][] FORE_TILES = new Tile[16][15];
}
