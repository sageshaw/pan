package analysis.data;

import analysis.Triple;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import display.Displayable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A class meant to hold individual channels. This is used as a way to access information across channels,
 * as well as to compartmentalize sets of channels from the same dataset/image.
 */

public class ChannelContainer implements OperablePointContainer, Displayable, MappedContainer {

    //Use of Guava's BiMap for inverse hashmap functionality
    private BiMap <String, OperablePointContainer> channels = HashBiMap.create();


    //Currently unsupported, may change in the future
    @Override
    public Triple getCentroid() {
        throw new UnsupportedOperationException();
    }

    //Returns dimensions (in number of pixels) across all channels.
    @Override
    public Triple getDimensions() {
//        Triple mins = getMin();
//        Triple maxes = getMax();
//
//        return new Triple(maxes.getX()-mins.getX(), maxes.getY()-mins.getY(), maxes.getZ()-mins.getZ());
        return getMax();
    }

    //Returns a List with all contained points (Triple).
    @Override
    public List<Triple> getData() {

        ArrayList<Triple> data = new ArrayList <>();

        for (PointContainer channel : channels.values()) {
            Iterator<Triple> itr = channel.iterator();
            while(itr.hasNext()) data.add(itr.next());

        }

        return data;
    }

    //Returns lowest x, y, z coordinates  across channels
    @Override
    public Triple getMin() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        Triple mins;

        for (OperablePointContainer channel : channels.values()) {
            mins = channel.getMin();
            minX = Math.min(mins.getX(), minX);
            minY = Math.min(mins.getY(), minY);
            minZ = Math.min(mins.getZ(), minZ);
        }

        return new Triple(minX, minY, minZ);
    }

    //Returns maximum x, y, z coordinates across all channels
    @Override
    public Triple getMax() {
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        Triple maxes;
        for (OperablePointContainer channel : channels.values()) {
            maxes = channel.getMax();
            maxX = Math.max(maxes.getX(), maxX);
            maxY = Math.max(maxes.getY(), maxY);
            maxZ = Math.max(maxes.getZ(), maxZ);
        }
        return new Triple(maxX, maxY, maxZ);
    }

    //Return channel key based on OperablePointContainer object (equals method is default object signature comparison)
    @Override
    public String key(PointContainer value) {
        return channels.inverse().get(value);
    }

    //Return corresponding OperablePointContainer object to  name
    @Override
    public PointContainer get(String name) {
        return channels.get(name);
    }

    //Remove corresponding OperablePointContainer object based on name
    @Override
    public PointContainer remove(String name) {
        return channels.remove(name);
    }

    //Removes container from Map based on object
    @Override
    public boolean remove(PointContainer value) {
        return channels.remove(key(value), value);
    }

    //Returns a String array of all Keys
    @Override
    public String[] keys() {
        return channels.keySet().toArray(new String[0]);
    }

    //Reduces lowest x,y,z coordinates to zero, translates rest of the set to preserve relative distances
    @Override
    public void makeRelative() {
        Triple mins = getMin();
        translate(-mins.getX(), -mins.getY(), -mins.getZ());
    }

    //Translate contained containers a specified number of pixels
    @Override
    public void translate(int xOffset, int yOffset, int zOffset) {
        for (PointContainer channel : channels.values()) {
            channel.translate(xOffset, yOffset, zOffset);
        }
    }

    //TODO: Figure out how to use generics for this
    @Override
    public void add(Object e) {
        throw new UnsupportedOperationException("Cannot add dataset without name. Use add(String name, Object e)");
    }


    //TODO: clunky, untangle typing (TOO MUCH CASTING)
    //Add new container to map (must be a OperablePointContainer)
    @Override
    public void add(String name, PointContainer container) {
        channels.put(name, (OperablePointContainer) container);
    }

    @Override
    public Iterator <OperablePointContainer> iterator() {
        return channels.values().iterator();
    }


}




