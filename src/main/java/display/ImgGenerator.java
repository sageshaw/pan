package display;

import structs.Triple;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import java.util.List;

public interface ImgGenerator {

    void drawMarker(int x, int y, int z, Img<UnsignedByteType> img);

    default Img<UnsignedByteType> generate(Displayable data) {
        List<Triple> points = data.getData();
        Img<UnsignedByteType> img = blankImage(data);

        for (Triple pt : points) {
            drawMarker(pt.getX(), pt.getY(), pt.getZ(), img);
        }

        return img;
    }

    default Img<UnsignedByteType> blankImage(Displayable data) {
        Triple tupleDims = data.getDimensions();
        ImgFactory<UnsignedByteType> imgFactory = new ArrayImgFactory<>();
        int[] dims = new int[]{tupleDims.getX()+1, tupleDims.getY()+1, tupleDims.getZ()+1};
        return imgFactory.create(dims, new UnsignedByteType() );
    }

    default void drawPoint(int x, int y, int z, Img<UnsignedByteType> img) {
        RandomAccess<UnsignedByteType> r = img.randomAccess();

        if (x > img.dimension(0) || y > img.dimension(1) || z > img.dimension(2) || x < 0 || y <0 || z < 0) return;

        r.setPosition(x, 0);
        r.setPosition(y, 1);
        r.setPosition(z, 2);
        UnsignedByteType t = r.get();
        t.set(255);
    }

}
