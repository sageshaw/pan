package cmds.gui;

import analysis.data.PointContainer;
import org.scijava.module.DefaultMutableModuleItem;
import org.scijava.module.ModuleInfo;
import org.scijava.module.ModuleItem;

//Bundles ModuleItems<T> and PointContainers together to easily perform dynamic checklist guis (see ExportAnalysis)
public class ChannelModuleItem<T, K extends PointContainer> {

  //the ListPointContainer and ModuleItem
  private K channel;
  private String name;
  private ModuleItem<T> item;


  public ChannelModuleItem(ModuleInfo info, String name, Class type, K channel) {
    item = new DefaultMutableModuleItem<T>(info, name, type);
    this.channel = channel;

  }

  public K getChannel() {
    return channel;
  }

  public ModuleItem<T> getModuleItem() {
    return item;
  }

}
