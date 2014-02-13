//THIS CLASS IS UNUSED, BUT IT ALWAYS SHOWS UP EVEN IF I DELETE IT
// PLEASE IGNORE

package memorygame;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class CardIcon extends JLabel{
	//declare variables
	private boolean isFlipped = true;
	private BufferedImage cardBackImage;

	
	
	public CardIcon(ImageIcon icon)	
	{
		try
		{
			 cardBackImage = ImageIO.read(new File("res/img/card_2d.png"));
		}
		catch (IOException ex) {
		}
		
//		setImage(cardBackImage);
	}
	
	private void flipCard()
	{
		isFlipped = (isFlipped == true) ? false : true;
	}
	
	private class MouseHandler implements MouseListener
	{

		@Override
		public void mouseClicked(MouseEvent arg0) {
			flipCard();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			
		}
		
	}
}
