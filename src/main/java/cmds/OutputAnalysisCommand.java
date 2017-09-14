package cmds;

import analysis.data.MappedContainer;
import analysis.data.OperablePointContainer;
import cmds.gui.ChannelModuleItem;
import net.imagej.ops.Initializable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.scijava.command.DynamicCommand;
import org.scijava.log.LogService;
import org.scijava.module.ModuleItem;
import org.scijava.plugin.Parameter;
import plugins.IOStorage;

import java.util.ArrayList;
import java.util.List;

public abstract class OutputAnalysisCommand extends DynamicCommand implements Initializable {


    @Parameter
    protected IOStorage ptStore;

    List <ChannelModuleItem <Boolean>> checkboxItems = new ArrayList <>();
    @Parameter
    private LogService logService;


    //TODO: fix casting hell
    @Override
    public void initialize() {

        String[] channelSetKeys = ptStore.keys();

        if (channelSetKeys.length == 0) {
            throw new NullArgumentException();
        }

        MappedContainer channelSet = null;
        String[] channelKeys;

        for (String channelSetKey : channelSetKeys) {
            channelSet = (MappedContainer) ptStore.get(channelSetKey);
            channelKeys = channelSet.keys();

            for (String channelKey : channelKeys) {
                OperablePointContainer channel = (OperablePointContainer) channelSet.get(channelKey);
                final ChannelModuleItem <Boolean> bundledChannelItem =
                        new ChannelModuleItem <>(getInfo(), channelKey, boolean.class, channel);

                bundledChannelItem.getModuleItem().setLabel(channelKey + "(" + channelSetKey + ")");
                checkboxItems.add(bundledChannelItem);
                getInfo().addInput(bundledChannelItem.getModuleItem());
            }
        }
    }


    protected List <ChannelModuleItem <Boolean>> getCheckedModules() {
        ArrayList <ChannelModuleItem <Boolean>> checkItems = new ArrayList <>();

        ModuleItem <Boolean> moduleItem;

        for (ChannelModuleItem <Boolean> bundledChannelItem : checkboxItems) {
            moduleItem = bundledChannelItem.getModuleItem();
            if (moduleItem.getValue(this)) {
                checkItems.add(bundledChannelItem);
            }
        }

        return checkItems;
    }
}
