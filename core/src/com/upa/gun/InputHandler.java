package com.upa.gun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import static com.upa.gun.Settings.MUTE;

public class InputHandler implements Updatable {
    private Vector2 checkKeys(float delta) {
        Vector2 velocity = new Vector2(0f, 0f);

        if (Gdx.input.isKeyPressed(Settings.KEY_LEFT)) {
                velocity.x -= Settings.playerSpeed;
                World.player.state = PlayerState.moving;
        }

        if (Gdx.input.isKeyPressed(Settings.KEY_RIGHT)) {
                velocity.x += Settings.playerSpeed;
                World.player.state = PlayerState.moving;
        }

        if (Gdx.input.isKeyPressed(Settings.KEY_DOWN)) {
                velocity.y -= Settings.playerSpeed;
                World.player.state = PlayerState.moving;
        }

        if (Gdx.input.isKeyPressed(Settings.KEY_UP)) {
                velocity.y += Settings.playerSpeed;
                World.player.state = PlayerState.moving;
        }

        if (Gdx.input.isKeyJustPressed(Settings.KEY_ROLL)) {
            if(World.player.timeSinceRoll >= Settings.ROLL_DELAY) {
                World.player.timeSinceRoll = 0f;
                World.player.state = new PlayerRollingState(World.player.direction);
            }
        }

        if (!Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)){
            World.player.state = PlayerState.idle;
        }

        return velocity.setLength(Settings.playerSpeed);
    }

    /**
     * Handle all player input
     * @param delta Frame time for current tick
     */
    @Override
    public void update(float delta) {
        Vector2 velocity = new Vector2(0f, 0f);
        if (World.player.state.controllable) {
            velocity = checkKeys(delta);
            if (velocity.x != 0f || velocity.y != 0f) {
                World.player.direction = Direction.getDirection(velocity);
            }
        }

        World.player.setVelocity(velocity);

        if (Gdx.input.justTouched()) {
            OrthographicCamera camera = new OrthographicCamera();
            camera.setToOrtho(false, Settings.RESOLUTION.x, Settings.RESOLUTION.y);

            if(World.player.bulletCooldown <= 0) {
                Vector3 mousePos3 = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(),
                        0f));
                Vector2 mousePos = new Vector2(mousePos3.x, mousePos3.y);
                Vector2 bulletAngle = mousePos.sub(World.player.getCenter());
                World.playerBullets.add(new FriendlyBullet(World.player.getCenter(),
                        bulletAngle.angleRad()));
                World.player.shot.stop();
                if(!MUTE) {
                    World.player.shot.play(.5f);
                }
                World.player.bulletCooldown = Settings.playerBulletCooldown;
            }
        }
    }

    public void pausedUpdate(float delta) {
        if(Gdx.input.isKeyJustPressed(Settings.KEY_PAUSE)) {
            GunGame game = World.player.getGunGame();
            game.renderer.disableAllButtons();
            switch(World.activity) {
                case 0:
                    World.activity = 1;
                    game.renderer.enablePauseButtons();
                    break;
                case 1:
                    World.activity = 0;
                    break;
                case 2:
                    World.activity = 1;
                    game.renderer.enablePauseButtons();
                    break;
                case 3:
                    World.activity = 1;
                    game.renderer.enablePauseButtons();
                    break;
                case 4:
                    World.activity = 1;
                    game.renderer.enablePauseButtons();
                    break;
                case 5:
                    break;
                default:
                    Gdx.app.log("InputHandler", "Found invalid activity identifier (" + World.activity + ")");
                    break;
            }
        }
    }

}
