//Because Java doesn't really  have a tuple construct (this one is a triple)
public class Point {

    //coordinate fields
    private final int X_COORD;
    private final int Y_COORD;
    private final int Z_COORD;

    //assigned on creation, fields should never change (hence final)
    public Point(int x, int y, int z) {

        X_COORD = x;
        Y_COORD = y;
        Z_COORD = z;

    }

    //Some getters
    public int getX_COORD() {
        return X_COORD;
    }

    public int getY_COORD() {
        return Y_COORD;
    }

    public int getZ_COORD() {
        return Z_COORD;
    }

    //basic Object method override
    @Override
    public String toString() {
        return "("+ X_COORD +","+ Y_COORD +","+ Z_COORD +")";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Point && equals((Point)obj);

    }

    private boolean equals(Point pt) {
        return X_COORD == pt.X_COORD && Y_COORD == pt.Y_COORD && Z_COORD == pt.Z_COORD;
    }


}
