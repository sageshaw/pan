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
 * Proves basic structure for a command that provides a basic structure for a command plugin
 * handling two-channel analysis.
 */
public abstract class BiChannelCommand extends DynamicCommand implements Initializable {

    @Parameter
    protected PanService panService;

    @Parameter(label = "Channel File", choices = {"a", "b"})
    private String channelSetName;


    private String from;

    private String to;

    public void initialize() {

        ArrayList <String> options = new ArrayList <>();

        String[] channelSetKeys = panService.channelSetKeys();

        if (channelSetKeys.length == 0) throw new NullArgumentException();

        for (String channelSetKey : channelSetKeys)
            options.add(channelSetKey);


        MutableModuleItem<String> selectSetItem = getInfo().getMutableInput("channelSetNamez", String.class);

        selectSetItem.setChoices(options);

    }

    @Override
    public void run() {

        ChannelContainer channelSet = panService.getChannelSet(channelSetName);
        String[] keys = channelSet.keys();

        GenericDialog selectChannelDialogue = new GenericDialog("Set target channels...");
        selectChannelDialogue.addMessage("Select target channels on which to operate: ");
        selectChannelDialogue.addChoice("From channel id", keys, keys[0]);
        selectChannelDialogue.addChoice("To channel id", keys, keys[0]);
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

        String from = selectChannelDialogue.getNextChoice();
        String to = selectChannelDialogue.getNextChoice();


        doOnRun(containers, from, to);
    }

    protected abstract void doOnRun(List<ChannelContainer> channelContainers, String fromChannelName, String toChannelName);

}
