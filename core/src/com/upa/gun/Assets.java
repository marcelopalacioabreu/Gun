package com.upa.gun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

public class Assets {
    @SuppressWarnings("LibGDXStaticResource")
    private static AssetManager assetManager;

    public static Texture powerup1; //temp, will make atlas later


    public static Texture floor;
    public static Texture pauseBackground;

    public static Texture crate;
    public static Texture crateTop;
    public static Texture crateSide;
    public static Texture doorTop;
    public static Texture doorSide;


    public static Texture shadow;

    public static TextureAtlas playerAtlas;
    public static TextureAtlas slimeAtlas;
    public static TextureAtlas strongSlimeAtlas;
    public static TextureAtlas bossSlimeAtlas;
    public static TextureAtlas buttonSkins;
    public static TextureAtlas healthBars;
    public static TextureAtlas powerups;

    public static Texture bullets;
    public static TextureRegion bulletLaser;
    public static Texture bulletsEnemies;
    public static TextureRegion bulletEnemy;
    public static Texture bulletsEnemiesBoss;
    public static TextureRegion bulletBoss;

    public static BitmapFont menuFont;

    public Pixmap pm;
    public static Texture crosshair;
    public static Texture border;

    public static Sound bulletSound;
    public static Sound bossDieSound;

    public static TextureAtlas spriteAtlas;
    public static Map<SpriteState, Map<Direction, Animation<TextureRegion>>> playerAnimations;
    public static Map<SpriteState, Map<Direction, Animation<TextureRegion>>> slimeAnimations;
    public static Map<SpriteState, Map<Direction, Animation<TextureRegion>>> strongSlimeAnimations;
    public static Map<SpriteState, Map<Direction, Animation<TextureRegion>>> bossSlimeAnimations;

    public static ShaderProgram flashWhiteShader;

    private static Map<AnimationKey, Animation<TextureRegion>> animations;

    private static Texture loadTexture(String filepath) {
        return new Texture(Gdx.files.internal(filepath));
    }

    static void load() {
        assetManager = new AssetManager();
        animations = new HashMap<AnimationKey, Animation<TextureRegion>>();

        assetManager.load("sprites/enemies.atlas", TextureAtlas.class);
        assetManager.finishLoading();
        spriteAtlas = new TextureAtlas(Gdx.files.internal("sprites/sprites.atlas"));

        buttonSkins = new TextureAtlas(Gdx.files.internal("sprites/buttons.atlas"));

        floor = loadTexture("sprites/stages/floor.png");
        pauseBackground = loadTexture("sprites/stages/pause_background.png");
        border = loadTexture("sprites/stages/border.png");
        crate = loadTexture("sprites/terrain/crate.png");
        crateTop = loadTexture("sprites/terrain/crate_top.png");
        crateSide = loadTexture("sprites/terrain/crate_side.png");
        doorTop = loadTexture("sprites/terrain/door_top.png");
        doorSide = loadTexture("sprites/terrain/door_side.png");


        shadow = loadTexture("sprites/miscellaneous/shadow.png");
        shadow.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        healthBars = new TextureAtlas(Gdx.files.internal("sprites/healthBars.atlas"));
        powerups = new TextureAtlas(Gdx.files.internal("sprites/powerups.atlas"));

        playerAtlas = new TextureAtlas(Gdx.files.internal("sprites/player.atlas"));
        playerAnimations = new HashMap<SpriteState, Map<Direction, Animation<TextureRegion>>>();
        loadPlayerAnimations();


        crosshair = new Texture(Gdx.files.internal("sprites/miscellaneous/crosshair.png"));
        crosshair.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        Pixmap crosshairPixmap = new Pixmap(Gdx.files.internal("sprites/miscellaneous/crosshair.png"));
		Cursor cursor = Gdx.graphics.newCursor(crosshairPixmap, crosshairPixmap.getWidth()/2, crosshairPixmap.getHeight()/2);
		Gdx.graphics.setCursor(cursor);

        slimeAtlas = new TextureAtlas(Gdx.files.internal("sprites/slime.atlas"));
        slimeAnimations = new HashMap<SpriteState, Map<Direction, Animation<TextureRegion>>>();
        loadSlimeAnimations();

        strongSlimeAtlas = new TextureAtlas(Gdx.files.internal("sprites/strongSlime.atlas"));
        strongSlimeAnimations = new HashMap<SpriteState, Map<Direction, Animation<TextureRegion>>>();
        loadStrongSlimeAnimations();

        bossSlimeAtlas = new TextureAtlas(Gdx.files.internal("sprites/bossSlime.atlas"));
        bossSlimeAnimations = new HashMap<SpriteState, Map<Direction, Animation<TextureRegion>>>();
        loadBossSlimeAnimations();

        bullets = loadTexture("sprites/bullets/laserBullet.png");

        bulletLaser = new TextureRegion(bullets, 0, 0, 33, 14);

        bulletsEnemies = loadTexture("sprites/bullets/slimePellet.png");
        bulletEnemy = new TextureRegion(bulletsEnemies, 0, 0, 13, 16);

        bulletsEnemiesBoss = loadTexture("sprites/bullets/bossPellet.png");
        bulletBoss = new TextureRegion(bulletsEnemiesBoss, 0, 0,13,16);

        menuFont = new BitmapFont();

        loadShaders();

        bulletSound = Gdx.audio.newSound(Gdx.files.internal("sfx/gunshot.mp3"));
        bossDieSound = Gdx.audio.newSound(Gdx.files.internal("sfx/bossdie.wav"));


    }

