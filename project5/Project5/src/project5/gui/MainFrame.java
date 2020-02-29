package project5.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import project5.ea.Population;
import project5.ea.Ptype;
import static project5.gui.MainFrame.DrawType.*;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class MainFrame extends JFrame {
	
	private final ArrayList<Population> pops;
	
	private DrawType drawType = POPULATION;
	private final DrawPanel drawPanel;
	
	private final Object lock = new Object();
	
	public MainFrame(int w, int h) throws HeadlessException {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pops = new ArrayList<>();
		drawPanel = new DrawPanel();
		setResizable(true);
		drawPanel.setPreferredSize(new Dimension(w, h));
		
		add(drawPanel);
		pack();
	}
	
	
	public void addPopulation(Population pop) {
		pops.add(pop);
	}
	
	
	public void draw(DrawType mode, int generation, int nonDominated) {
		drawType = mode;
		updateTitle(generation, nonDominated);
		repaintPanel();
	}
	
	
	private void updateTitle(int generation, int nonDominated) {
		if (pops.size() == 1) setTitle(String.format("Generation: %1$-8sNon-dominated solutions: %2$s", String.valueOf(generation), String.valueOf(nonDominated)));
		else setTitle("");
	}
	
	
	private void repaintPanel() {
		drawPanel.repaint();
		if (!isVisible()) return;
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException ex) {
			}
		}
	}

	
	class DrawPanel extends JPanel {
		
		public final boolean DARK = false;
		
		public static final int PLOT_SIZE       = 6;
		public static final int MAX_X           = 180000; // Distance
		public static final int MAX_Y           = 2000; // Cost
		public static final int AXIS_LABELS     = 10;
		public static final int AXIS_LABEL_SIZE = 5;
		public static final int AXIS_INDENT     = 35;
		
		private final Color BACKGROUND = DARK ? Color.BLACK : Color.WHITE;
		private final Color LABEL_COLOR = DARK ? Color.WHITE : Color.BLACK;
		
		public static final int PLOT_RAD = PLOT_SIZE >> 1;
		public final Color[] COLORS = { Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.ORANGE, Color.YELLOW, Color.PINK, Color.GRAY, Color.MAGENTA };

		
		public DrawPanel() {
			setBackground(BACKGROUND);
		}
		

		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			super.paint(g);
			
			final Dimension screenSize = getSize();
			
			BasicStroke labelStroke = new BasicStroke(2.0f);
			BasicStroke gridStroke = new BasicStroke(0.2f);
			
			g2.setColor(LABEL_COLOR);
			
			g2.setStroke(labelStroke);
			g2.drawLine(AXIS_INDENT, 0, AXIS_INDENT, screenSize.height - AXIS_INDENT); // Y axis
			g2.drawLine(AXIS_INDENT, screenSize.height - AXIS_INDENT, screenSize.width, screenSize.height - AXIS_INDENT); // X axis
			
			// Y axis labels
			final double ystep = (screenSize.height - AXIS_INDENT) / (double)AXIS_LABELS;
			for (int i = 0; i < AXIS_LABELS; i++) {
				int y = (int)(screenSize.height - (AXIS_INDENT + i * ystep));
				g2.setStroke(labelStroke);
				g2.drawLine(AXIS_INDENT - AXIS_LABEL_SIZE, y, AXIS_INDENT, y);
				g2.drawString(String.valueOf(i * MAX_Y / AXIS_LABELS), 0, y + 6);
				g2.setStroke(gridStroke);
				g2.drawLine(AXIS_INDENT, y, screenSize.width, y);
			}
			
			// X axis labels
			final double xstep = (screenSize.width - AXIS_INDENT) / (double)AXIS_LABELS;
			for (int i = 0; i < AXIS_LABELS; i++) {
				int x = (int)(AXIS_INDENT + i * xstep);
				String value = String.valueOf(i * MAX_X / AXIS_LABELS);
				g2.setStroke(labelStroke);
				g2.drawLine(x, screenSize.height - (AXIS_INDENT - AXIS_LABEL_SIZE), x, screenSize.height - AXIS_INDENT);
				g2.drawString(value, x - (value.length() >>> 1) * 7, screenSize.height - 12);
				g2.setStroke(gridStroke);
				g2.drawLine(x, 0, x, screenSize.height - AXIS_INDENT);
			}
			
			
			g2.setStroke(new BasicStroke(1.0f));
			
			int color = 0;
			
			for (Population pop : pops) {
				int currentRank = 1;
				int j = 0;

				int bestCost = Integer.MAX_VALUE;
				int bestDist = Integer.MAX_VALUE;
				int worstCost = Integer.MIN_VALUE;
				int worstDist = Integer.MIN_VALUE;

				final ArrayList<Ptype> arr = pop.getPopulation();

				while (j < arr.size()) {
					ArrayList<Ptype> front = new ArrayList<>(20);

					for (; j < arr.size(); j++) {
						Ptype p = arr.get(j);
						if (p == null) {
							System.out.println("j = " + j);
							System.out.println("size = " + arr.size());
						}
						if (p.rank != currentRank) {
							currentRank = p.rank;
							break;
						}
						front.add(p);
					}

					java.util.Collections.sort(front, new Ptype.FrontComparator());
					ArrayList<Point> points = new ArrayList<>(front.size());

					// add points
					for (Ptype p : front) {
						int x = (int)((screenSize.width - AXIS_INDENT) * p.totDist / (double)MAX_X + AXIS_INDENT);
						int y = screenSize.height - (int)((screenSize.height - AXIS_INDENT) * p.totCost / (double)MAX_Y + AXIS_INDENT);
						points.add(new Point(x, y));

						bestCost = Math.min(bestCost, p.totCost);
						bestDist = Math.min(bestDist, p.totDist);
						worstCost = Math.max(worstCost, p.totCost);
						worstDist = Math.max(worstDist, p.totDist);
					}

					// draw lines between points
					g2.setColor(LABEL_COLOR);
					for (int i = points.size() - 1; i > 0; i--) {
						Point a = points.get(i);
						Point b = points.get(i - 1);
						g2.drawLine(a.x, a.y, b.x, b.y);
					}

					// draw points
					g2.setColor(COLORS[color++ % COLORS.length]);
					for (int i = 0; i < points.size(); i++) {
						Point p = points.get(i);
						plot(g2, p.x, p.y);
					}

					if (drawType == PARETO_FRONT) break;
				}

				if (pops.size() == 1) {
					int bestCostLine = screenSize.height - (int)((screenSize.height - AXIS_INDENT) * bestCost / (double)MAX_Y + AXIS_INDENT); // y
					int bestDistLine = (int)((screenSize.width - AXIS_INDENT) * bestDist / (double)MAX_X + AXIS_INDENT); // x

					int worstCostLine = screenSize.height - (int)((screenSize.height - AXIS_INDENT) * worstCost / (double)MAX_Y + AXIS_INDENT); // y
					int worstDistLine = (int)((screenSize.width - AXIS_INDENT) * worstDist / (double)MAX_X + AXIS_INDENT); // x

					g2.setStroke(new BasicStroke(1.0f));
					g2.setColor(new Color(0, 145, 19));

					g2.drawLine(AXIS_INDENT, bestCostLine, screenSize.width, bestCostLine);
					g2.drawLine(bestDistLine, 0, bestDistLine, screenSize.height - AXIS_INDENT);

					g2.drawString(String.valueOf(bestCost), AXIS_INDENT + 2, bestCostLine);
					g2.drawString(String.valueOf(bestDist), bestDistLine, screenSize.height - AXIS_INDENT - 2);

					g2.setColor(Color.RED);

					g2.drawLine(AXIS_INDENT, worstCostLine, screenSize.width, worstCostLine);
					g2.drawLine(worstDistLine, 0, worstDistLine, screenSize.height - AXIS_INDENT);

					g2.drawString(String.valueOf(worstCost), AXIS_INDENT + 2, worstCostLine);
					g2.drawString(String.valueOf(worstDist), worstDistLine, screenSize.height - AXIS_INDENT - 2);
				}
			}
			
			synchronized (lock) {
				lock.notifyAll();
			}
			
		}
		
		
		private void plot(Graphics2D g2, int x, int y) {
			g2.fillOval(x - PLOT_RAD, y - PLOT_RAD, PLOT_SIZE, PLOT_SIZE);
		}
	}
	
	
	public enum DrawType {
		POPULATION, PARETO_FRONT;
	}
	
}
