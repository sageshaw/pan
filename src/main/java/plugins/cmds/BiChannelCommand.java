package plugins.cmds;

import datastructures.points.SuperPointContainer;
import datastructures.points.OperablePointContainer;
import analysis.ops.AnalysisOperation;
import analysis.ops.BiOperation;
import org.apache.commons.math3.exception.NullArgumentException;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;

import java.util.ArrayList;

/**
 * Proves basic structure for a command that provides a basic structure for a command plugin
 * handling two-channel analysis.
 */
public abstract class BiChannelCommand extends AnalysisCommand {


    @Parameter(label = "From", choices = {"a", "b"})
    private String from;

    @Parameter(label = "To", choices = {"c", "d"})
    private String to;

    public void initialize() {

        ArrayList <String> options = new ArrayList <>();

        String[] channelSetKeys = panContext.channelSetKeys();

        if (channelSetKeys.length == 0) throw new NullArgumentException();

        for (String channelSetKey : channelSetKeys) {
            SuperPointContainer channelSet = panContext.getChannelSet(channelSetKey);
            String[] channelKeys = channelSet.keys();

            for (String channelKey : channelKeys) {
                options.add(channelKey + "(" + channelSetKey + ")");
            }

        }

        MutableModuleItem <String> fromItem = getInfo().getMutableInput("from", String.class);
        MutableModuleItem <String> toItem = getInfo().getMutableInput("to", String.class);

        fromItem.setChoices(options);
        toItem.setChoices(options);

        super.initialize();
    }


    protected OperablePointContainer getFromChannel() {
        String[] channelSetKeys = panContext.channelSetKeys();

        for (String channelSetKey : channelSetKeys) {
            SuperPointContainer channelSet = panContext.getChannelSet(channelSetKey);
            String[] channelKeys = channelSet.keys();

            for (String channelKey : channelKeys) {
                if (from.equals(channelKey + "(" + channelSetKey + ")"))
                    return (OperablePointContainer) channelSet.get(channelKey);
            }

        }

        return null;
    }

    protected OperablePointContainer getToChannel() {
        String[] channelSetKeys = panContext.channelSetKeys();

        for (String channelSetKey : channelSetKeys) {
            SuperPointContainer channelSet = panContext.getChannelSet(channelSetKey);
            String[] channelKeys = channelSet.keys();

            for (String channelKey : channelKeys) {
                if (to.equals(channelKey + "(" + channelSetKey + ")")) {
                    return (OperablePointContainer) channelSet.get(channelKey);
                }
            }

        }

        return null;
    }

    protected String getFromName() {
        return from;
    }

    protected String getToName() {
        return to;
    }

    @Override
    Class <? extends AnalysisOperation> getOperationType() {
        return BiOperation.class;
    }
}
