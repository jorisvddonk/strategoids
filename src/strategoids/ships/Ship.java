/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids.ships;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.media.opengl.GL;
import strategoids.Main;
import strategoids.drawing.LineObject;
import strategoids.drawing.Point;
import strategoids.drawing.PointText;
import strategoids.factions.Faction;
import strategoids.misc.MyMath;
import strategoids.ships.AI.IShipAICore;
import strategoids.ships.AI.Order;
import strategoids.ships.AI.Order.OrderType;
import strategoids.ships.AI.OrderTarget;
import strategoids.ships.AI.Sector;
import strategoids.ships.AI.ShipAICore_Default;
import strategoids.ships.bullets.IBullet.BulletType;
import strategoids.ships.bullets.Missile;
import strategoids.ships.bullets.Shell;
import strategoids.ships.components.Builder;
import strategoids.ships.components.IComponent;
import strategoids.ships.components.Thruster;
import strategoids.ships.components.Turret;

/**
 * A ship. One epic complex thing.
 */
public class Ship implements Cloneable {

    public double xpos, ypos;   //X and Y position
    public double rot = 0; //rotation (in degrees)
    public double movx = 0; //movement vector, X component
    public double movy = 0; //movement vector, Y component
    public String shipname; //ship's name ("Fighter 256")
    public String identifier; //ship's identifier ("Fighter")
    public LineObject ship_graphic; //Only used in storage's ship blueprints
                                    //CREATE THIS AGAIN IN CASE YOU MODIFY
                                    //THE SHIP GRAPHIC (for collision's sake)
    public Integer ship_graphic_displayList; //DisplayList integer.
                                             //If you change the ship_graphic
                                             //GET RID OF THIS
    public ArrayList<ArrayList<Thruster>> thrusters; //Thrusters. For graphics
        //Thruster slots: 0: forward. 1: backward. 2: left. 3: right
    public ArrayList<Turret> turrets; //Turrets. pew pew pew!
    public double thruster_force = 0.001; //Thruster force of this ship.
    public double turn_rate = 1; //Turning rate of this ship
    public ArrayList<Order> orderqueue; //Order queue of the ship.
    public double fire_recharge = 0; //If this is <= 0, the ship can fire.
    public double fire_rate = 100; //Fire rate. fire_recharge gets set to this when the ship fires.
    public Faction faction; //What faction the ship belongs to
    public int hitpoints; //The hitpoints remaining of this ship.
    public Point returnpoint = null; //The 'return point' of this ship. Once a ship
        //has gone through all its orders, it'll attackmove to this point.
        //TODO: GET RID OF THIS (MAKE IT A NORMAL ORDER, FOR GOD'S SAKE)
        //(as it only complicates things. ;))
    public Sector sector = null; //The sector this ship is currently in

    //Used for AI:
    public double angle_target; //Angle between ship and target X/Y
    public double angle_rot_diff; //Difference between angle_target and rotation (closest). In other words, the ship needs to turn angle_rot_diff degrees to look at its target.
    public double speed_ship; //Speed of ship
    public double distance_ship_x_target; //Distance between ship and target
    public double movementangle_ship; //Angle ship is moving in (movx and movy vector)
    public double ticks; //Ticks it takes to reach the target.
    public double displacement; //How far a ship can go (based on thrust) with 'ticks' ticks.
    public double delta_angle_movement; //Difference between angle_target and movementangle_ship. In other words, if this is near 0, the ship is moving towards its target.
        //(or rather: the closer this is to 0, the more the ship is moving towards its target)
    
    public BulletType bulletType = BulletType.laser; //Bullet Type the ship fires. Can be 'laser' or 'missile'.
        //TODO: get rid of this, and put a ship's fixed-weapons (non-turrets) into a 'component' array.

    //Carrying abilities are currently slightly buggy:
    public boolean isCarrier; //Is this ship able to carry other ships?
        //TODO: get rid of this too. Place carrying abilities into a component
    public ArrayList<Ship> carrier_shipscarrying; //The ships this ship is carrying.
        //TODO: get rid of this too. Place carrying abilities into a component
    public boolean disabled; //Is this ship disabled?
        //This is used for when a ship is being carried by another. Should probably
        //do this some other way, maybe...

