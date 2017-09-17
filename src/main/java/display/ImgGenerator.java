package display;

import analysis.Triple;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public interface ImgGenerator {

    void drawMarker(int x, int y, int z, Img<UnsignedByteType> img);


    default Img <UnsignedByteType> generate(Displayable... datasets) {

        Triple[] points;
        int size = 0;

        for (Displayable data : datasets) {
            size += data.getPoints().length;
        }

        points = new Triple[size];
        int i = 0;
        for (Displayable data : datasets) {
            for (Triple pt : data.getPoints()) {
                points[i] = pt;
                i++;
            }
        }

        Img <UnsignedByteType> img = blankImage(datasets);
        for (Triple pt : points) {
            drawMarker(pt.getX(), pt.getY(), pt.getZ(), img);
        }

        return img;


    }

    default Img <UnsignedByteType> blankImage(Displayable... datasets) {
        Triple maxDims = Triple.ZERO_TRIPLE();
        for (Displayable data : datasets) {
            Triple curDims = data.getDimensions();
            maxDims.setX(Math.max(maxDims.getX(), curDims.getX()));
            maxDims.setY(Math.max(maxDims.getY(), curDims.getY()));
            maxDims.setZ(Math.max(maxDims.getZ(), curDims.getZ()));
        }

        ImgFactory<UnsignedByteType> imgFactory = new ArrayImgFactory<>();
        int[] dims = new int[]{maxDims.getX() + 1, maxDims.getY() + 1, maxDims.getZ() + 1};
        return imgFactory.create(dims, new UnsignedByteType() );
    }

    default void drawPoint(int x, int y, int z, Img<UnsignedByteType> img) {
        RandomAccess<UnsignedByteType> r = img.randomAccess();

        if (x >= img.dimension(0) || y >= img.dimension(1) || z >= img.dimension(2) || x < 0 || y < 0 || z < 0) return;

        //System.out.println("x:" + x + "y:"  + y + "z:" + z + " " + img.dimension(0) + " " + img.dimension(1) + " " + img.dimension(2));

        r.setPosition(x, 0);
        r.setPosition(y, 1);
        r.setPosition(z, 2);
        UnsignedByteType t = r.get();
        t.set(255);
    }

}