    /**
     * Load or return the given animation.
     * @param key The key identifying the animation.
     * @return The animation corresponding to the key.
     */
    static Animation<TextureRegion> getAnimation(AnimationKey key) {
        if (!animations.containsKey(key)) {
            loadAnimation(key);
        }

        return animations.get(key);
    }

    private static void loadShaders() {
        flashWhiteShader = new ShaderProgram(Gdx.files.internal("shaders/flash-white-vertex.glsl"),
                Gdx.files.internal("shaders/flash-white-fragment.glsl"));
    }

    /**
     * Load the given animation.
     * @param key The key identifying the animation.
     */
    private static void loadAnimation(AnimationKey key) {
        TextureAtlas atlas = assetManager.get(key.getAtlas(), TextureAtlas.class);
        Array<TextureAtlas.AtlasRegion> frames = atlas.findRegions(key.getAnimationName());

        for (TextureAtlas.AtlasRegion frame : frames) {
            frame.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        Animation<TextureRegion> animation = new Animation<TextureRegion>(0.25f, frames, Animation.PlayMode.LOOP);
        animations.put(key, animation);
    }

    static Vector2 getTextureSize(Map<SpriteState, Map<Direction, Animation<TextureRegion>>> map) {
        TextureRegion frame = map.get(SpriteState.IDLE).get(Direction.DOWN).getKeyFrame(0);
        return new Vector2(frame.getRegionWidth(), frame.getRegionHeight());
    }

    private static Animation<TextureRegion> loadPlayerAnimation(String direction) {
        return new Animation<TextureRegion>(0.25f,
                spriteAtlas.findRegions("player" + direction), Animation.PlayMode.LOOP);
    }

    private static Animation<TextureRegion> loadFastPlayerAnimation(String direction) {
        return new Animation<TextureRegion>(0.18f, spriteAtlas.findRegions("player" + direction), Animation.PlayMode.LOOP);
    }

    private static Animation<TextureRegion> loadPlayerIdleAnimation(String direction) {
        return loadPlayerAnimation(direction + "-idle");
    }

    private static void loadPlayerAnimations() {
        Map<Direction, Animation<TextureRegion>> playerMovingAnimations =
                new HashMap<Direction, Animation<TextureRegion>>();
        playerMovingAnimations.put(Direction.DOWN, loadPlayerAnimation("Front"));
        playerMovingAnimations.put(Direction.DOWN_LEFT, loadPlayerAnimation("Front"));
        playerMovingAnimations.put(Direction.DOWN_RIGHT, loadPlayerAnimation("Front"));
        playerMovingAnimations.put(Direction.UP, loadPlayerAnimation("Back"));
        playerMovingAnimations.put(Direction.UP_LEFT, loadPlayerAnimation("Back"));
        playerMovingAnimations.put(Direction.UP_RIGHT, loadPlayerAnimation("Back"));
        playerMovingAnimations.put(Direction.LEFT, loadFastPlayerAnimation("Left"));
        playerMovingAnimations.put(Direction.RIGHT, loadFastPlayerAnimation("Right"));
        playerAnimations.put(SpriteState.MOVING, playerMovingAnimations);

        Map<Direction, Animation<TextureRegion>> playerIdleAnimations =
                new HashMap<Direction, Animation<TextureRegion>>();
        playerIdleAnimations.put(Direction.DOWN, loadPlayerIdleAnimation("Front"));
        playerIdleAnimations.put(Direction.DOWN_LEFT, loadPlayerIdleAnimation("Front"));
        playerIdleAnimations.put(Direction.DOWN_RIGHT, loadPlayerIdleAnimation("Front"));
        playerIdleAnimations.put(Direction.UP, loadPlayerIdleAnimation("Back"));
        playerIdleAnimations.put(Direction.UP_LEFT, loadPlayerIdleAnimation("Back"));
        playerIdleAnimations.put(Direction.UP_RIGHT, loadPlayerIdleAnimation("Back"));
        playerIdleAnimations.put(Direction.LEFT, loadPlayerIdleAnimation("Left"));
        playerIdleAnimations.put(Direction.RIGHT, loadPlayerIdleAnimation("Right"));
        playerAnimations.put(SpriteState.IDLE, playerIdleAnimations);
    }

    private static Animation<TextureRegion> loadSlimeAnimation(String name) {
        return new Animation<TextureRegion>(0.25f, slimeAtlas.findRegions(name),
                Animation.PlayMode.LOOP);
    }

    private static Animation<TextureRegion> loadSlimeAnimationFlipped(String name) {
        Array<TextureAtlas.AtlasRegion> frames = slimeAtlas.findRegions(name);
        for (TextureAtlas.AtlasRegion frame : frames) {
            frame.flip(true, false);
        }

        return new Animation<TextureRegion>(0.25f, frames, Animation.PlayMode.LOOP);
    }

    private static void loadSlimeAnimations() {
        Map<Direction, Animation<TextureRegion>> slimeMovingAnimations = new HashMap<Direction, Animation<TextureRegion>>();
        slimeMovingAnimations.put(Direction.LEFT, loadSlimeAnimation("slime"));
        slimeMovingAnimations.put(Direction.RIGHT, loadSlimeAnimationFlipped("slime"));
        slimeAnimations.put(SpriteState.MOVING, slimeMovingAnimations);

        Map<Direction, Animation<TextureRegion>> slimeAttackAnimations = new HashMap<Direction, Animation<TextureRegion>>();
        slimeAttackAnimations.put(Direction.LEFT, loadSlimeAnimation("slimeAttack"));
        slimeAttackAnimations.put(Direction.RIGHT, loadSlimeAnimationFlipped("slimeAttack"));
        slimeAnimations.put(SpriteState.ATTACKING, slimeAttackAnimations);

        Map<Direction, Animation<TextureRegion>> slimeHurtAnimations = new HashMap<Direction, Animation<TextureRegion>>();
        slimeHurtAnimations.put(Direction.LEFT, loadSlimeAnimation("slime-death"));
        slimeHurtAnimations.put(Direction.RIGHT, loadSlimeAnimationFlipped("slime-death"));
        slimeAnimations.put(SpriteState.HURT, slimeHurtAnimations);
    }

    private static Animation<TextureRegion> loadStrongSlimeAnimation(String name) {
        return new Animation<TextureRegion>(0.25f, strongSlimeAtlas.findRegions(name),
                Animation.PlayMode.LOOP);
    }

    private static Animation<TextureRegion> loadStrongSlimeAnimationFlipped(String name) {
        Array<TextureAtlas.AtlasRegion> frames = strongSlimeAtlas.findRegions(name);
        for (TextureAtlas.AtlasRegion frame : frames) {
            frame.flip(true, false);
        }

        return new Animation<TextureRegion>(0.25f, frames, Animation.PlayMode.LOOP);
    }

    private static void loadStrongSlimeAnimations() {
        Map<Direction, Animation<TextureRegion>> strongSlimeMovingAnimations = new HashMap<Direction, Animation<TextureRegion>>();
        strongSlimeMovingAnimations.put(Direction.LEFT, loadStrongSlimeAnimation("strongSlime"));
        strongSlimeMovingAnimations.put(Direction.RIGHT, loadStrongSlimeAnimationFlipped("strongSlime"));
        strongSlimeAnimations.put(SpriteState.MOVING, strongSlimeMovingAnimations);

        Map<Direction, Animation<TextureRegion>> strongSlimeAttackingAnimations = new HashMap<Direction, Animation<TextureRegion>>();
        strongSlimeAttackingAnimations.put(Direction.LEFT, loadStrongSlimeAnimation("strongSlimeAttack"));
        strongSlimeAttackingAnimations.put(Direction.RIGHT, loadStrongSlimeAnimationFlipped("strongSlimeAttack"));
        strongSlimeAnimations.put(SpriteState.ATTACKING, strongSlimeAttackingAnimations);

        Map<Direction, Animation<TextureRegion>> strongSlimeHurtAnimations = new HashMap<Direction, Animation<TextureRegion>>();
        strongSlimeHurtAnimations.put(Direction.LEFT, loadStrongSlimeAnimation("strongSlime-death"));
        strongSlimeHurtAnimations.put(Direction.RIGHT, loadStrongSlimeAnimationFlipped("strongSlime-death"));
        strongSlimeAnimations.put(SpriteState.HURT, strongSlimeHurtAnimations);
    }

    private static Animation<TextureRegion> loadBossSlimeAnimation(String name) {
        return new Animation<TextureRegion>(0.25f, bossSlimeAtlas.findRegions(name),
                Animation.PlayMode.LOOP);
    }

    private static Animation<TextureRegion> loadBossSlimeAnimationFlipped(String name) {
        Array<TextureAtlas.AtlasRegion> frames = bossSlimeAtlas.findRegions(name);
        for (TextureAtlas.AtlasRegion frame : frames) {
            frame.flip(true, false);
        }

        return new Animation<TextureRegion>(0.25f, frames, Animation.PlayMode.LOOP);
    }

    private static void loadBossSlimeAnimations() {
        Map<Direction, Animation<TextureRegion>> bossSlimeMovingAnimations = new HashMap<Direction, Animation<TextureRegion>>();
        bossSlimeMovingAnimations.put(Direction.LEFT, loadBossSlimeAnimation("bossSlime"));
        bossSlimeMovingAnimations.put(Direction.RIGHT, loadBossSlimeAnimationFlipped("bossSlime"));
        bossSlimeAnimations.put(SpriteState.MOVING, bossSlimeMovingAnimations);

        Map<Direction, Animation<TextureRegion>> bossSlimeAttackingAnimations = new HashMap<Direction, Animation<TextureRegion>>();
        bossSlimeAttackingAnimations.put(Direction.LEFT, loadBossSlimeAnimation("bossSlimeAttack"));
        bossSlimeAttackingAnimations.put(Direction.RIGHT, loadBossSlimeAnimationFlipped("bossSlimeAttack"));
        bossSlimeAnimations.put(SpriteState.ATTACKING, bossSlimeAttackingAnimations);

        Map<Direction, Animation<TextureRegion>> bossSlimeHurtAnimations = new HashMap<Direction, Animation<TextureRegion>>();
        bossSlimeHurtAnimations.put(Direction.LEFT, loadBossSlimeAnimation("bossSlime-hurt"));
        bossSlimeHurtAnimations.put(Direction.RIGHT, loadBossSlimeAnimationFlipped("bossSlime-hurt"));
        bossSlimeAnimations.put(SpriteState.HURT, bossSlimeHurtAnimations);
    }
}