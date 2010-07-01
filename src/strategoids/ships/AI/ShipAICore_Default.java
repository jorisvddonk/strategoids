/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.ships.AI;

import strategoids.Main;
import strategoids.misc.MyMath;
import strategoids.ships.AI.Order.OrderType;
import strategoids.ships.Ship;
import strategoids.ships.bullets.IBullet.BulletType;
import strategoids.ships.bullets.Missile;
import strategoids.ships.bullets.Shell;
import strategoids.ships.components.IComponent;
import strategoids.ships.components.Turret;

/**
 * The default Ship AI Core.
 * The "Messy One".. ;)
 */
public class ShipAICore_Default implements IShipAICore{

    /**
     * Abandon all hope, ye who enter here! I (megagun) am not an AI coder, and
     * thus this function is pretty ugly. Furthermore, this code is VERY 'hacky'
     * ... Indeed, it is a giant mess of hacks. I apologize. :(
     *
     * Basically, the ai_tick does all the 'decision making' for the ship.
     * Turns the ship's order into actual actions for the ship to do (turn left,
     * thrust, turn right, shoot, etc).
     *
     * Keep in mind that this function does not make use of 'states'. This means
     * that the ai_tick function does NOT store ANY data inside the ship!
     * In fact, the ai_tick function (for every tick) determines the state of
     * the ship, and based on that, it decides what to do. I don't know if this
     * is either ugly (probably) or brilliant (maybe :P), but fact is that part
     * of the reason why this code is so messy is because of the fact that it
     * is stateless. Don't attempt to comprehend what this function does, it's
     * useless (I don't even know anymore myself).
     *
     * AGAIN, THIS FUNCTION IS VERY VERY VERY UGLY AND MIGHT NEED TO BE
     * COMPLETELY REWRITTEN!!!! HELL, IT PROBABLY SHOULD!!!
     * FURTHERMORE, THIS FUNCTION SHOULD PROBABLY BE PUT INTO AN 'AI_CORE'
     * OBJECT. The ai_core object can then be rewritten for different types of
     * ships. For instance, a missile carrier should behave differently from
     * a normal fighter. Currently, that 'case distinction' is hard-coded in
     * the ai_tick function, which is ugly..
     *
     * As I (megagun) am not an astrophysicist, I have no idea if my way of
     * making a ship move around the universe is proper or not. Either way,
     * this is how the ship 'behaves':
     * 1) Make sure that the movement direction is more-or-less the same as
     *      the direction of the target.
     * 2) Accelerate until at the 'turnaround point' where the ship has to
     *      decelerate.
     * 3) Decelerate.
     * 4) Stop (hax! we're using some cheap way to set the movement to 0. :P)
     *
     * Currently, all ships have retrothrusters, and thus don't have to make a
     * physical turn. This is bad. Some ships (especially huge ones) probably
     * should not contain retrothrusters, and thus need a new step between
     * 2 and 3, where it physically rotates 180 degrees. Again, a seperate AI
     * core object can make this all work out nicely.
     */
    public void ai_tick(Ship ship) {
        for (IComponent c : ship.components) {
            c.process();
        }

        if (!ship.disabled) {
            //System.out.println("---");
            try {
                if (ship.getOrder() != null && ship.getOrder().ordertype != Order.OrderType.empty) {
                    if (ship.getOrder().ordertype == Order.OrderType.move && ship.getOrder().target.isship) {
                        if (Main.strategoids.factions_isMine(ship.getOrder().target.targetship.getFaction()) && ship.getOrder().target.targetship.isCarrier() && ship.distance_ship_x_target < 3) {
                            //order is a friendly ship that can carry ships and distance is low!
                            Ship carriership = (Ship) ship.getOrder().target.targetship;
                            ship.order_fulfill(); //TODO: see what we should do here? Maybe we should not clear the orders, hmm...
                            //orders_clear();
                            carriership.carrier_shipscarrying.add(ship);
                            Main.strategoids.game.removeShipfromSector(ship, ship.sector);
                            ship.disabled = true;
                            System.out.println("Carrier ship carrying " + carriership.carrier_shipscarrying.size() + " ships now");
                        }
                    }
                }
                if (ship.getOrder() != null && ship.getOrder().ordertype != Order.OrderType.empty) {

                    if (ship.distance_ship_x_target < Main.strategoids.SHIP_WANTEDDISTANCE * 5) {
                        if (ship.orderqueue.size() > 1 && (ship.getOrder().ordertype == Order.OrderType.move || ship.getOrder().ordertype == Order.OrderType.attackmove)) { //If we have a queue and a 'move' order, AND we're close to our target, consider order as fulfilled and go to the next order in the queue
                            ship.order_fulfill();
                        }
                    }
                    if (ship.getOrder().ordertype == Order.OrderType.attack && (ship.distance_ship_x_target < 100 || (ship.bulletType == BulletType.missile && ship.distance_ship_x_target < 600))) {
                        if (ship.fire_recharge <= 0) {
                            if (ship.turrets.size() == 0) {
                                if (ship.bulletType == BulletType.laser) {
                                    if (ship.angle_rot_diff < 1) {
                                        Main.strategoids.game.bullets.add(new Shell(ship.xpos, ship.ypos, ship.rot, 4, 25, ship.faction, 4, false));
                                        ship.fire_recharge = ship.fire_rate;
                                    }
                                } else if (ship.bulletType == BulletType.missile) {
                                    if (ship.angle_rot_diff < 45) {
                                        Main.strategoids.game.bullets.add(new Missile(ship.xpos, ship.ypos, 0.4, ship.rot, 120, 500, ship.faction, 0.01d, ship.getOrder().target));
                                        Main.strategoids.game.bullets.add(new Missile(ship.xpos, ship.ypos, 0.4, ship.rot, -120, 500, ship.faction, 0.01d, ship.getOrder().target));
                                        ship.fire_recharge = ship.fire_rate;
                                    }
                                }
                            }
                        }
                        for (Turret t : ship.turrets) {
                            t.shoot(ship.faction, ship.xpos, ship.ypos, ship.rot, ship.getOrder().target);
                        }
                    }
                    if (ship.distance_ship_x_target < (Main.strategoids.SHIP_WANTEDDISTANCE + 2) && ship.speed_ship > 0 && ship.getOrder().ordertype != OrderType.attack) {
                        if (ship.speed_ship > (ship.thruster_force) && ship.speed_ship < (ship.thruster_force * 35)) {
                            //System.out.println(speed/ship.thruster_force + "    ---       " + mang);
                            ship.movx *= 0.9; //HAX
                            ship.movy *= 0.9; //HAX
                        }
                    }
                    if (ship.distance_ship_x_target < (Main.strategoids.SHIP_WANTEDDISTANCE + 2) && ship.speed_ship < (ship.thruster_force * 10) && (ship.getOrder().ordertype == OrderType.move || ship.getOrder().ordertype == OrderType.attackmove)) {
                        ship.movx = 0; //HAX
                        ship.movy = 0; //HAX
                        if (!ship.getOrder().target.isShip()) {
                            ship.order_fulfill();
                        }
                    } else {
                        int ti = MyMath.ClosestDegDirection(ship.rot, ship.angle_target);
                        //if ((mang > 1 && distance > (Fortress.SHIP_WANTEDDISTANCE) && ((displacement + (Fortress.SHIP_WANTEDDISTANCE)) < distance)) && distance > 2 * Fortress.SHIP_WANTEDDISTANCE) {
                        if ((ship.delta_angle_movement > 1 && ship.distance_ship_x_target > (Main.strategoids.SHIP_WANTEDDISTANCE) && ((ship.displacement + (Main.strategoids.SHIP_WANTEDDISTANCE)) < ship.distance_ship_x_target)) || (ship.delta_angle_movement > 90 && ship.speed_ship != 0) && ship.distance_ship_x_target > 2 * Main.strategoids.SHIP_WANTEDDISTANCE) {
                            //System.out.println(mang);
                            int mti = MyMath.ClosestDegDirection(ship.movementangle_ship, ship.angle_target);
                            //System.out.println(System.currentTimeMillis());

                            if (mti == -1) {
                                if ((ship.angle_rot_diff < 45 && ti == 1) || ti == -1) {
                                    ship.turn_left();
                                    //System.out.println("herp");
                                }
                                if (ti == 1 || ship.angle_rot_diff < 0.5 || (ship.angle_rot_diff < 10 && ship.distance_ship_x_target > 2 * Main.strategoids.SHIP_WANTEDDISTANCE)) {
                                    if (ship.angle_rot_diff < 140) {
                                        //System.out.println("ti:" + ti + "  ang:" + ang + "  dist:" + distance + "  mang:" + mang);
                                        ship.thrust_forward();
                                    } else {
                                        ship.turn_left();
                                    }

                                    //System.out.println("foo");
                                }
                            }
                            if (mti == 1) {
                                if ((ship.angle_rot_diff < 45 && ti == -1) || ti == 1) {
                                    ship.turn_right();
                                }
                                if (ti == -1 || ship.angle_rot_diff < 0.5 || (ship.angle_rot_diff < 10 && ship.distance_ship_x_target > 2 * Main.strategoids.SHIP_WANTEDDISTANCE)) {
                                    if (ship.angle_rot_diff < 140) {
                                        //System.out.println("ti:" + ti + "  ang:" + ang + "  dist:" + distance + "  mang:" + mang);
                                        ship.thrust_forward();
                                    } else {
                                        ship.turn_right();
                                    }
                                    //System.out.println("foo");
                                }
                            }
                            if (mti == 0) {
                                System.out.println("HERPIE DERPIE DERP");
                            }
                        } else {
                            //How many ticks do I need to be at speed 0 again?
                            //What will be the distance between me and the target if I keep decelerating for all those ticks?
                            //Greater than 0? accelerate. Otherwise decelerate.

                            //   v = a * t
                            //   t = v / a
                            //   s = ((begin velocity + end velocity)*t)/2
                            if (ship.angle_rot_diff < 5) {
                                //System.out.println("Ticks: " + ticks + "     Displacement: " + displacement + "     Distance: " + distance + "     Speed: " + speed);
                                if ((ship.displacement + (Main.strategoids.SHIP_WANTEDDISTANCE)) < ship.distance_ship_x_target) {
                                    ship.thrust_forward();
                                    //System.out.println("+Speed: " + speed + "   thr: " + ship.thruster_force);
                                    if (ti == -1) {
                                        ship.turn_left(ship.angle_rot_diff);
                                    } else if (ti == 1) {
                                        ship.turn_right(ship.angle_rot_diff);
                                    }
                                } else {
                                    //System.out.println("-");

                                    ship.thrust_backward();
                                    //System.out.println("-Speed: " + speed + "   thr: " + ship.thruster_force);
                                    if (ti == -1) {
                                        ship.turn_left(ship.angle_rot_diff);
                                    } else if (ti == 1) {
                                        ship.turn_right(ship.angle_rot_diff);
                                    }
                                }
                            } else {
                                //System.out.println("foo");
                                if (ti == -1) {
                                    ship.turn_left();
                                } else if (ti == 1) {
                                    ship.turn_right();
                                }
                            }
                        }
                    }
                } else { // !if (ship.getOrder() != null && ship.getOrder().ordertype != Order.OrderType.empty) {
                    //Get to a halt
                    //double speed = Point2D.distance(0, 0, ship.movx, ship.movy);
                    if (ship.speed_ship > 0) {
                        if (ship.speed_ship >= (ship.thruster_force * 35)) {
                            //double movangle = MyMath.FixDeg(Math.toDegrees(Math.atan2(ship.movy, ship.movx)));
                            int mti = MyMath.ClosestDegDirection(MyMath.FixDeg(ship.rot + 180), ship.movementangle_ship);
                            //double mang = MyMath.FixDeg(MyMath.ClosestDegAngle(MyMath.FixDeg(rot + 180), movangle));
                            if (mti == -1) {
                                ship.turn_left();
                            } else if (mti == 1) {
                                ship.turn_right();
                            }
                            if (ship.delta_angle_movement < 3) {
                                ship.thrust_forward();
                            }
                        } else {
                            //System.out.println(speed/ship.thruster_force + "    ---       " + mang);
                            ship.movx *= 0.8; //HAX
                            ship.movy *= 0.8; //HAX
                            if (ship.speed_ship < ship.thruster_force) {
                                ship.movx = 0;
                                ship.movy = 0;
                            }
                        }
                    }
                    //ship.movx = 0;
                    //ship.movy = 0;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