    public LinkedList<IComponent> components; //The components this ship has.
    public IShipAICore shipAICore; //the ship AI core

    /**
     * Create a ship!
     * @param xpos
     * @param ypos
     * @param rot
     * @param shiptype
     * @param faction
     */
    public Ship(double xpos, double ypos, double rot, String shiptype, Faction faction) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.rot = rot;
        this.shipname = shiptype + " " + Main.strategoids.random.nextInt(999);
        this.identifier = shiptype;
        this.ship_graphic = null;
        this.thrusters = new ArrayList<ArrayList<Thruster>>();
        this.thrusters.add(new ArrayList<Thruster>()); //0-forwards
        this.thrusters.add(new ArrayList<Thruster>()); //1-backwards
        this.thrusters.add(new ArrayList<Thruster>()); //2-left
        this.thrusters.add(new ArrayList<Thruster>()); //3-right
        this.orderqueue = new ArrayList<Order>();
        this.faction = faction;
        this.hitpoints = 10;
        this.turrets = new ArrayList<Turret>();
        fix_rotation();
        fix_thrust();
        this.isCarrier = false;
        this.carrier_shipscarrying = new ArrayList<Ship>();
        this.disabled = false;
        this.ship_graphic_displayList = null; //Strategoids.game.storage.getDisplayList_byString(identifier);
        this.components = new LinkedList<IComponent>();
        setShipAICore("default");
    }

    public void setShipAICore(String newCore) {
        this.shipAICore = Main.strategoids.game.storage.shipAICores.get(newCore);
    }

    /*public void draw(GL gl) { //old style without display lists
    if (!disabled) {
    ship_graphic.draw(gl, xpos, ypos, rot);
    for (Turret t : turrets) {
    t.draw(gl, rot);
    }
    }
    }*/

    /**
     * Draw the ship!
     * @param gl
     */
    public void draw(GL gl) {
        gl.glLoadIdentity();
        gl.glTranslated(xpos, ypos, 0);
        gl.glRotated(rot - 90, 0, 0, 1); //+ rotoffset of 90 or -90?
        //Color?
        gl.glColor3d(faction.r, faction.g, faction.b);
        //Strategoids.game.storage.callDisplayList_byString(gl, identifier);
        if (ship_graphic_displayList == null) {
            ship_graphic_displayList = Main.strategoids.game.storage.getDisplayList_byString(identifier);
        }
        if (ship_graphic_displayList != null) {
            Main.strategoids.game.storage.callDisplayList(gl, ship_graphic_displayList);
        }
        for (Turret t : turrets) {
            t.draw(gl, rot);
        }
    }

    /**
     * Process a 'movement' tick.
     * Basically, use physics on the ship! :)
     */
    public void movement_tick() {
        if (!disabled) {
            xpos += movx;
            ypos += movy;
            Main.strategoids.game.fixSector(this);
            if (fire_recharge > 0) {
                fire_recharge--;
            }
            for (Turret t : turrets) {
                t.process();
            }
        }
    }

    /**
     * Order: Ship, move to these coordinates!
     * @param x
     * @param y
     */
    public void order_move(double x, double y) {
        if (orderAble()) {
            Order order = new Order();
            order.target = new OrderTarget(x, y);
            order.ordertype = Order.OrderType.move;
            orderAdd(order);
        }
        setReturnPoint(null);
    }

    /**
     * Order: Ship, attack this ship!
     * @param othership
     */
    public void order_attackShip(Ship othership) {
        if (orderAble()) {
            Order order = new Order();
            order.target = new OrderTarget(othership);
            order.ordertype = Order.OrderType.attack;
            orderAdd(order);
        }
    }

    /**
     * Order: Ship, attackmove to these coordinates!
     * @param x
     * @param y
     */
    public void order_attackMove(double x, double y) {
        if (orderAble()) {
            Order order = new Order();
            order.target = new OrderTarget(x, y);
            order.ordertype = Order.OrderType.attackmove;
            orderAdd(order);
        }
        setReturnPoint(null);
    }

    /**
     * Order: Ship, look at these coordinates!
     * @param x
     * @param y
     */
    public void order_lookAt(double x, double y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Order: Ship, look in this direction!
     * @param angle_in_degs
     */
    public void order_lookTo(double angle_in_degs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get a pointtext object that describes the ship.
     * @return
     */
    public PointText getPointText() {
        return new PointText(xpos, ypos, shipname + " [" + hitpoints + "] " + faction.name);
    }

    /**
     * Get the ship's graphic (lineobject)
     * @return
     */
    public LineObject getLineObject() {
        return ship_graphic;
    }

    /**
     * Turn left with full standard force.
     */
    public void turn_left() {
        turn_left(1);
    }

    /**
     * Turn right with full standard force.
     */
    public void turn_right() {
        turn_right(1);
    }


    /**
     * Turn left with 'multiplier' force
     * @param multiplier With how much you multiply the turning force. can not go above 1.
     */
    public void turn_left(double multiplier) {
        rot += turn_rate * Math.min(multiplier, 1);
        if (Math.min(multiplier, 1) == 1) {
            for (Thruster t : thrusters.get(2)) {
                t.do_effect(xpos, ypos, rot);
            }
        }
        rot = MyMath.FixDeg(rot);
        fix_rotation();
    }

    /**
     * Turn right with 'multiplier' force
     * @param multiplier With how much you multiply the turning force. can not go above 1.
     */
    public void turn_right(double multiplier) {
        rot -= turn_rate * Math.min(multiplier, 1);
        if (Math.min(multiplier, 1) == 1) {
            for (Thruster t : thrusters.get(3)) {
                t.do_effect(xpos, ypos, rot);
            }
        }
        rot = MyMath.FixDeg(rot);
        fix_rotation();
    }

    /**
     * Thrust forwards with full standard thrust.
     */
    public void thrust_forward() {
        thrust_forward(1);
    }

    /**
     * Thrust backwards with full standard thrust.
     */
    public void thrust_backward() {
        thrust_backward(1);
    }

    /**
     * Thrust forwards with 'multiplier' force.
     * @param multiplier With how much you multiply the force. can not go above 1.
     */
    public void thrust_forward(double multiplier) {
        movx += Math.cos(Math.toRadians(rot)) * thruster_force * Math.min(multiplier, 1);
        movy += Math.sin(Math.toRadians(rot)) * thruster_force * Math.min(multiplier, 1);
        for (Thruster t : thrusters.get(0)) {
            t.do_effect(xpos, ypos, rot);
        }
        fix_thrust();
    }


    /**
     * Thrust backwards with 'multiplier' force.
     * @param multiplier With how much you multiply the force. can not go above 1.
     */
    public void thrust_backward(double multiplier) {
        movx -= Math.cos(Math.toRadians(rot)) * thruster_force * Math.min(multiplier, 1);
        movy -= Math.sin(Math.toRadians(rot)) * thruster_force * Math.min(multiplier, 1);
        for (Thruster t : thrusters.get(1)) {
            t.do_effect(xpos, ypos, rot);
        }
        fix_thrust();
    }

    /**
     * Get the X coordinate of the ship
     * @return
     */
    public double getX() {
        return xpos;
    }

    /**
     * Get the Y coordinate of the ship
     * @return
     */
    public double getY() {
        return ypos;
    }

    /**
     * 'fix' (recalculate for AI function) rotation things.
     */
    public void fix_rotation() {
        if (getOrder() != null) {
            angle_target = MyMath.FixDeg(Math.toDegrees(Math.atan2(getOrder().getTargetPosition().y - ypos, getOrder().getTargetPosition().x - xpos)));
            angle_rot_diff = MyMath.FixDeg(MyMath.ClosestDegAngle(rot, angle_target));
            delta_angle_movement = MyMath.FixDeg(MyMath.ClosestDegAngle(movementangle_ship, angle_target));
            fix_common();
        }
    }

    /**
     * 'fix' (recalculate for AI function) thrust things.
     */
    public void fix_thrust() {
        speed_ship = Point2D.distance(0, 0, movx, movy);
        movementangle_ship = MyMath.FixDeg(Math.toDegrees(Math.atan2(movy, movx)));
        ticks = (speed_ship / (thruster_force)); //ZERO?
        displacement = (speed_ship * ticks) / 2;
        if (getOrder() != null) {
            distance_ship_x_target = Point2D.distance(xpos, ypos, getOrder().getTargetPosition().x, getOrder().getTargetPosition().y);
            fix_common();
            delta_angle_movement = MyMath.FixDeg(MyMath.ClosestDegAngle(movementangle_ship, angle_target));
        }
    }

    /**
     * 'fix' common stuffs.
     * This function is currently empty! :D
     */
    public void fix_common() {
    }

    /**
     * 'fix' (recalculate for AI function) thrust and rotation things.
     */
    public void fix_all() {
        fix_rotation();
        fix_thrust();
    }


    public void ai_tick() {
        shipAICore.ai_tick(this);
    }

    public Order getOrder() {
        if (orderqueue.size() > 0) {
            return orderqueue.get(0);
        } else {
            return null;
        }

    }

    /**
     * Add x to movement vector component X
     * @param x
     */
    public void movX(double x) {
        movx += x;
    }

    /**
     * Add y to movement vector component Y
     * @param y
     */
    public void movY(double y) {
        movy += y;
    }

    /**
     * Get rotation of ship (in degs)
     * @return
     */
    public double getRot() {
        return rot;
    }

    /**
     * Clear orders of the ship
     */
    public void orders_clear() {
        for (Order o : orderqueue) { //unsubscribe!!!
            o.unsubscribe();
        }

        if (orderAble()) {
            orderqueue.clear();
            //order.target = new OrderTarget(0,0);
            //order.ordertype = Order.OrderType.empty;
        }

    }

    /**
     * Move the ship towards other ship ("follow")
     * @param othership
     */
    public void order_move(Ship othership) {
        if (orderAble()) {
            Order order = new Order();
            order.target = new OrderTarget(othership);
            order.ordertype = Order.OrderType.move;
            orderqueue.add(order);
        }

    }

    @Override
    /**
     * Tostring. self-explanatory, I hope. :)
     */
    public String toString() {
        return "Ship[" + identifier + "/" + shipname + "] @" + getX() + "|" + getY() + " -- mov@" + movx + "|" + movy + "   Orders: " + orderqueue.size();
    }

    /**
     * Return the order queue of the ship
     * @return
     */
    public ArrayList<Order> getOrders() {
        return orderqueue;
    }

    /**
     * Can you order this ship around?
     * @return
     */
    public boolean orderAble() {
        return true;
    }

    /**
     * This function is called when a ship 'fulfills' its order (for example,
     * when it has moved to its destination or when its target has been
     * annihilated).
     */
    public void order_fulfill() {
        if (orderqueue.size() > 0) {
            if (!orderqueue.get(0).persistent) {
                orderqueue.remove(0);
            } else {
                Order temporder = orderqueue.get(0);
                orderqueue.remove(0);
                orderqueue.add(temporder);
            }
            fix_all();
        }
    }

    /**
     * return the faction of ship
     * @return
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     * return hitpoints of ship
     * @return
     */
    public int getHitPoints() {
        return hitpoints;
    }

    /**
     * remove hp hitpoints from ships HP
     * @param hp
     */
    public void subtractHitPoints(int hp) {
        this.hitpoints -= hp;
    }

    /**
     * return the return point
     * @return
     */
    public Point getReturnPoint() {
        return returnpoint;
    }

    /**
     * set the return point
     * @param returnpoint
     */
    public void setReturnPoint(Point returnpoint) {
        this.returnpoint = returnpoint;
    }

    /**
     * add an order to the ship.
     * @param order
     */
    public void orderAdd(Order order) {
        order.subscribe();
        orderqueue.add(order);
        fix_rotation();

        fix_thrust();

    }

    /**
     * clear all orders and add a new one
     * @param order
     */
    public void orderSet(Order order) {
        orders_clear();
        orderAdd(order);
        fix_rotation();

        fix_thrust();

    }

    /**
     * get the sector this ship is in
     * @return
     */
    public Sector getSector() {
        return sector;
    }

    /**
     * set the sector this ship is in
     * @param s
     */
    public void setSector(Sector s) {
        this.sector = s;
    }

    /**
     * get the turrets
     * @return
     */
    public ArrayList<Turret> getTurrets() {
        return turrets;
    }

    /**
     * is this ship a carrier?
     * @return
     */
    public boolean isCarrier() {
        return isCarrier;
    }

    @Override
    /**
     * clone the ship! (super.clone)
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public LinkedList<IComponent> getComponents() {
        return components;
    }
}
