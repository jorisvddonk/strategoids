/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.ships.bullets;

import javax.media.opengl.GL;
import strategoids.factions.Faction;
import strategoids.ships.Ship;

/**
 * Bullet interface.
 * A bullet is something that can move and be drawn.
 * plu you can check collisions.
 */
public interface IBullet {
    public enum BulletType{
        laser,
        missile};

        
    public void movement_tick();
    public void process_tick();
    public void draw(GL gl);
    public boolean mustDestroy();
    public boolean checkCollisionWith(Ship ship);
    public void forceDestroy();
    public Faction getFaction();
    public double getX();
    public double getY();
    public boolean isInstaHit();
}
