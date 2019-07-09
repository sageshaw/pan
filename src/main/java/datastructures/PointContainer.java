package datastructures;

/**
 * Most basic functional unit in PAN. All point storage structures (Triples included) need to implement this
 * interface.
 *
 * @param <T> Contained object type (another PointContainer, Triple)
 */

public interface PointContainer<T extends PointContainer> extends Iterable {

    void translate(int xOffset, int yOffset, int zOffset);

    int getSize();

    void makeRelative();
}
