import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

	private final JPanel workList;
	private final JPanel progressList;

	private final JProgressBar mainProgressBar;

	private final ExecutorService pool;

	private final int PROGRESS_FINISHED = 1000000;

	// -----------------------------------------------------------------------

	private CodeBreaker() {
		StatusWindow w = new StatusWindow();

		workList = w.getWorkList();
		progressList = w.getProgressList();
		mainProgressBar = w.getProgressBar();

		pool = Executors.newFixedThreadPool(2);
		w.enableErrorChecks();

	}

	// -----------------------------------------------------------------------

	public static void main(String[] args) {

		/*
		 * Most Swing operations (such as creating view elements) must be performed in
		 * the Swing EDT (Event Dispatch Thread).
		 * 
		 * That's what SwingUtilities.invokeLater is for.
		 */

		SwingUtilities.invokeLater(() -> {
			CodeBreaker codeBreaker = new CodeBreaker();
			new Sniffer(codeBreaker).start();
		});
	}

	// -----------------------------------------------------------------------

	/** Called by a Sniffer thread when an encrypted message is obtained. */
	@Override
	public void onMessageIntercepted(String message, BigInteger n) {

		SwingUtilities.invokeLater(() -> {

			WorklistItem workItem = new WorklistItem(n, message);

			JButton breakButton = new JButton("Break");
			breakButton.addActionListener(e -> {
				mainProgressBar.setMaximum(mainProgressBar.getMaximum() + PROGRESS_FINISHED);

				ProgressItem progressItem = new ProgressItem(n, message);
				progressList.add(progressItem);

				workList.remove(workItem);

				JButton cancelButton = new JButton("Cancel");
				Tracker tracker = new Tracker(progressItem.getProgressBar());
				Future<?> task = createTask(progressItem, cancelButton, message, n, tracker);

				cancelButton.addActionListener(e1 -> {
					tracker.cancelTracking();
					task.cancel(true);

					progressItem.getTextArea().setText("[cancelled]");
					progressItem.remove(cancelButton);
					progressItem.add(createRemoveButton(progressItem, cancelButton));

				});

				progressItem.add(cancelButton);

			});

			workItem.add(breakButton);
			workList.add(workItem);

		});

	}

	private Future<?> createTask(ProgressItem item, JButton cancelButton, String message, BigInteger n,
			ProgressTracker tracker) {

		Future<?> task = pool.submit(() -> {
			try {
				String broken = Factorizer.crack(message, n, tracker);

				SwingUtilities.invokeLater(() -> {

					item.getTextArea().setText(broken);
					item.add(createRemoveButton(item, cancelButton));

				});

			} catch (InterruptedException e) {
				System.out.println("Task cancelled!");
			}
		});

		return task;
	}

	private JButton createRemoveButton(ProgressItem item, JButton cancelButton) {
		item.remove(cancelButton);
		JButton removeButton = new JButton("Remove");

		removeButton.addActionListener(e -> {
			progressList.remove(item);

			mainProgressBar.setValue(mainProgressBar.getValue() - PROGRESS_FINISHED);
			mainProgressBar.setMaximum(mainProgressBar.getMaximum() - PROGRESS_FINISHED);

		});

		return removeButton;

	}

	private class Tracker implements ProgressTracker {
		private int totalProgress;
		private JProgressBar bar;

		private Tracker(JProgressBar bar) {
			totalProgress = 0;
			this.bar = bar;
		}

		@Override
		public void onProgress(int ppmDelta) {
			int delta = Math.min(ppmDelta, PROGRESS_FINISHED - bar.getValue());
			totalProgress += delta;

			SwingUtilities.invokeLater(() -> {
				bar.setValue(totalProgress);
				mainProgressBar.setValue(mainProgressBar.getValue() + delta);
			});

		}

		public void cancelTracking() {
			int temp = totalProgress;
			totalProgress = PROGRESS_FINISHED;

			bar.setValue(totalProgress);
			mainProgressBar.setValue(mainProgressBar.getValue() + (PROGRESS_FINISHED - temp));
		}

	}

}
