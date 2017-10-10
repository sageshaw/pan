package analysis.data;

public interface MappedPointContainer<T extends PointContainer> extends PointContainer {

    /**
     *  For Point Containers that hold other PointContainers (ex : listPointContainer) not hold
     *  Triples themselves.
     */


    String key(T value);

    String[] keys();

    T get(String name);

    T remove(String name);

    boolean remove(T value);

    void add(String name, T container);


}
