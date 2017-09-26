package cmds;

import analysis.data.MappedPointContainer;
import analysis.data.PointContainer;
import cmds.gui.ChannelModuleItem;
import net.imagej.ops.Initializable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.scijava.command.DynamicCommand;
import org.scijava.log.LogService;
import org.scijava.module.ModuleItem;
import org.scijava.plugin.Parameter;
import plugins.IOStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DynamicOutputSingleChannel extends DynamicCommand implements Initializable {

    //Grab the instances of classes we need
    @Parameter
    protected IOStorage ptStore;

    List <ChannelModuleItem <Boolean, PointContainer>> checkboxItems = new ArrayList <>();
    @Parameter
    private LogService logService;


    //Sets up dynamic list of channel options to output on histogram
    @Override
    public void initialize() {
        //Grab names of all channelSets to iterate (normally, we'd use the iterator, but we need to know the names
        //as well
        String[] channelSetKeys = ptStore.keys();

        //If there are none, throw an exception
        if (channelSetKeys.length == 0) {
            throw new NullArgumentException();
        }

        //Some variable declarations so we do not have to reinstantiate references every single loop
        //iteration
        MappedPointContainer channelSet = null;
        String[] channelKeys;

        //Using the keys for the channelSets, iterate through the channelSets and grab the keys inside each individual
        //channelSet to iterate through channels
        for (String channelSetKey : channelSetKeys) {
            //Grab a channelSet from IOStorage to iterate through its channels
            channelSet = ptStore.get(channelSetKey);
            channelKeys = channelSet.keys();
            //Using keys, grab channel and create a ChannelModuleItem (bundled channel and ModuleItem) and
            //add to a List for a checkBoxList to generate dynamic GUI
            for (String channelKey : channelKeys) {
                //Grab channel
                PointContainer channel = channelSet.get(channelKey);
                //Create bundled ChannelModuleItem
                final ChannelModuleItem <Boolean, PointContainer> bundledChannelItem =
                        new ChannelModuleItem <>(getInfo(), channelKey, boolean.class, channel);

                //setup up post-instantiation properties for ModuleItem and add to dynamic list
                bundledChannelItem.getModuleItem().setLabel(channelKey + "(" + channelSetKey + ")");
                checkboxItems.add(bundledChannelItem);
                getInfo().addInput(bundledChannelItem.getModuleItem());
            }
        }
    }

    //Method to grab the module list of all checked items. This supplies a list bundled channel item.
    protected List <ChannelModuleItem <Boolean, PointContainer>> getCheckedModules() {
        //Instantiate a list for the result
        ArrayList <ChannelModuleItem <Boolean, PointContainer>> checkItems = new ArrayList <>();
        //Preinstantiation of reference so we don't have to throw it away
        ModuleItem <Boolean> moduleItem;
        //Iterate through checkBoxItems, add to checkItems if the item was checked in GUI
        for (ChannelModuleItem <Boolean, PointContainer> bundledChannelItem : checkboxItems) {
            moduleItem = bundledChannelItem.getModuleItem();
            if (moduleItem.getValue(this)) {
                checkItems.add(bundledChannelItem);
            }
        }

        return checkItems;
    }

    protected Map <String, PointContainer> getCheckedChannels() {
        Map <String, PointContainer> result = new HashMap <String, PointContainer>();
        List <ChannelModuleItem <Boolean, PointContainer>> checkedItems = getCheckedModules();

        for (ChannelModuleItem <Boolean, PointContainer> bundledChannelItem : checkedItems) {
            result.put(bundledChannelItem.getModuleItem().getName(), bundledChannelItem.getChannel());
        }

        return result;
    }
}
