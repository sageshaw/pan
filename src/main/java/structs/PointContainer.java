package structs;

public interface PointContainer extends Iterable {


  void add(Object element);

  void makeRelative();

  void translate(int xOffset, int yOffset, int zOffset);


}
