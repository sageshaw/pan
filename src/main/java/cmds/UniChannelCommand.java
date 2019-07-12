package cmds;

import datastructures.points.SuperPointContainer;
import datastructures.points.PointContainer;
import analysis.ops.AnalysisOperation;
import analysis.ops.UniOperation;
import cmds.gui.ChannelModuleItem;
import org.apache.commons.math3.exception.NullArgumentException;
import org.scijava.log.LogService;
import org.scijava.module.ModuleItem;
import org.scijava.plugin.Parameter;
import plugins.PanContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides basic framework for single-channel analysis. This class will automatically grab current loaded
 * ChannelSets to display them to the gui, and will return the selected ChannelSets to the command extending this one.
 */
public abstract class UniChannelCommand extends AnalysisCommand {

    //Grab the instances of classes we need
    @Parameter
    protected PanContext ptStore;

    List <ChannelModuleItem <Boolean, PointContainer>> checkboxItems = new ArrayList <>();
    @Parameter
    private LogService logService;


    //Sets up dynamic list of channel options to output on histogram
    @Override
    public void initialize() {
        //Grab names of all channelSets to iterate (normally, we'd use the iterator, but we need to know the names
        //as well
        String[] channelSetKeys = ptStore.channelSetKeys();

        //If there are none, throw an exception
        if (channelSetKeys.length == 0) {
            throw new NullArgumentException();
        }

        //Some variable declarations so we do not have to reinstantiate references every single loop
        //iteration
        SuperPointContainer channelSet;
        String[] channelKeys;

        //Using the channelSetKeys for the channelSets, iterate through the channelSets and grab the channelSetKeys inside each individual
        //channelSet to iterate through channels
        for (String channelSetKey : channelSetKeys) {
            //Grab a channelSet from PanContext to iterate through its channels
            channelSet = ptStore.getChannelSet(channelSetKey);
            channelKeys = channelSet.keys();
            //Using channelSetKeys, grab channel and create a ChannelModuleItem (bundled channel and ModuleItem) and
            //addChannelSet to a List for a checkBoxList to generate dynamic GUI
            for (String channelKey : channelKeys) {
                //Grab channel
                PointContainer channel = channelSet.get(channelKey);
                //Create bundled ChannelModuleItem
                final ChannelModuleItem <Boolean, PointContainer> bundledChannelItem =
                        new ChannelModuleItem <>(getInfo(), channelKey, boolean.class, channel);

                //setup up post-instantiation properties for ModuleItem and addChannelSet to dynamic list
                bundledChannelItem.getModuleItem().setLabel(channelKey + "(" + channelSetKey + ")");
                checkboxItems.add(bundledChannelItem);
                getInfo().addInput(bundledChannelItem.getModuleItem());
            }
        }

        super.initialize();
    }

    //Method to grab the module list of all checked items. This supplies a list bundled channel item.
    private List <ChannelModuleItem <Boolean, PointContainer>> getCheckedModules() {
        //Instantiate a list for the result
        ArrayList <ChannelModuleItem <Boolean, PointContainer>> checkItems = new ArrayList <>();
        //Preinstantiation of reference so we don't have to throw it away
        ModuleItem <Boolean> moduleItem;
        //Iterate through checkBoxItems, addChannelSet to checkItems if the item was checked in GUI
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

    @Override
    Class <? extends AnalysisOperation> getOperationType() {
        return UniOperation.class;
    }

}
