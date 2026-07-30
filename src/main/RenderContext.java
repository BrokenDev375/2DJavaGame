package main;

import entity.Entity;

public interface RenderContext {
    GameConfig getConfig();

    Camera camera();

    Entity player();

    int frameCounter();
}
