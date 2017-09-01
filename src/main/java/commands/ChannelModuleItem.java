package commands;

import containers.OperableContainer;
import containers.TripleContainer;
import org.scijava.module.DefaultMutableModuleItem;
import org.scijava.module.ModuleInfo;
import org.scijava.module.ModuleItem;

class ChannelModuleItem<T> {

  private TripleContainer channel;
  private ModuleItem<T> item;

  ChannelModuleItem(ModuleInfo info, String name, Class type, TripleContainer channel) {
    item = new DefaultMutableModuleItem<T>(info, name, type);
    this.channel = channel;


  }

  TripleContainer getChannel() {
    return channel;
  }

  ModuleItem<T> getModuleItem() {
    return item;
  }


}
