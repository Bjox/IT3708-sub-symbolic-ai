package project4.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.RenderingHints;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import project4.BeerGame;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class MainFrame extends JFrame {

	private final DrawPanel content;
	private BeerGame game;
	private final Object syncObj = new Object();
	private boolean visible = true;
	private final SliderFrame sliderFrame;
	private int delay;

	public MainFrame() {
		this.content = new DrawPanel();
		content.setPreferredSize(new Dimension(900, 450));
		sliderFrame = new SliderFrame();
		setResizable(false);
		add(content);
		pack();
		
		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				sliderFrame.dispose();
				visible = false;
				synchronized (syncObj) {
					syncObj.notifyAll();
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}
		});
	}
	
	private static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ex) {}
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		sliderFrame.setVisible(b);
	}
	
	public void waitForDispose() {
		synchronized (syncObj) {
			while (visible) {
				try {
					syncObj.wait();
				} catch (InterruptedException ex) {
					System.out.println(ex);
				}
			}
		}
	}
	
	public void setGame(BeerGame game) {
		this.game = game;
		setTitle(game.getInfoStr());
	}
	
	public void update(boolean sleep) {
		content.repaint();
		setTitle(game.getInfoStr());
		if (sleep) sleep(delay);
	}
	
	
	class DrawPanel extends JPanel {
		
		private static final int cell_w = 30;
		private static final int cell_h = 30;
		
		private final Color SMALL_OBJECT_COLOR = new Color(0, 190, 255);
		private final Color BIG_OBJECT_COLOR = Color.BLUE;
		private final Color TRACKER_COLOR = new Color(255, 30, 30);
		private final Color TRACKER_PULL_COLOR = new Color(0, 230, 0);
		private final Color SHADOW_COLOR = new Color(170, 0, 0);

		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			super.paint(g2);
			
			if (game != null) {
				int object = game.getObject();
				int object_h = game.getObjectHeight();
				int tracker = game.getTracker();
				int colMask = tracker & object & 0x3FFF_FFFF;
				
				g2.setColor(game.getLastAction() == BeerGame.Action.PULL ? TRACKER_PULL_COLOR : TRACKER_COLOR);
				for (int i = 0; i < 30; i++) {
					if ((tracker & 1 << i) != 0) paintCell(g2, 29-i, 14);
				}
				
				g2.setColor(game.getObjectLen() <= 4 ? SMALL_OBJECT_COLOR : BIG_OBJECT_COLOR);
				for (int i = 0; i < 30; i++) {
					if ((object & 1 << i) != 0) paintCell(g2, 29-i, object_h);
				}
				
				if (colMask != 0) {
					g2.setColor(SHADOW_COLOR);
					for (int i = 0; i < 30; i++) {
						if ((colMask & 1 << i) != 0) paintCell(g2, 29-i, 14);
					}
				}
				
			}
			
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(1.0f));

			final int w = getWidth();
			final int h = getHeight();

			for (int i = 1; i < 30; i++) {
				g2.drawLine(i * cell_w, 0, i * cell_w, h);
			}
			for (int i = 1; i < 15; i++) {
				g2.drawLine(0, i * cell_h, w, i * cell_h);
			}
			
		}
		
		public void paintCell(Graphics2D g2, int x, int y) {
			g2.fillRect(x * cell_w, y * cell_h, cell_w, cell_h);
		}
	}
	
	
	class SliderFrame extends JFrame {

		private final JSlider slider;
		
		public SliderFrame() throws HeadlessException {
			slider = new JSlider(0, 600, 200);
			slider.setPreferredSize(new Dimension(300, 20));
			setDelay();
			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					setDelay();
				}
			});
			
			JPanel sliderContent = new JPanel();
			
			sliderContent.add(new JLabel("Visualization speed:"));
			sliderContent.add(slider);
			
			add(sliderContent);
			pack();
		}
		
		private void setDelay() {
			int val = slider.getValue();
			delay = (int) (val * val / 1000.0 + 20);
		}
		
	}
	
}
