package com.upa.gun;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.upa.gun.enemy.UnrecognizedHitboxTypeException;

public class CrateTop extends Entity {

    private Hitboxes hitbox;

    public float x;
    public float y;

    Sprite crateTopSprite;

    CrateTop(float x, float y) {

        super(x, y, 64, 64, 0, -27); //width and height are dimensions of Assets.crateTop, offset is height of Assets.crateSide

        crateTopSprite = new Sprite(Assets.crateTop);
        crateTopSprite.setScale(1);

        this.x = x;
        this.y = y;

        try {
            createHitbox("rectangular", 64, 64);
        } catch(UnrecognizedHitboxTypeException e) {
            //do nothing I guess
        }
    }

    public Hitboxes getHitbox() { return hitbox; }

    //will need update
    private void createHitbox(String hitboxType, int width, int height) throws UnrecognizedHitboxTypeException {
        if (hitboxType.equals("rectangular")) {
            hitbox = new Hitboxes();
        } else {
            throw new UnrecognizedHitboxTypeException(hitboxType);
        }
    }

}
