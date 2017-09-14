package analysis.data;

import analysis.Triple;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the Linear abstract class using ArrayLists.
 */

public class DefaultLinear extends Linear {


    public DefaultLinear() {
        super(new ArrayList <>());
    }


    //TODO: figure out if there is a better way to force variable initialization
    @Override
    public List <Triple> getData() {
        return points;
    }
}
