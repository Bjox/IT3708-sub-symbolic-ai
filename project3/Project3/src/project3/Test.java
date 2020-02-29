package project3;

import project3.flatland.World;
import project3.gui.MainFrame;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Test {

	
	public static void main(String[] args) throws Exception {
		
		World world = new World(0.33, 0.33, 123);
		world.getAgent().x = 5;
		world.getAgent().y = 5;
		MainFrame frame = new MainFrame(world);
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		while (true) {
			world.moveLocal(Util.randomInt(0, 3));
			frame.update();
			
			Thread.sleep(250);
		}
		
	}

}

