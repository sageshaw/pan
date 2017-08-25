package containers;

import java.util.ArrayList;
import java.util.List;

/** Flyweight class for TripleContainers.
 *
 *
 *
 */

public class ChannelSet implements TripleContainer {

    private ArrayList<TripleContainer> channels = new ArrayList<>();



    @Override
    public Triple getMin() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        Triple mins;

        for (TripleContainer channel: channels) {
            mins = channel.getMin();
            minX = Math.min(mins.getX(), minX);
            minY = Math.min(mins.getY(), minY);
            minZ = Math.min(mins.getZ(), minZ);
        }

        return new Triple(minX, minY, minZ);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<Triple> getPoints() {
        ArrayList<Triple> masterList = new ArrayList<>();
        for (TripleContainer channel: channels) {
            masterList.addAll(channel.getPoints());
        }

        return masterList;
    }

    public TripleContainer getChannel(String name) {
        for (TripleContainer channel : channels) {
            if (channel.getName().equals(name)) return channel;
        }

        return null;

    }

    public void addChannel(TripleContainer channel) {
        channels.add(channel);
    }

    public TripleContainer removeChannel(String name) {
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getName().equals(name)) return channels.remove(i);
        }

        return null;
    }

    @Override
    public void makeRelative() {
        for (TripleContainer channel : channels) {
            channel.makeRelative();
        }
    }
}
