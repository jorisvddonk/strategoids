/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids.ships.AI;

import java.util.LinkedList;
import javax.media.opengl.GL;
import strategoids.Main;
import strategoids.gui.GUILineSquare;
import strategoids.misc.Int2D;
import strategoids.ships.Ship;

/**
 * A Sector.
 * Sectors are used to speed searching of nearby ships (to attack) up.
 * Ships are stored inside Sectors.
 * Sectors have a sector number that is composed of an X and Y component.
 */
public class Sector {

    public LinkedList<Ship> ships;
    public Int2D sector_number;

    public Sector(int sector_x, int sector_y) {
        this.sector_number = new Int2D(sector_x,sector_y);
        ships = new LinkedList<Ship>();
    }

    public Sector(Int2D i) {
        this.sector_number = i;
        ships = new LinkedList<Ship>();
    }

    public void addShip(Ship i) {
        ships.add(i);
    }

    public void remShip(Ship i) {
        ships.remove(i);
    }

    @Override
    public String toString() {
        return "Sector: " + sector_number.toString() + "   Numships in this sector: " + ships.size();
    }

    public void draw(GL gl) {
        GUILineSquare.Render(gl, null,
                (sector_number.int1 < 0? (sector_number.int1*Main.strategoids.SECTORSIZE) : (sector_number.int1*Main.strategoids.SECTORSIZE)),
                (sector_number.int2 < 0? (sector_number.int2*Main.strategoids.SECTORSIZE)  : (sector_number.int2*Main.strategoids.SECTORSIZE)),
                (sector_number.int1 < 0? (sector_number.int1*Main.strategoids.SECTORSIZE) + Main.strategoids.SECTORSIZE : (sector_number.int1*Main.strategoids.SECTORSIZE)+ Main.strategoids.SECTORSIZE),
                (sector_number.int2 < 0? (sector_number.int2*Main.strategoids.SECTORSIZE) + Main.strategoids.SECTORSIZE : (sector_number.int2*Main.strategoids.SECTORSIZE)+ Main.strategoids.SECTORSIZE),
                0,0,1,0.1);
    }

}
