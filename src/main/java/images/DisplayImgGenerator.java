package images;

import constructs.Triple;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import java.util.List;

public class DisplayImgGenerator implements ImgGenerator {


    @Override
    public void placeMarker(int x, int y, int z, Img <UnsignedByteType> img) {
        drawX(x, y, z, 10, img);
    }

    private void drawX(int x, int y, int z, int lineLength, Img<UnsignedByteType> img) {
        for (int i = 1; i < lineLength; i++) {
            try {
                drawPoint(x+i, y+i, z, img);

            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Point ("+x+","+y+") out of bounds. Skipping...");
            }
        }
        for (int i = 1; i < lineLength; i++) {
            try {
                drawPoint(x-i, y-i, z, img);

            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Point ("+x+","+y+") out of bounds. Skipping...");
            }
        }
        for (int i = 1; i < lineLength; i++) {
            try {
                drawPoint(x-i, y+i, z, img);

            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Point ("+x+","+y+") out of bounds. Skipping...");
            }
        }
        for (int i = 1; i < lineLength; i++) {
            try {
                drawPoint(x+i, y-i, z, img);

            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Point ("+x+","+y+") out of bounds. Skipping...");
            }
        }
    }


}
