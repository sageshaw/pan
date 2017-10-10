package display;

import analysis.Triple;

/**
 * Defines behavior of class that can be converted into an Img via ImgGenerator.
 */
public interface Displayable {


    Triple getDimensions();

    Triple[] getPoints();


}
