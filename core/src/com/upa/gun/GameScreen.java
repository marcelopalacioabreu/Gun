package com.upa.gun;

import com.badlogic.gdx.ScreenAdapter;

public class GameScreen extends ScreenAdapter {
    private GunGame game;
    private Renderer renderer;

    GameScreen(GunGame game) {
        this.game = game;

        renderer = game.renderer;
    }

    @Override
    public void render(float delta) {
        game.world.update(delta);
        renderer.draw(game.world);
        game.world.deleteMarkedForDeletion();
    }

}
