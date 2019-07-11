package cmds.io;


import datastructures.OperablePointContainer;
import datastructures.PointContainer;
import cmds.gui.ChannelModuleItem;
import datastructures.SuperPointContainer;
import net.imagej.ops.Initializable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.log.LogService;
import org.scijava.module.ModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.PanContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Command plugin to remove a ChannelSet from PanStorage.
 */
@Plugin(type = Command.class, menuPath="PAN>Remove channel set...")
public class RemoveChannelSet extends DynamicCommand implements Initializable{

    @Parameter
    PanContext ptStore;

    @Parameter
    LogService logService;

    List <ChannelModuleItem <Boolean, SuperPointContainer>> checkboxItems = new ArrayList <>();

    @Override
    public void initialize() {

        String[] channelSetKeys = ptStore.keys();


        if (channelSetKeys.length == 0) throw new NullArgumentException();

        for (String channelSetKey : channelSetKeys) {
            SuperPointContainer channelSet = ptStore.get(channelSetKey);

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
                SuperPointContainer removed = ptStore.remove(ptStore.key(bundledChannelItem.getChannel()));
            }
        }

    }
}
