package cmds.gui;

import analysis.pts.PointContainer;
import org.scijava.module.DefaultMutableModuleItem;
import org.scijava.module.ModuleInfo;
import org.scijava.module.ModuleItem;

public class ChannelModuleItem<T> {

  private PointContainer channel;
  private ModuleItem<T> item;

  public ChannelModuleItem(ModuleInfo info, String name, Class type, PointContainer channel) {
    item = new DefaultMutableModuleItem<T>(info, name, type);
    this.channel = channel;


  }

  public PointContainer getChannel() {
    return channel;
  }

  public ModuleItem<T> getModuleItem() {
    return item;
  }


}
