package factory.model;

public class ToolThread extends Thread {

	private WidgetMonitor data;
	private Tool tool;
	private Widget widget;

	public ToolThread(Tool tool, Widget widget, WidgetMonitor data) {
		this.data = data;
		this.tool = tool;
		this.widget = widget;
	}

	@Override
	public void run() {

		while (true) {

			try {
				tool.waitFor(widget);
				data.stop();
				tool.performAction();
				data.start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		
	}

}
