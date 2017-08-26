package containers;

public abstract class TripleContainer implements Iterable {
  String name;

  abstract Triple getMin();

  public String getName() {
    return name;
  }

  public abstract void add(Object element);

  abstract void makeRelative();

  abstract void translate(int xOffset, int yOffset, int zOffset);
}
