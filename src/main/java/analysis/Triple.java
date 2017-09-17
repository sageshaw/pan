package analysis;

import analysis.data.PointContainer;

import java.util.Iterator;

//Because Java doesn't really  have a tuple construct (this one is a triple) to represent a voxel
public class Triple implements PointContainer {

  //coordinate fields
  private int x, y, z;

  //assigned on creation, fields should never change (hence final)
  public Triple(int x, int y, int z) {

    this.x = x;
    this.y = y;
    this.z = z;
  }

  //Some getters
  public int getX() {
    return x;
  }

  public void setX(int xCoord) {
    this.x = xCoord;
  }

  public int getY() {
    return y;
  }

  //Some setters (use sparingly, only if you know what you're doing)

  public void setY(int yCoord) {
    this.y = yCoord;
  }

  public int getZ() {
    return z;
  }

  public void setZ(int zCoord) {
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


    @Override
    public void translate(int xOffset, int yOffset, int zOffset) {
        throw new UnsupportedOperationException("Use getters/setters instead");
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public void makeRelative() {
        throw new UnsupportedOperationException("Need another point to relate");
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException("This is a single point. No need to iterate");
    }

}
