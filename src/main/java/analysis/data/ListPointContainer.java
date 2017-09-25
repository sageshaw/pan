package analysis.data;

/**
 * Defines a basic container for points (Triple)
 */

public interface ListPointContainer<T extends PointContainer> extends OperablePointContainer {


    void add(T pt);

    T remove(int i);

    boolean remove(T pt);

    T get(int i);


}
