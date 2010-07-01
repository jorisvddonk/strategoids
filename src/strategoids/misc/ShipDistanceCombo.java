/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.misc;

import strategoids.ships.Ship;

/**
 * Combines a ship and a distance.
 * Used for determining the closest ship from one other ship.
 */
public class ShipDistanceCombo implements Comparable {
    public Ship ship;
    public Double distance;

    public ShipDistanceCombo(Ship ship, Double distance) {
        this.ship = ship;
        this.distance = distance;
    }

    public int compareTo(Object o) {
        ShipDistanceCombo s = (ShipDistanceCombo) o;
        if (s.distance < this.distance) {
            return 1;
        } else if (s.distance > this.distance) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Distance:" + distance + "    Ship:" + ship;
    }


}
