package com.upa.gun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

public abstract class Bullet extends Entity {
    double angle;

    boolean markedForDeletion;

    Bullet(float x, float y, double angle, TextureRegion texture) {
        super(x, y);
        this.angle = angle;

        bulletSprite = new Sprite(texture);
        bulletSprite.setRotation((float) (angle * 180 / Math.PI));
        bulletSprite.setScale(1f/Settings.PPM);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setTransform(x, y, (float) angle);

        PolygonShape bulletBox = new PolygonShape();
        bulletBox.setAsBox(bulletSprite.getWidth()/2/Settings.PPM, bulletSprite.getHeight()/2/Settings.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = bulletBox;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);

        markedForDeletion = false;
    }

    public void update(float delta) {

        int bulletX = (int)body.getTransform().getPosition().x;
        int bulletY = (int)body.getTransform().getPosition().y;

        if(bulletX < 0 || bulletX > 1280f/Settings.PPM || bulletY < 0 || bulletY > 800f/Settings.PPM) {
            markedForDeletion = true;
        }

    }
}