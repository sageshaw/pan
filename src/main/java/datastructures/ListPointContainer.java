package datastructures;

/**
 * Defines a basic list container for points (Triple).
 */

public interface ListPointContainer<T extends Triple> extends OperablePointContainer {


    /**
     * Adds passed Triple to point list.
     *
     * @param pt Triple to add to list.
     */
    void add(T pt);

    /**
     * Removes Triple point from provided int index.
     * @param i Index in point list for point to be removed.
     * @return Triple removed from list.
     */
    T remove(int i);

    /**
     * Removes point from passed duplicate Triple point (does not need same reference).
     * @param pt Duplicate triple passed to remove in list.
     * @return True if removal successful.
     */
    boolean remove(T pt);

    /**
     * Returns Triple at index i.
     * @param i index
     * @return Triple point.
     */
    T get(int i);


}
