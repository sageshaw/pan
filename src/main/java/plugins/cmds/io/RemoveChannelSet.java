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

                // check if item is in a batch, and break batch if user says so
                if (ptStore.isInBatch(name)) {
                    GenericDialog gd = new GenericDialog("Split batch...");
                    gd.addMessage("'" + name + "' is in a batch. To remove the channel set, you must split the batch. Would you like to split it? This action cannot be undone.");
                    gd.setOKLabel("Yes");
                    gd.setCancelLabel("No");
                    gd.showDialog();
                    if(gd.wasCanceled()) return;

                    ptStore.removeBatchNames(ptStore.getBatch(name));
                }
                SuperPointContainer removed = ptStore.removeChannelSet(ptStore.channelSetKey(bundledChannelItem.getChannel()));
            }
        }

    }
}
