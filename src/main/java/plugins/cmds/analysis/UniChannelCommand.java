package plugins.cmds.analysis;

import datastructures.points.ChannelContainer;
import ij.gui.GenericDialog;
import org.apache.commons.math3.exception.NullArgumentException;
import org.scijava.Initializable;
import org.scijava.command.DynamicCommand;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import plugins.PanService;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides basic framework for single-channel analysis. This class will automatically grab current loaded
 * ChannelSets to display them to the gui, and will return the selected ChannelSets to the command extending this one.
 */

@Deprecated
public abstract class UniChannelCommand extends DynamicCommand implements Initializable {

    @Parameter
    protected PanService panService;

    @Parameter(label = "Channel", choices = {"a", "b"})
    private String channelSetName;

    @Override
    public void initialize() {

        ArrayList<String> options = new ArrayList<>();

        String[] channelSetKeys = panService.channelSetKeys();

        if (channelSetKeys.length == 0) throw new NullArgumentException();

        for (String channelSetKey : channelSetKeys)
            options.add(channelSetKey);

        MutableModuleItem<String> selectSetItem = getInfo().getMutableInput("channelSetName", String.class);

        selectSetItem.setChoices(options);
    }

    public void run() {

        ChannelContainer channelSet = panService.getChannelSet(channelSetName);
        String[] keys = channelSet.keys();

        GenericDialog selectChannelDialogue = new GenericDialog("Set target channels...");
        selectChannelDialogue.addMessage("Select target channels on which to operate: ");
        selectChannelDialogue.addChoice("Channel id", keys, keys[0]);
        selectChannelDialogue.showDialog();

        if (selectChannelDialogue.wasCanceled()) return;

        List<ChannelContainer> containers = new ArrayList<ChannelContainer>();

        if (channelSet.isBatched()) {
            GenericDialog batchDialogue = new GenericDialog("Found batch...");
            batchDialogue.addMessage("'" + channelSetName + "' was found to be in a batch. Run operation on entire batch?");
            batchDialogue.setOKLabel("Yes");
            batchDialogue.setCancelLabel("No");
            batchDialogue.showDialog();

            if (batchDialogue.wasOKed()) {
                containers = panService.getChannelSetBatch(channelSet.getBatchKey());
            } else {
                containers.add(channelSet);
            }

        } else {
            containers.add(channelSet);
        }

        String channelID = selectChannelDialogue.getNextChoice();

        doOnRun(containers, channelID);


    }

    protected abstract void doOnRun(List<ChannelContainer> channelContainers, String channelName);





}
