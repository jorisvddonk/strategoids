/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */


package strategoids.ships.AI;
import strategoids.ships.Ship;

/**
 * Strategoids Ship AI Core interface.
 * Ship AI Cores are objects that define the way a ship reacts to its orders.
 * Furthermore, a Ship AI Core controls the ship's weapons, thrusters and other
 * components. * 
 */
public interface IShipAICore {
    public void ai_tick(Ship ship);
}
