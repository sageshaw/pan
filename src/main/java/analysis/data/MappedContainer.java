package analysis.data;

public interface MappedContainer {

    /*
    For Point Containers that use a Guava BiMap to store their data

     */

    //TODO: check if this interface is implemented elsewhere

    String key(PointContainer value);

    String[] keys();

    PointContainer get(String name);

    PointContainer remove(String name);

    boolean remove(PointContainer value);

    void add(String name, PointContainer container);


}
