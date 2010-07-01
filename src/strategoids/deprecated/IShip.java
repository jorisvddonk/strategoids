/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */

package strategoids.ships;

import java.util.ArrayList;
import java.util.LinkedList;
import javax.media.opengl.GL;
import strategoids.drawing.PointText;
import strategoids.drawing.LineObject;
import strategoids.drawing.Point;
import strategoids.factions.Faction;
import strategoids.ships.AI.Order;
import strategoids.ships.AI.Sector;
import strategoids.ships.components.IComponent;
import strategoids.ships.components.Turret;

/**
 * Interface for a Ship.
 * This is really mostly deprecated, as in the future, a 'ship' can have various
 * 'components' which signify what that ship can do, and ships will most likely
 * have a dynamically-loadable AI core. Sometimes, though, I use this interface
 * myself to test new variations of ships against eachother without having to
 * muck about in the source codes too much... :)
 */
public interface IShip {
    public void draw(GL gl);
    public void movement_tick();
    public void ai_tick();

    public Faction getFaction();

    public Order getOrder();
    public ArrayList<Order> getOrders();
    public boolean orderAble();
    public void order_fulfill();

    public void orderAdd(Order order);
    public void orderSet(Order order);

    public void order_move(IShip othership);
    public void order_move(double x, double y);
    public void order_attackShip(IShip othership);
    public void order_attackMove(double x, double y);
    public void order_lookAt(double x, double y);
    public void order_lookTo(double angle_in_degs);
    public void orders_clear();
    public PointText getPointText();
    public LineObject getLineObject();

    public double getX();
    public double getY();
    public double getRot();
    public void movX(double x);
    public void movY(double y);

    public int getHitPoints();
    public void subtractHitPoints(int hp);

    public Point getReturnPoint();
    public void setReturnPoint(Point returnpoint);

    public Sector getSector();
    public void setSector(Sector s);

    public ArrayList<Turret> getTurrets();
    public boolean isCarrier();

    public LinkedList<IComponent> getComponents();
}
