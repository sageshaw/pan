import java.util.ArrayList;
import java.util.List;

//Channel object to hold point data + channel name
public class Channel {

    //our fields
    private List<Point> points;
    private String name;


    public Channel(String channelName) {
        this(channelName, new ArrayList<>());
    }

    public Channel(String channelName, List<Point> data) {
        name = channelName;
        points = data;

    }

    //Some getters + setters
    public List<Point> getPoints() {
        return points;
    }

    public String getName() {
        return name;
    }


}
