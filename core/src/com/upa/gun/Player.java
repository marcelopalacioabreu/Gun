package com.upa.gun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

public class Player extends Entity {
    private static final float HITBOX_SIZE = 15f;

    boolean rolling;
    boolean hurt;

    Vector2 spawnPoint;

    double bulletCooldown;

    public boolean topStop = false;
    public boolean botStop = false;
    public boolean leftStop = false;
    public boolean rightStop = false;

    private int health;

    boolean iframe;
    float iframeTimer;

    private GunGame game;
    private InputHandler inputHandler;

    float timeSinceRoll;

    Sound shot;

    Direction direction;

    PlayerState state;

    Player(float x, float y, GunGame game) {
        super(x, y, Assets.getTextureSize(Assets.playerAnimations).x, Assets.getTextureSize(Assets.playerAnimations).y,
                0, 0);
        spawnPoint = new Vector2(x, y);

        state = PlayerState.idle;

        bulletCooldown = 0.2f;
        hurt = false;

        health = Settings.PLAYER_HEALTH;

        direction = Direction.DOWN;

        iframe = false;
        iframeTimer = 0f;

        timeSinceRoll = Settings.ROLL_DELAY;

        this.game = game;
        state.setGame(game);
        shot = Gdx.audio.newSound(Gdx.files.internal("sfx/gunshot.mp3"));

        inputHandler = new InputHandler();
    }

    /**
     * Create player's hitbox (called by Entity constructor.)
     */
    @Override
    void createHitbox() {
        Vector2 position = getPosition();
        hitbox = new RectangularHitbox(position.x, position.y, HITBOX_SIZE, HITBOX_SIZE);
        centerHitbox();
        System.out.println("Size: " + getSize());
        System.out.println("Hitbox size: " + hitbox.getWidth() + ", " + hitbox.getHeight());
        System.out.println("Offset: " + getHitboxOffset());
    }

    /**
     * Return player's current health
     * @return Player's current health
     */
    int getHealth() {
        return health;
    }

    /**
     * Damages player if player is currently vulnerable, kills them if health drops below zero (this should probably be
     * handled in update() instead.)
      * @param damage Amount of damage in hit points to deal to player.
     */
    void hurt(int damage) {
        if (!iframe && !game.world.cinematicHappening) {
            health -= damage;
            iframe = true;
            if (health <= 0) {
                state = PlayerState.dying;
            }
        }
    }

    /**
     * Returns the correct SpriteState mapping the player's current state to the corresponding texture.
     * @return The SpriteState corresponding to the current state.
     */
    SpriteState getState() {
        return state.getTextureState();
    }

    /**
     * Start a roll in the current direction of movement, if not already rolling.
     */
    void roll() {
        if (!rolling) {
            rolling = true;
            Vector2 rollAngle = Direction.getAngle(direction).setLength(Settings.ROLL_SPEED);
            setVelocity(rollAngle);
        }
    }

    /**
     * Stop the player's movement.
     */
    private void stop() {
        setVelocity(0, 0);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        state.update(delta);

        timeSinceRoll += delta;

        bulletCooldown -= delta;

        if (state.controllable) {
            inputHandler.update(delta);
        }

        if (iframe) {
            iframeTimer += delta;
            if (iframeTimer > Settings.I_FRAME_LENGTH) {
                //System.out.println("iframe over");
                iframe = false;
                //opacity = 1f;
                iframeTimer = 0f;
            }
        }
    }
}
