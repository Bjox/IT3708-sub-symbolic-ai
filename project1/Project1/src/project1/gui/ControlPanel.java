package project1.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Locale;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSlider;
import javax.swing.JWindow;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import project1.Boid;
import project1.Main;
import project1.Predator;
import project1.Vector2D;
import project1.sim.Simulation;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class ControlPanel extends JDialog {

	public ControlPanel() throws HeadlessException {
		setTitle("Control panel");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);
		
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		
		JPanel sliderPanel = new JPanel();
		
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		
		DoubleSlider sepSlider = new DoubleSlider(0.0, 5.0, Boid.separationWeight, "Separation weight:");
		DoubleSlider alignSlider = new DoubleSlider(0.0, 0.1, Boid.alignmentWeight, "Alignment weight:");
		DoubleSlider cohSlider = new DoubleSlider(0.0, 0.05, Boid.cohesionWeight, "Cohesion weight:");
		DoubleSlider avoidSlider = new DoubleSlider(0.0, 15.0, Boid.avoidanceWeight, "Avoidance weight:");
		Slider dtSlider = new Slider(100_000, 20_000_000, Simulation.DEFAULT_DELTA_TIME, "Simulation timestep:") {
			@Override
			public String createLabelStr() {
				return "  " + String.format(Locale.ENGLISH, "%.1f", slider.getValue() / 1_000_000.0) + " ms";
			}
		};
		dtSlider.addChangeListener(e -> Simulation.DELTA_TIME = dtSlider.getValue());
		DoubleSlider simSpeedSlider = new DoubleSlider(0.0, 2.0, 1.0, "Simulation speed:", 40);
		
		sepSlider.addChangeListener(e -> Boid.separationWeight = sepSlider.getDoubleValue());
		alignSlider.addChangeListener(e -> Boid.alignmentWeight = alignSlider.getDoubleValue());
		cohSlider.addChangeListener(e -> Boid.cohesionWeight = cohSlider.getDoubleValue());
		avoidSlider.addChangeListener(e -> Boid.avoidanceWeight = avoidSlider.getDoubleValue());
		simSpeedSlider.addChangeListener(e -> Main.instance.sim.simSpeed = simSpeedSlider.getDoubleValue());
		
		JPanel buttPanel = new JPanel();
		buttPanel.setLayout(new GridLayout(7, 1, 0, 3));
		
		final JButton startStopButt = new JButton("Stop simulation");
		startStopButt.addMouseListener(new SimpleMouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (Main.instance.sim.isPaused()) {
					startStopButt.setText("Stop simulation");
					Main.instance.sim.resume();
				} else {
					startStopButt.setText("Start simulation");
					Main.instance.sim.pause();
				}
			}
		});
		
		JButton debugButt = new JButton("Toggle debug");
		debugButt.addMouseListener(new SimpleMouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Main.debug = !Main.debug;
			}
		});
		
		JButton spawnBoidButt = new JButton("Spawn 25 boids");
		spawnBoidButt.addMouseListener(new SimpleMouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Main.boidsToSpawn = 25;
			}
		});
		spawnBoidButt.setForeground(new Boid(new Vector2D()).color());
		
		JButton spawnPredButt = new JButton("Spawn predator");
		spawnPredButt.addMouseListener(new SimpleMouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Main.predatorsToSpawn = 1;
			}
		});
		spawnPredButt.setForeground(new Predator(new Vector2D()).color());
		
		JButton removeBoidsButt = new JButton("Remove boids");
		removeBoidsButt.addMouseListener(new SimpleMouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Main.removeBoids = true;
			}
		});
		
		JButton removePredButt = new JButton("Remove predators");
		removePredButt.addMouseListener(new SimpleMouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Main.removePredators = true;
			}
		});
		
		JButton removeObsButt = new JButton("Remove obstacles");
		removeObsButt.addMouseListener(new SimpleMouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Main.removeObs = true;
			}
		});
		
		JPanel separator = new JPanel();
		separator.setPreferredSize(new Dimension(0, 15));
		
		sliderPanel.add(sepSlider);
		sliderPanel.add(alignSlider);
		sliderPanel.add(cohSlider);
		sliderPanel.add(avoidSlider);
		sliderPanel.add(dtSlider);
		sliderPanel.add(simSpeedSlider);
		sliderPanel.add(separator);
		
		buttPanel.add(startStopButt);
		buttPanel.add(debugButt);
		buttPanel.add(spawnBoidButt);
		buttPanel.add(spawnPredButt);
		buttPanel.add(removeBoidsButt);
		buttPanel.add(removePredButt);
		buttPanel.add(removeObsButt);
		
		content.add(sliderPanel);
		content.add(buttPanel);
		
		add(content);
		pack();
	}

	
	
}


class Slider extends JPanel {
	protected JSlider slider = new JSlider();
	protected JLabel v_label = new JLabel();
	protected JLabel label = new JLabel();

	public Slider(int min, int max, int start, String label) {
		this.label.setText(label);
		slider.setMinimum(min);
		slider.setMaximum(max);
		slider.setValue(start);
		
		setLayout(new BorderLayout());
		add(slider, BorderLayout.WEST);
		add(v_label, BorderLayout.EAST);
		add(this.label, BorderLayout.NORTH);
		
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				v_label.setText(createLabelStr());
			}
		});
		
		slider.setPreferredSize(new Dimension(300, slider.getHeight()));
		v_label.setText(createLabelStr());
	}
	
	public void addChangeListener(ChangeListener l) {
		slider.addChangeListener(l);
	}
	
	public int getValue() {
		return slider.getValue();
	}
	
	public String createLabelStr() {
		return String.valueOf(slider.getValue());
	}
	
}



class DoubleSlider extends Slider {

	private final double min, max;

	public DoubleSlider(double min, double max, double start, String label) {
		this(min, max, start, label, 1000);
	}
	
	public DoubleSlider(double min, double max, double start, String label, int steps) {
		super(0, steps, steps / 2, label);
		this.min = min;
		this.max = max;
		
		double r = (start - min) / (max - min);
		slider.setValue((int)((slider.getMaximum() - slider.getMinimum()) * r + slider.getMinimum()));
		
		v_label.setText(createLabelStr());
	}
	
	public double getDoubleValue() {
		double r = slider.getValue() / (double)slider.getMaximum();
		double v = (max - min) * r + min;
		return v;
	}
	
	@Override
	public String createLabelStr() {
		return String.format(Locale.ENGLISH, "%.3f", getDoubleValue());
	}
	
}


abstract class SimpleMouseListener implements MouseListener {

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
}