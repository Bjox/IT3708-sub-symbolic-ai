package project1.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import project1.Boid;
import project1.Ent;
import project1.Main;
import project1.Obstacle;
import project1.Vector2D;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public final class Frame extends JFrame {
	
	private final DrawPanel drawPanel;
	private ArrayList<Ent> ents;
	
	private Point mp_point;
	private Point md_point;

	public Frame(int w, int h) throws HeadlessException {
		ents = new ArrayList<>();
		
		setTitle("Boids");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		drawPanel = new DrawPanel();
		drawPanel.setPreferredSize(new Dimension(w, h));
		add(drawPanel);
		
		drawPanel.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				mp_point = e.getPoint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (mp_point != null) {
					Point mr_point = e.getPoint();
					double radius = Point.distance(mr_point.x, mr_point.y, mp_point.x, mp_point.y);
					
					if (radius > 5) {
						Main.obstacleToAdd = new Obstacle(mp_point.x, mp_point.y, (int)radius);
					}
					
					mp_point = null;
				}
				md_point = null;
			}

			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
		drawPanel.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				md_point = e.getPoint();
			}

			@Override
			public void mouseMoved(MouseEvent e) {}
		});
		
		pack();
		setLocationRelativeTo(null);
	}
	
	
	public void drawEnts(ArrayList<Ent> ents) {
		this.ents = ents;
		drawPanel.repaint();
	}
	
	
	class DrawPanel extends JPanel {

		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			if (!Main.debug) g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			super.paint(g2);
			
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, getWidth(), getHeight());
			
			for (int i = 0; i < ents.size(); i++) {
				paintBoid(g2, ents.get(i));
			}
			
			if (mp_point != null && md_point != null) {
				double radius = Point.distance(mp_point.x, mp_point.y, md_point.x, md_point.y);
				g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f, new float[] {4.0f}, 0.0f));
				g2.setColor(Color.GRAY);
				g2.drawOval(mp_point.x - (int)radius, mp_point.y - (int)radius, (int)(radius*2), (int)(radius*2));
			}
		}
		
		public void paintBoid(Graphics2D g2, Ent ent) {
			int e_x = (int)ent.getPos().x;
			int e_y = (int)ent.getPos().y;
			int e_r = ent.radius();
			
			int x = e_x - e_r;
			int y = e_y - e_r;
			
			g2.setColor(ent.color());
			g2.fillOval(x, y, e_r * 2, e_r * 2);
			
			if (!(ent instanceof Obstacle)) {
				int l_x = (int)(Math.cos(ent.getAlign()) * e_r * 1.3 + e_x);
				int l_y = (int)(Math.sin(ent.getAlign()) * e_r * 1.3 + e_y);

				g2.setColor(Color.BLACK);
				g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
				g2.drawLine(e_x, e_y, l_x, l_y);
			}
			
			if (Main.debug && (ent instanceof Boid)) {
				g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
				
				g2.setColor(Color.LIGHT_GRAY);
				g2.drawOval(e_x - ent.localRadius, e_y - ent.localRadius, ent.localRadius * 2, ent.localRadius * 2);
				
				g2.setColor(Color.GREEN);
				drawVector(g2, ent.getVel().scaled(0.5), ent.getPos());
				
				g2.setColor(Color.RED);
				drawVector(g2, ent.sep.scaled(50), ent.getPos());
				
				g2.setColor(Color.BLUE);
				drawVector(g2, ent.coh.scaled(50), ent.getPos());
				
				g2.setColor(Color.MAGENTA);
				drawVector(g2, ent.align.scaled(10), ent.getPos());
			}
			
			
			
			
		}
		
		private void drawVector(Graphics2D g2, Vector2D v, Vector2D p) {
			g2.drawLine((int)p.x, (int)p.y, (int)(p.x + v.x), (int)(p.y + v.y));
		}
		
	}
	
}
