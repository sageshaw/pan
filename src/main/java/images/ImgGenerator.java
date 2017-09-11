package images;

import constructs.OperableContainer;
import constructs.Triple;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import java.util.List;

public interface ImgGenerator {

    void placeMarker(int x, int y, int z, Img<UnsignedByteType> img);

    default Img<UnsignedByteType> generate(Displayable data) {
        List<Triple> points = data.getData();
        Img<UnsignedByteType> img = blankImage(data);

        for (Triple pt : points) {
            placeMarker(pt.getX(), pt.getY(), pt.getZ(), img);
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
        r.setPosition(x, 0);
        r.setPosition(y, 1);
        r.setPosition(z, 2);
        UnsignedByteType t = r.get();
        t.set(255);
    }

}
