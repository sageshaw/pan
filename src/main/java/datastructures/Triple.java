package datastructures;

/**
 * Representation of a three-dimensional point, since Java does not natively support tuples.
 */
public class Triple {

  public static Triple MAX_TRIPLE() {
    return new Triple(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
  }

  public static Triple MIN_TRIPLE() {
    return new Triple(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
  }

  public static Triple ZERO_TRIPLE() {
    return new Triple(0.0, 0.0, 0.0);
  }

  //coordinate fields
  private double x, y, z;

  //assigned on creation, fields should never change (hence final)
  public Triple(double x, double y, double z) {

    this.x = x;
    this.y = y;
    this.z = z;
  }

  //Some getters
  public double getX() {
    return x;
  }

  public void setX(double xCoord) {
    this.x = xCoord;
  }

  public double getY() {
    return y;
  }

  //Some setters (use sparingly, only if you know what you're doing)

  public void setY(double yCoord) {
    this.y = yCoord;
  }

  public double getZ() {
    return z;
  }

  public void setZ(double zCoord) {
    this.z = zCoord;
  }

  //basic Object method override
  @Override
  public String toString() {
    return "(" + x + "," + y + "," + z + ")";
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Triple && equals((Triple) obj);
  }

  private boolean equals(Triple pt) {
    return x == pt.x && y == pt.y && z == pt.z;
  }





}
