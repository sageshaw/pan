package analysis.data;

public interface MappedPointContainer<T extends PointContainer> extends PointContainer {

    /*
    For Point Containers that use a Guava BiMap to store their data

     */

    String key(T value);

    String[] keys();

    T get(String name);

    T remove(String name);

    boolean remove(T value);

    void add(String name, T container);


}
