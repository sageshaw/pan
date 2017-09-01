package commands;



import containers.TripleContainer;
import net.imagej.ops.Initializable;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.log.LogService;
import org.scijava.module.ModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import plugins.IOStorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Plugin(type = Command.class, menuPath="PAN>Remove channel set...")
public class RemoveChannelSet extends DynamicCommand implements Initializable{

    @Parameter
    IOStorage store;

    @Parameter
    LogService logService;

    List<ChannelModuleItem<Boolean>> checkboxItems = new ArrayList<>();

    @Override
    public void initialize() {
        Iterator panChannelSetIterator = store.iterator();
        if(!panChannelSetIterator.hasNext()) throw new NullPointerException("No sets found in plugins.IOStorage");

        while(panChannelSetIterator.hasNext()) {
            TripleContainer set = (TripleContainer) panChannelSetIterator.next();

            final ChannelModuleItem <Boolean> bundledChannelItem =
                    new ChannelModuleItem <>(getInfo(), set.getName(), boolean.class, set);

            bundledChannelItem.getModuleItem().setLabel(set.getName());
            checkboxItems.add(bundledChannelItem);
            getInfo().addInput(bundledChannelItem.getModuleItem());
        }

    }

    @Override
    public void run() {

        ModuleItem<Boolean> moduleItem;

        for (ChannelModuleItem <Boolean> bundledChannelItem : checkboxItems) {
            moduleItem = bundledChannelItem.getModuleItem();

            if (moduleItem.getValue(this)) {
               boolean isRemoved = store.remove(bundledChannelItem.getChannel());
               System.out.println("Channel set '" + bundledChannelItem.getChannel().getName() + "' removed: " + isRemoved );
            }
        }

    }
}
