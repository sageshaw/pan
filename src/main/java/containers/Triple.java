package containers;

//Because Java doesn't really  have a tuple construct (this one is a triple)
public class Triple {

  //coordinate fields
  private int xCoord;
  private int yCoord;
  private int zCoord;

  //assigned on creation, fields should never change (hence final)
  public Triple(int x, int y, int z) {

    xCoord = x;
    yCoord = y;
    zCoord = z;
  }

  //Some getters
  public int getX() {
    return xCoord;
  }

  public void setX(int xCoord) {
    this.xCoord = xCoord;
  }

  public int getY() {
    return yCoord;
  }

  //Some setters (use sparingly, only if you know what you're doing)

  public void setY(int yCoord) {
    this.yCoord = yCoord;
  }

  public int getZ() {
    return zCoord;
  }

  public void setZ(int zCoord) {
    this.zCoord = zCoord;
  }

  //basic Object method override
  @Override
  public String toString() {
    return "(" + xCoord + "," + yCoord + "," + zCoord + ")";
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Triple && equals((Triple) obj);
  }

  private boolean equals(Triple pt) {
    return xCoord == pt.xCoord && yCoord == pt.yCoord && zCoord == pt.zCoord;
  }
}
