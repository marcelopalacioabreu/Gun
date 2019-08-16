package com.upa.gun.enemy;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.upa.gun.*;
import com.upa.gun.cutscene.BossSlimeEntrance;

import static com.upa.gun.Settings.SPAWN_CAP;
import static com.upa.gun.Settings.SPAWN_CAP_LIMIT;

public class Spawner {
    World world;
    public int slimesKilled;
    public int slimesKilledSinceLastBoss;

    public static int purpleSlimesKilled;
    public static int greenSlimesKilled;

    boolean bossAlive;

    public int slimesSpawned;

    int bossThreshold;
    int bossHealth;

    public Spawner(World world) {
        this.world = world;

        slimesKilled = 0;
        slimesKilledSinceLastBoss = 0;
        purpleSlimesKilled = 0;
        greenSlimesKilled = 0;

        bossAlive = false;

        bossThreshold = 5;

        bossHealth = 30;

        //slimeFactory = new EnemyFactory(new Slime(0, 0));
        //strongSlimeFactory = new EnemyFactory(new StrongSlime(0, 0));
    }

    /**
     * Reset this spawner to the default state, as if it were newly constructed.
     */
    public void reset() {
        slimesKilled = 0;
        slimesKilledSinceLastBoss = 0;

        bossAlive = false;
        bossThreshold = 5;
        bossHealth = 30;
    }

    public int calculateScore() {
        return purpleSlimesKilled + 2 * greenSlimesKilled;
    }

    public void spawnSlime(int id) {
        if(!SPAWN_CAP || slimesSpawned < SPAWN_CAP_LIMIT) {
            float spawnX = (((float)Math.random() * (Assets.floor.getWidth() - 128)) + (Settings.RESOLUTION.x/2 - Assets.floor.getWidth()/2) + 64);
            float spawnY = (((float)Math.random() * (Assets.floor.getHeight() - 128)) + (Settings.RESOLUTION.y/2 - Assets.floor.getHeight()/2) + 64);
            World.indicators.add(new SpawnIndicator(spawnX, spawnY, 0f, 1f, world.getEnemyFactory(), id));
            slimesSpawned++;
        }
    }

    public void spawnBossSlime() {
        TextureRegion bossSlimeHurt = Assets.bossSlimeAnimations.get(SpriteState.HURT).get(Direction.LEFT).getKeyFrame(0);
        float spawnX = ((Settings.RESOLUTION.x - (float)bossSlimeHurt.getRegionWidth()) / 2f);
        float spawnY = Settings.RESOLUTION.y;

        Enemy boss = world.getEnemyFactory().createEnemy(2, new Vector2(spawnX, spawnY));
        World.enemies.add(boss);

        BossSlimeEntrance entrance = new BossSlimeEntrance(boss);
        entrance.start();
        World.sequences.add(entrance);
    }
}
