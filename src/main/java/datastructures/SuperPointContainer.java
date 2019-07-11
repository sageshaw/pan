package datastructures;

public interface SuperPointContainer<T extends PointContainer> extends Iterable{

    /**
     *  For containers that hold other point containers (Pointcontainers that contain triples).
     */


    String key(T value);

    String[] keys();

    T get(String name);

    T remove(String name);

    boolean remove(T value);

    void add(String name, T container);

    int getSize();


}
