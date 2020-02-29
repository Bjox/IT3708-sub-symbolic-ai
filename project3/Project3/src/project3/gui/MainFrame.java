package project3.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import project3.Project3;
import project3.flatland.Agent;
import project3.flatland.World;

import static project3.flatland.World.*;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public final class MainFrame extends JFrame implements Runnable{
	
	private final DrawPanel content;
	private World world;
	
	private final Thread renderThread;
	private final Object syncObj = new Object();
	private boolean visible = true;
	
	public MainFrame(World world) throws HeadlessException {
		content = new DrawPanel();
		content.setPreferredSize(new Dimension(600, 600));
		
		setWorld(world);
		init();
		
		renderThread = new Thread(this);
		renderThread.start();
		
		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
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
	
	private void init() {
//		setResizable(false);
		add(content);
		pack();
	}
	
	public void setWorld(World world) {
		this.world = world;
		setXY(true);
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
	
	public void update() {
		Agent agent = world.getAgent();
		
		if (agent != null)
			setTitle(String.format("World seed: %4$s  Timestep: %1$s  Food eaten: %2$s  Poison eaten: %3$s", agent.timestep, agent.foodEaten, agent.poisonEaten, world.worldSeed));
		
		setXY(false);
	}
	
	private void setXY(boolean set) {
		Agent agent = world.getAgent();
		
		if (agent != null) {
			if (set) {
				pos_x = agent.x;
				pos_y = agent.y;
				heading = agent.heading;
				
				alpha_x = 0.0;
				alpha_y = 0.0;
				alpha_heading = 0.0;
			} else {
				pos_x += alpha_x;
				pos_y += alpha_y;
				heading += alpha_heading;
				
				long headingRound = Math.round(heading);
				if (headingRound == -1) heading = 3.0;
				else if (Math.round(heading) == 4) heading = 0.0;

				alpha_x = 0.0;
				alpha_y = 0.0;
				alpha_heading = 0.0;

				alpha_x = agent.x - pos_x;
				alpha_y = agent.y - pos_y;
				alpha_heading = agent.heading - heading;
				
				if (Math.abs(alpha_heading) > 1.1) {
					alpha_heading = -1.0 * Math.signum(alpha_heading);
				}

				if (Math.abs(alpha_x) > 1.1) {
					pos_x += alpha_x;
					alpha_x = 0.0;
				}

				if (Math.abs(alpha_y) > 1.1) {
					pos_y += alpha_y;
					alpha_y = 0.0;
				}
				
				
			}
			
		}
	}
	
	private double pos_x;
	private double pos_y;
	private double heading;
	
	private double alpha_x = 0.0;
	private double alpha_y = 0.0;
	private double alpha_heading = 0.0;
	
	public static volatile double step = 0.1 * Project3.speed;
	
	@Override
	public void run() {
		
		while (visible) {
			
			if (Double.compare(alpha_heading, 0.0) == 0)
			{
				if (Math.abs(alpha_x) >= step) {
					pos_x += step * Math.signum(alpha_x);
					alpha_x -= step * Math.signum(alpha_x);
				}
				else if (Double.compare(alpha_x, 0.0) != 0) {
					pos_x += alpha_x;
					alpha_x = 0.0;
				}

				if (Math.abs(alpha_y) >= step) {
					pos_y += step * Math.signum(alpha_y);
					alpha_y -= step * Math.signum(alpha_y);
				}
				else if (Double.compare(alpha_y, 0.0) != 0) {
					pos_y += alpha_y;
					alpha_y = 0.0;
				}
			}
			
			if (Math.abs(alpha_heading) >= step) {
				heading += step * 2.0 * Math.signum(alpha_heading);
				alpha_heading -= step * 2.0 * Math.signum(alpha_heading);
			} else if (Double.compare(alpha_heading, 0.0) != 0) {
				heading += alpha_heading;
				alpha_heading = 0.0;
			}
			
			repaint();
			
			try {
				Thread.sleep(10);
			} catch (Exception e) {
			}
		}
		
	}
	
	
	class DrawPanel extends JPanel {

		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			super.paint(g2);
			
			g2.setStroke(new BasicStroke(2.0f));
			
			final int w = getWidth();
			final int h = getHeight();
			
			final int wi = w / World.N;
			final int hi = h / World.N;
			
			for (int i = 1; i < World.N; i++) {
				g2.drawLine(i * wi, 0, i * wi, h);
				g2.drawLine(0, i * hi, w, i * hi);
			}
			
			Agent agent = world.getAgent();
			if (agent != null) {
				paintAgent(g2, (int)(pos_x * wi), (int)(pos_y * hi), wi, hi, heading);
			}
			
			final int[][] cells = world.getCells();
			
			for (int i = 0; i < World.N; i++) {
				for (int j = 0; j < World.N; j++) {
					
					if (cells[i][j] > 0) {
						if (cells[i][j] == FOOD) {
							paintFood(g2, (int)(i * wi + wi * 0.2), (int)(j * hi + hi * 0.2), wi * 0.6, hi * 0.6);
						}
						
						else if (cells[i][j] == POISON) {
							paintPoison(g2, (int)(i * wi + wi * 0.1), (int)(j * hi + hi * 0.1), wi * 0.8, hi * 0.8);
						}
					}
					
				}
			}
		}
		
		
		private final Color BODY_COLOR = new Color(0, 113, 184);
		private final Color SENSOR_COLOR = new Color(250, 125, 0);
		private final Color EYE_COLOR = new Color(255, 234, 0);
		
		private void paintAgent(Graphics2D g2, int x, int y, double scale_x, double scale_y, double heading) {
			g2.translate(x, y);
			g2.scale(scale_x * 0.1, scale_y * 0.1);
			
			g2.rotate(heading * Math.PI / 2, 5, 5);
			
			Polygon body = new Polygon();
			body.addPoint(4, 2);
			body.addPoint(2, 4);
			body.addPoint(2, 6);
			body.addPoint(4, 8);
			body.addPoint(6, 8);
			body.addPoint(8, 6);
			body.addPoint(8, 4);
			body.addPoint(6, 2);
			
			g2.setColor(BODY_COLOR);
			g2.fillPolygon(body);
			
			Polygon sensor = new Polygon();
			sensor.addPoint(5, -1);
			sensor.addPoint(4, 2);
			sensor.addPoint(6, 2);
			
			g2.setColor(SENSOR_COLOR);
			g2.fillPolygon(sensor);
			
			g2.rotate(Math.PI / 2, 5, 5);
			g2.fillPolygon(sensor);
			
			g2.rotate(-Math.PI, 5, 5);
			g2.fillPolygon(sensor);
			
			g2.rotate(Math.PI / 2, 5, 5);
			
			g2.setColor(EYE_COLOR);
			g2.fillOval(3, 3, 1, 1);
			g2.fillOval(6, 3, 1, 1);
			
			g2.rotate(-heading * Math.PI / 2, 5, 5);
			
			g2.scale(10.0 / scale_x, 10.0 / scale_y);
			g2.translate(-x, -y);
		}
		
		private final Color FOOD_COLOR = new Color(0, 153, 82);
		private final Color POISON_COLOR = new Color(255, 35, 0);
		
		public void paintFood(Graphics2D g2, int x, int y, double scale_x, double scale_y) {
			g2.translate(x, y);
			g2.scale(scale_x, scale_y);
			
			g2.setColor(FOOD_COLOR);
			g2.fillOval(0, 0, 1, 1);
			
			g2.scale(1.0 / scale_x, 1.0 / scale_y);
			g2.translate(-x, -y);
		}
		
		private void paintPoison(Graphics2D g2, int x, int y, double scale_x, double scale_y) {
			g2.translate(x, y);
			g2.scale(scale_x * 1.0 / 6.0, scale_y * 1.0 / 6.0);
			
			Polygon p = new Polygon();
			p.addPoint(3, 0);
			p.addPoint(1, 3);
			p.addPoint(3, 6);
			p.addPoint(5, 3);
			
			g2.setColor(POISON_COLOR);
			g2.fillPolygon(p);
			
			g2.scale(6.0 / scale_x, 6.0 / scale_y);
			g2.translate(-x, -y);
		}
		
	}
	
}
