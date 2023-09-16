package main;

import machine.Engine;
public class Main {
    public static final String TITLE = "Demo";
    public static final Assets ASSETS = new Assets();
    public static final machine.Engine Engine = new Engine();

    public static void main(String[] args) {
        ASSETS.init();
        Engine.init(TITLE,Assets.Sprites.get("ic_window").Image);
        //Thread game_thread = new Thread();
    }
}
