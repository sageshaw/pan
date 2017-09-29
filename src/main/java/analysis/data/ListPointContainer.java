package analysis.data;

import analysis.Triple;

/**
 * Defines a basic container for points (Triple)
 */

public interface ListPointContainer<T extends Triple> extends OperablePointContainer {


    void add(T pt);

    T remove(int i);

    boolean remove(T pt);

    T get(int i);


}
