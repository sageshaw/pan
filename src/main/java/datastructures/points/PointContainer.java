package datastructures.points;

/**
 * Most basic functional unit in PAN. All point storage structures (Triples included) need to implement this
 * interface.
 *
 * @param <T> Contained object type (another PointContainer, Triple)
 */

public interface PointContainer<T extends PointContainer> extends Iterable {

    int getSize();

    /**
     * Adds passed Triple to point list.
     *
     * @param pt Triple to addChannelSet to list.
     */
    void add(Triple pt);

    /**
     * Removes point from passed duplicate Triple point (does not need same reference).
     * @param pt Duplicate triple passed to removeChannelSet in list.
     * @return True if removal successful.
     */
    boolean remove(Triple pt);

    Triple[] getPoints();
}
