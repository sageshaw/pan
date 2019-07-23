package display;

import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * An implementation of ImgGenerator for client viewing (better than individual pixels
 */
@Deprecated
public class DisplayImgGenerator implements ImgGenerator {

    public enum PointMarker {
        plus, cross
    }

    private static final int DEFAULT_LINE_LENGTH = 10;

    private PointMarker shape;
    private int lineLength;

    public DisplayImgGenerator(PointMarker shape) {
        this(shape, DEFAULT_LINE_LENGTH);
    }

    public DisplayImgGenerator(PointMarker shape, int lineLength) {
        this.shape = shape;
        this.lineLength = lineLength;
    }


    @Override
    public void drawMarker(int x, int y, int z, Img <UnsignedByteType> img) {

        if (shape == PointMarker.cross) drawThickX(x, y, z, lineLength, img);
        else if(shape == PointMarker.plus) drawThickPlus(x, y, z, lineLength, img);

    }


    private void drawThickPlus(int x, int y, int z, int length, Img<UnsignedByteType> img) {
        int thickenSize = length/5;

        for (int i = 0; i < thickenSize; i++) {
            drawThinPlus(x+i, y+i, z, length, img );
        }
        for (int i = 0; i < thickenSize; i++) {
            drawThinPlus(x-i, y-i, z, length, img );
        }
        for (int i = 0; i < thickenSize; i++) {
            drawThinPlus(x-i, y+i, z, length, img );
        }
        for (int i = 0; i < thickenSize; i++) {
            drawThinPlus(x+i, y-i, z, length, img );
        }
    }

    private void drawThinPlus(int x, int y, int z, int length, Img<UnsignedByteType> img) {

        drawPoint(x, y, z, img);

        for (int i = 1; i < length; i++) {
            drawPoint(x+i, y, z, img);
        }
        for (int i = 1; i < length; i++) {
            drawPoint(x-i, y, z, img);

        }
        for (int i = 1; i < length; i++) {
            drawPoint(x, y+i, z, img);

        }
        for (int i = 1; i < length; i++) {
            drawPoint(x, y-i, z, img);
        }

    }

    private void drawThickX(int x, int y, int z, int length, Img<UnsignedByteType> img) {
        int thickenSize = length/5;

        for (int i = 0; i < thickenSize; i++) {
            drawThinX(x+i, y, z, length, img );
        }
        for (int i = 0; i < thickenSize; i++) {
            drawThinX(x-i, y, z, length, img );
        }
        for (int i = 0; i < thickenSize; i++) {
            drawThinX(x, y+i, z, length, img );
        }
        for (int i = 0; i < thickenSize; i++) {
            drawThinX(x, y-i, z, length, img );
        }
    }

    private void drawThinX(int x, int y, int z, int lineLength, Img<UnsignedByteType> img) {
        drawPoint(x, y, z, img);

        for (int i = 1; i < lineLength; i++) {
            drawPoint(x+i, y+i, z, img);
        }
        for (int i = 1; i < lineLength; i++) {
            drawPoint(x-i, y-i, z, img);
        }
        for (int i = 1; i < lineLength; i++) {
            drawPoint(x+i, y-i, z, img);
        }
        for (int i = 1; i < lineLength; i++) {
            drawPoint(x-i, y+i, z, img);
        }
    }


}
