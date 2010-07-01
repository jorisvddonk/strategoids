/*
 * This file is part of the Strategoids Real-Time-Strategy game/engine project
 *  and is licensed under the GPLv3.
 * See 'Strategoids.java' for more info.
 */
package strategoids.ships.components;

/**
 * Component interface.
 * Components can be 'processed'.
 */
public interface IComponent {

    public enum ComponentType {

        builder
    };

    public ComponentType getComponentType();
    public void process();
}
