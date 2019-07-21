package datastructures;

public interface Annotatable {

    void addEntry(String entryName, double val);

    String[] getEntryNames();

    double value(String entryName);

}
