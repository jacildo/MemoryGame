/**
 * Name: Mark Brian Jacildo, Sam Welch
 * Date: Dec 05, 2013
 * Purpose: This is the tester class. it creates a game frame, sets the size, where it will show up, and set it to visible. 
 */

package memorygame;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class Game {

	public static void main(String[] args) {
		
		GameFrame gameFrame = new GameFrame();
		
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setSize(500, 450);
		gameFrame.setResizable(false);
//		gameFrame.pack();
		
		//I think this is a more reliable way to center the frame on the screen. When I try relative to null, it renders the frame at the bottom right of my screen.
		//The origin point is the top left edge of the frame, so I set the height and width to be half of  the difference between the height and width of my screen resolution and the
		// height and width of the frame
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int locationX = (int) ((screenSize.getWidth() - gameFrame.getWidth())/2); //casted to int, because somehow the screen size returns double type values
		int locationY = (int) ((screenSize.getHeight() - gameFrame.getHeight())/2);
		gameFrame.setLocation(locationX, locationY);
		
		gameFrame.setVisible(true);


		
//		while(true)
//		{
//			
//			gameFrame.repaint();
//		}

	}

}
