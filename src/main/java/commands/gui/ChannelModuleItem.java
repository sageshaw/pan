package commands.gui;

import constructs.TripleContainer;
import org.scijava.module.DefaultMutableModuleItem;
import org.scijava.module.ModuleInfo;
import org.scijava.module.ModuleItem;

public class ChannelModuleItem<T> {

  private TripleContainer channel;
  private ModuleItem<T> item;

  public ChannelModuleItem(ModuleInfo info, String name, Class type, TripleContainer channel) {
    item = new DefaultMutableModuleItem<T>(info, name, type);
    this.channel = channel;


  }

  public TripleContainer getChannel() {
    return channel;
  }

  public ModuleItem<T> getModuleItem() {
    return item;
  }


}
