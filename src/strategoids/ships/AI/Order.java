/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.ships.AI;

import strategoids.drawing.Point;

/**
 * An order.
 * Has a position (either a ship or a coordinate) and a type (such as move,
 * attack, attack-move)..
 *
 * Used for ships.
 */
public class Order {
    public enum OrderType{
        move,
        attack,
        attackmove,
        empty};


    public OrderTarget target;
    public OrderType ordertype;
    public boolean persistent;
    public int subscribers = 0;

    public Order(OrderTarget target, OrderType ordertype, boolean persistent) {
        this.target = target;
        this.ordertype = ordertype;
        this.persistent = persistent;
    }

    public Order(OrderTarget target, OrderType ordertype) {
        this.target = target;
        this.ordertype = ordertype;
        this.persistent = false;    //sane default
    }

    public Order() {
        this.target = null;
        this.ordertype = null;
        this.persistent = false; //sane default
    }


    public Point getTargetPosition() {
        return new Point(target.getX(), target.getY());
    }

    public OrderType getOrderType() {
        return ordertype;
    }

    public void subscribe() {
        subscribers++;
    }
    public void unsubscribe() {
        subscribers--;
    }

    @Override
    public String toString() {
        return "Order:" + ordertype + "  subscribers:" + subscribers + "  persistent:" + persistent + "  target:" + target;
    }




}
