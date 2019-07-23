package display;

import datastructures.points.Triple;

/**
 * Defines behavior of class that can be converted into an Img via ImgGenerator.
 */
@Deprecated
public interface Displayable {


    Triple getDimensions();

    Triple[] getPoints();


}
