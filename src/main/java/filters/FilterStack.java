package filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a stack of filters - maybe simplify with Streams?
 */

public class FilterStack implements PanFilter {

    List<PanFilter> filterList = new ArrayList<>();

    public FilterStack(PanFilter... filters) {
        for (PanFilter filter : filters) {
            filterList.add(filter);
        }
    }

    public FilterStack() {

    }

    public void add(PanFilter filter) {
        filterList.add(filter);
    }

    @Override
    public double[] filter(double[] data) {
        for (PanFilter filter : filterList) {
            data = filter.filter(data);
        }

        return data;
    }


}
