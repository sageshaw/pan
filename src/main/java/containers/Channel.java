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
        int minX = -1;
        int minY = -1;
        int minZ = -1;

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

        for (int i = 0; i < channels.length; i++) {
            mins = channels[i].getMin();
            minX = mins.getX();
            minY = mins.getY();
            minZ = mins.getZ();


        }

        for (int i = 0; i < channels.length; i++) {

            for (int j = 0; j < channels[i].points.size(); j++) {
                channels[i].points.get(j).setX(channels[i].points.get(j).getX() - minX);
                channels[i].points.get(j).setY(channels[i].points.get(j).getY() - minY);
                channels[i].points.get(j).setZ(channels[i].points.get(j).getZ() - minZ);
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
