package plugins.cmds.io;


import datastructures.gui.ChannelModuleItem;
import datastructures.points.ChannelContainer;
import datastructures.points.SuperPointContainer;
import ij.gui.GenericDialog;
import net.imagej.ops.Initializable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.log.LogService;
import org.scijava.module.ModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.PanService;

import java.util.ArrayList;
import java.util.List;

/**
 * Command plugin to removeChannelSet a ChannelSet from PanStorage.
 */
@Plugin(type = Command.class, menuPath="PAN>Remove channel set...")
public class RemoveChannelSet extends DynamicCommand implements Initializable{

    @Parameter
    PanService ptStore;

    @Parameter
    LogService logService;

    List <ChannelModuleItem <Boolean, SuperPointContainer>> checkboxItems = new ArrayList <>();

    @Override
    public void initialize() {

        String[] channelSetKeys = ptStore.channelSetKeys();


        if (channelSetKeys.length == 0) throw new NullArgumentException();

        for (String channelSetKey : channelSetKeys) {
            SuperPointContainer channelSet = ptStore.getChannelSet(channelSetKey);

            final ChannelModuleItem <Boolean, SuperPointContainer> bundledChannelItem =
                    new ChannelModuleItem <>(getInfo(), channelSetKey, boolean.class, channelSet);

            bundledChannelItem.getModuleItem().setLabel(channelSetKey);
            checkboxItems.add(bundledChannelItem);
            getInfo().addInput(bundledChannelItem.getModuleItem());
        }

    }

    @Override
    public void run() {

        ModuleItem<Boolean> moduleItem;

        for (ChannelModuleItem <Boolean, SuperPointContainer> bundledChannelItem : checkboxItems) {
            moduleItem = bundledChannelItem.getModuleItem();

            if (moduleItem.getValue(this)) {
                SuperPointContainer channelSet = bundledChannelItem.getChannel();
                String name = ptStore.channelSetKey(channelSet);

                // check if item is in a batch, and break/delete batch if user says so
                if (ptStore.isInBatch(name)) {
                    //build dialogue for user choice
                    GenericDialog gd = new GenericDialog("Batched channel");
                    gd.addChoice("'" + name + "' is in a batch. What would you like to do?", BATCH_CHOICES, CHOICE_DELETE_BATCH);
                    gd.showDialog();

                    if (gd.wasCanceled()) {
                        return;
                    }

                    String choice = gd.getNextChoice();

                    if (choice.equals(CHOICE_DELETE_BATCH)) {                   // if user wants to delete batch...
                        ArrayList<String> batchNames = ptStore.getBatch(name);
                        for (String chanName : batchNames) {
                            ptStore.removeChannelSet(chanName);
                        }
                        ptStore.removeBatchNames(batchNames);
                        return;

                    } else if (choice.equals(CHOICE_DELETE_CHANNEL)) {           // if user wants to break batch and delete individual channel...
                        ptStore.removeBatchNames(ptStore.getBatch(name));
                        ptStore.removeChannelSet(name);
                        return;
                    }

                }

                ptStore.removeChannelSet(name);
            }
        }

    }

    private static final String CHOICE_DELETE_BATCH = "Delete entire batch";
    private static final String CHOICE_DELETE_CHANNEL = "Split up batch and delete individual channel set";
    private static final String[] BATCH_CHOICES = new String[]{CHOICE_DELETE_BATCH, CHOICE_DELETE_CHANNEL};
}
