package images;

import constructs.Triple;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import java.util.List;

public class AnalysisImgGenerator implements ImgGenerator{
    @Override
    public void placeMarker(int x, int y, int z, Img <UnsignedByteType> img) {
        drawPoint(x, y, z, img);
    }
}
