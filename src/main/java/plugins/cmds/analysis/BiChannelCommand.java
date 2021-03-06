package plugins.cmds.analysis;

import datastructures.points.ChannelContainer;
import ij.gui.GenericDialog;
import org.scijava.Initializable;
import org.scijava.command.DynamicCommand;
import org.scijava.log.LogService;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import plugins.PanService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Proves basic structure for a command that provides a basic structure for a command plugin
 * handling two-channel analysis.
 */
public abstract class BiChannelCommand extends DynamicCommand implements Initializable {

    private HashMap<String, String> displayToChannelName;

    @Parameter
    protected LogService logService;

    @Parameter
    protected PanService panService;

    @Parameter(label = "Channel File", choices = {"a", "b"})
    private String channelSetDisplayName;

    public void initialize() {

        ArrayList<String> options = new ArrayList<>();

        String[] channelSetKeys = panService.channelSetKeys();

        if (channelSetKeys.length == 0) cancel("No channels loaded");

        displayToChannelName = new HashMap<>();

        for (String channelSetKey : channelSetKeys) {
            String displayName;
            ChannelContainer channelSet = panService.getChannelSet(channelSetKey);

            if (channelSet.isBatched()) {
                displayName = "[" + channelSet.getBatchKey() + "] " + channelSetKey;
            } else {
                displayName = channelSetKey;
            }

            displayToChannelName.put(displayName, channelSetKey);
            options.add(displayName);
        }

        MutableModuleItem<String> selectSetItem = getInfo().getMutableInput("channelSetDisplayName", String.class);

        selectSetItem.setChoices(options);

    }

    @Override
    public void run() {

        String channelSetName = displayToChannelName.get(channelSetDisplayName);

        ChannelContainer channelSet = panService.getChannelSet(channelSetName);
        String[] keys = channelSet.keys();

        GenericDialog selectChannelDialogue = new GenericDialog("Set target channels...");
        selectChannelDialogue.addMessage("Select target channels on which to operate: ");
        selectChannelDialogue.addChoice("From channel id", keys, keys[0]);
        selectChannelDialogue.addChoice("To channel id", keys, keys[0]);
        selectChannelDialogue.showDialog();

        if (selectChannelDialogue.wasCanceled()) return;

        String from = selectChannelDialogue.getNextChoice();
        String to = selectChannelDialogue.getNextChoice();

        boolean runOnBatch = false;
        if (channelSet.isBatched()) {
            GenericDialog batchDialogue = new GenericDialog("Found batch...");
            batchDialogue.addMessage("'" + channelSetName + "' was found to be in a batch. Run operation on entire batch?");
            batchDialogue.setOKLabel("Yes");
            batchDialogue.setCancelLabel("No");
            batchDialogue.showDialog();
            runOnBatch = batchDialogue.wasOKed();
        }

        if (!setup(channelSetName, channelSet, from, to, runOnBatch)) return;


        if (runOnBatch) {

            List<ChannelContainer> containers = panService.getChannelSetBatch(channelSet.getBatchKey());

            for (ChannelContainer container : containers) {
                forEveryChannelSetDo(panService.channelSetKey(container), container, from, to, true);
            }

            end();

            return;
        }


        forEveryChannelSetDo(channelSetName, channelSet, from, to, false);
        end();

        return;
    }

    protected abstract boolean setup(String channelSetName, ChannelContainer channels, String fromChannelName, String toChannelName, boolean isBatched);

    //TODO: implement boolean stopper for other command types as well (if necessary)
    protected abstract void forEveryChannelSetDo(String channelSetName, ChannelContainer channels, String fromChannelName, String toChannelName, boolean isBatched);

    protected abstract void end();


}
