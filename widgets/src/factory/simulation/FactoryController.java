package factory.simulation;

import factory.model.ToolThread;
import factory.model.Widget;
import factory.model.WidgetMonitor;

public class FactoryController {
    
    public static void main(String[] args) {
        Factory factory = new Factory();
        WidgetMonitor data = new WidgetMonitor(factory.getConveyor());
        
        Thread orange = new ToolThread(factory.getPaintTool(), Widget.ORANGE_MARBLE, data);
        Thread green = new ToolThread(factory.getPressTool(), Widget.GREEN_BLOB, data);
        
        orange.start();
        green.start();
        
    }
}
