package containers;

public abstract class TripleContainer implements Iterable {

  public TripleContainer(String name) {
    this.name = name;
  }

  String name;

  abstract Triple getMin();

  public String getName() {
    return name;
  }

  public abstract void add(Object element);

  abstract void makeRelative();

  abstract void translate(int xOffset, int yOffset, int zOffset);

  public abstract double[] nearestNeighborAnalysis();
}
