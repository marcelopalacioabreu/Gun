package com.upa.gun;

import com.badlogic.gdx.math.Vector2;

public class Entity implements Updatable {
    Vector2 position;
    Vector2 velocity;
    Entity(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0f, 0f);
    }

    public void setVelocity(float x, float y) {
        velocity.x = x;
        velocity.y = y;
    }

    public void setVelocity(Vector2 velocity) {
        setVelocity(velocity.x, velocity.y);
    }

    @Override
    public void update(float delta) {
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
    }
}
