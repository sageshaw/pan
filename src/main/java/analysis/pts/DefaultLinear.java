package analysis.pts;

import analysis.Triple;

import java.util.ArrayList;
import java.util.List;

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
