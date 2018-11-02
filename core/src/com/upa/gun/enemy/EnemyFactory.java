package com.upa.gun.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;

public class EnemyFactory {
    Map<Integer, EnemyInfo> enemies;

    public EnemyFactory(String path) {
        enemies = new HashMap<Integer, EnemyInfo>();

        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(Gdx.files.internal(path)).get("enemies");

        for (JsonValue enemy : root) {
            int id = enemy.getInt("id");
            int health = enemy.getInt("health");

            String hitboxType = enemy.getString("hitboxType");
            int hitboxWidth = enemy.getInt("hitboxWidth");
            int hitboxHeight = enemy.getInt("hitboxHeight");

            int width = enemy.getInt("width");
            int height = enemy.getInt("height");
            String sprite = enemy.getString("sprite");

            JsonValue rotationVal = enemy.get("rotation");
            AttackRotation rotation = new AttackRotation();
            for (JsonValue attack : rotationVal) {
                String[] attackComponents = attack.asString().split(" ");

                String type = attackComponents[0];
                float length = Float.parseFloat(attackComponents[1]);
                float interval = Float.parseFloat(attackComponents[2]);
                boolean mobile = Boolean.parseBoolean(attackComponents[3]);

                if (type.equals("trackingBurst")) {
                    rotation.attacks.add(new TrackingBurstAttack(length, interval, mobile));
                } else if (type.equals("circular")) {
                    rotation.attacks.add(new CircularAttack(length, interval, mobile));
                } else if (type.equals("none")) {
                    rotation.attacks.add(new NoAttack(length, mobile));
                } else {
                    Gdx.app.debug("EnemyFactory", "Unknown attack type " + type + ", ignoring");
                }
            }

            EnemyInfo info = new EnemyInfo(id, health, hitboxType, hitboxWidth, hitboxHeight, width, height, sprite,
                    rotation);
            enemies.put(id, info);
        }

        Gdx.app.log("EnemyFactory", "Completed loading enemies");
    }

    Enemy createEnemy(int id, int x, int y) {
        return new Enemy(enemies.get(id), x, y);
    }
}