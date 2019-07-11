package display;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedByteType;


/**
 * Implementation of ImgGenerator to produce an image usable for analysis (single point for where a voxel is present)
 */

@Deprecated
public class AnalysisImgGenerator implements ImgGenerator{
    @Override
    public void drawMarker(int x, int y, int z, Img <UnsignedByteType> img) {
        drawPoint(x, y, z, img);
    }
}
