package structs;

public abstract class PointContainer implements Iterable {

  public PointContainer(String name) {
    this.name = name;
  }


  protected String name;

  //TODO: clarify responsibilities of PointContainer and OperableOperableContainer

  public String getName() {
    return name;
  }

  public abstract void add(Object element);

  abstract void makeRelative();

  abstract void translate(int xOffset, int yOffset, int zOffset);


}
