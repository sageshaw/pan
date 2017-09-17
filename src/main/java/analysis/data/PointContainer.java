package analysis.data;

public interface PointContainer<T extends PointContainer> extends Iterable {

    void translate(int xOffset, int yOffset, int zOffset);

    int getSize();

    void makeRelative();
}
