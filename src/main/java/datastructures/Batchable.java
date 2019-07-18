package datastructures;

public interface Batchable {

    void setBatchKey(String key);

    String getBatchKey();

    boolean isBatched();

    void removeFromBatch();

}
