package containers;

import java.util.List;

public interface TripleContainer {
    String name = null;

    Triple getMin();
    String getName();

    List<Triple> getPoints();

    void makeRelative();

    void translate(int xOffset, int yOffset, int zOffset);
}
