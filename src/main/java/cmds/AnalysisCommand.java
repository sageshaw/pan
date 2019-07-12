package cmds;

import analysis.ops.AnalysisOperation;
import analysis.ops.OpScript;
import net.imagej.ops.Initializable;
import org.scijava.ItemVisibility;
import org.scijava.command.DynamicCommand;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import plugins.PanContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides the basic structure of a command that handles analysis via OpScript. Since all analysis commands (even ones
 * that do not require OpScripts will extend this class, the use of OpScript can be arbitrarily specified.
 */
public abstract class AnalysisCommand extends DynamicCommand implements Initializable {
    @Parameter
    protected PanContext panContext;

    @Parameter(label = "Analysis", choices = {"a", "b"})
    String analysisChoice;

    public void initialize() {
        ArrayList <String> options = new ArrayList <>();
        List <Class <?>> analysisClasses = panContext.findOpScripts();
        if (analysisClasses == null) throw new IllegalStateException("Cannot find OpScripts.");

        for (Class <?> c : analysisClasses) {
            for (Annotation a : c.getAnnotations()) {
                if (a instanceof OpScript && ((OpScript) a).type() == getOperationType()) {
                    options.add(((OpScript) a).label());
                }
            }
        }

        MutableModuleItem <String> choiceItem = getInfo().getMutableInput("analysisChoice", String.class);

        if (allowAnalysisSelection()) {
            choiceItem.setChoices(options);
        } else {
            choiceItem.setVisibility(ItemVisibility.INVISIBLE);
        }
    }

    protected Class <?> getChosenAnalysisOp() {
        List <Class <?>> analysisClasses = panContext.findOpScripts();

        for (Class <?> c : analysisClasses) {
            for (Annotation a : c.getAnnotations()) {
                if (a instanceof OpScript && ((OpScript) a).type() == getOperationType()) {
                    if (analysisChoice.equals(((OpScript) a).label())) return c;
                }
            }
        }

        throw new IllegalStateException("Chosen analysis algorithm not available");
    }

    abstract Class <? extends AnalysisOperation> getOperationType();

    protected abstract boolean allowAnalysisSelection();
}
