package datastructures;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import display.Displayable;

import java.util.Iterator;

/**
 * A class meant to associate individual channels together. This is used as a way to access information across channels,
 * as well as to group channels together from the same sample.
 */

public class ChannelContainer<T extends OperablePointContainer> implements OperablePointContainer, SuperPointContainer, Displayable {

    //Use of Guava's BiMap for inverse hashmap functionality
    private BiMap <String, T> channels = HashBiMap.create();



    //Returns dimensions (in number of pixels) across all channels.
    @Override
    public Triple getDimensions() {
        return getMax();
    }

    //Returns a List with all contained points (Triple).
    @Override
    public Triple[] getPoints() {

        Triple[] result = new Triple[getSize()];

        int i = 0;
        for (T channel : channels.values()) {
            Triple[] chnlPts = channel.getPoints();
            for (Triple pt : chnlPts) {
                result[i] = pt;
                i++;
            }

        }
        return result;
    }

    @Override
    public Triple getCentroid() {
        int x = 0;
        int y = 0;
        int z = 0;

        for (T channel : channels.values()) {
            for (Triple pt : channel.getPoints()) {
                x += pt.getX();
                y += pt.getY();
                z += pt.getZ();
            }


        }

        return new Triple(x / getSize(), y / getSize(), z / getSize());
    }

    //Returns lowest x, y, z coordinates  across channels
    @Override
    public Triple getMin() {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;

        Triple mins;

        for (OperablePointContainer channel : channels.values()) {
            mins = channel.getMin();
            minX = Math.min(mins.getX(), minX);
            minY = Math.min(mins.getY(), minY);
            minZ = Math.min(mins.getZ(), minZ);
        }

        return new Triple(minX, minY, minZ);
    }
e
    //Returns maximum x, y, z coordinates across all channels
    @Override
    public Triple getMax() {
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double maxZ = Double.MIN_VALUE;
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
    public String key(PointContainer value) {
        return channels.inverse().get(value);
    }

    //Return corresponding OperablePointContainer object to  name
    @Override
    public T get(String name) {
        return channels.get(name);
    }

    //Remove corresponding OperablePointContainer object based on name
    @Override
    public T remove(String name) {
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
    public void translate(double xOffset, double yOffset, double zOffset) {
        for (T channel : channels.values()) {
            channel.translate(xOffset, yOffset, zOffset);
        }
    }

    @Override
    public int getSize() {
        int size = 0;

        for (T channel : channels.values()) {
            size += channel.getSize();
        }

        return size;
    }

    //TODO: figure out if this is the right action to take
    @Override
    public void add(Triple pt) {
        throw new UnsupportedOperationException("Cannot add a point to a channel set!");
    }

    @Override
    public boolean remove(Triple pt) {
        throw new UnsupportedOperationException("Cannot remove a point from a channel set!");
    }


    //Add new container to map (must be a OperablePointContainer)
    @Override
    public void add(String name, T container) {
        channels.put(name, (T) container);
    }

    @Override
    public Iterator <T> iterator() {
        return channels.values().iterator();
    }


}




