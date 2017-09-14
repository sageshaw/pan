package analysis.data;

/**
 * Defines a basic container for points (Triple)
 */

public interface PointContainer extends Iterable {


  void add(Object element);

  void makeRelative();

  void translate(int xOffset, int yOffset, int zOffset);


}
