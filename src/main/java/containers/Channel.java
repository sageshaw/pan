package containers;

import java.util.ArrayList;
import java.util.List;

//containers.Channel object to hold point data + channel name
public class Channel {

    //our fields
    private List<Triple> points;
    private String name;


    public Channel(String channelName) {
        this(channelName, new ArrayList<>());
    }

    public Channel(String channelName, List<Triple> data) {
        name = channelName;
        points = data;

    }

    private Triple getMin() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        for (Triple pt : points) {
            minX = Math.min(pt.getX(), minX);
            minY = Math.min(pt.getY(), minY);
            minZ = Math.min(pt.getZ(), minZ);
        }

        return new Triple(minX, minY, minZ);
    }


    public static void makeRelative(Channel... channels) {

        Triple mins;
        int minX = 0;
        int minY = 0;
        int minZ = 0;

        for (Channel channel1 : channels) {
            mins = channel1.getMin();
            minX = mins.getX();
            minY = mins.getY();
            minZ = mins.getZ();


        }

        for (Channel channel : channels) {

            for (int j = 0; j < channel.points.size(); j++) {
                channel.points.get(j).setX(channel.points.get(j).getX() - minX);
                channel.points.get(j).setY(channel.points.get(j).getY() - minY);
                channel.points.get(j).setZ(channel.points.get(j).getZ() - minZ);
            }
        }
    }

    //Some getters + setters
    public List<Triple> getPoints() {
        return points;
    }

    public String getName() {
        return name;
    }


}
