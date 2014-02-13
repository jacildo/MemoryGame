/**
 * Name: Mark Brian Jacildo, Sam Welch
 * Date: Dec. 05, 2013
 * Purpose: This is a class that extends a JButton just to have the basic methods of a button and the nice hover animation. This class stores a card path in the constructor
 * 		which will be used to reveal the card that it represents.
 */

package memorygame;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

public class Card extends JButton{

	//declarations
	private boolean isHidden;
	private boolean isFaceDown;
	private int cardIndex;
	private ImageIcon cardIcon;
	private ImageIcon cardBackIcon = new ImageIcon("res/img/cardback.png");
	private String path;
	
	public Card(String path)
	{
		//set the button size to the same dimension as the card pictures
		setPreferredSize(new Dimension(65, 87));
		
		//set the default status of the card to face down and not hidden
		isFaceDown = true;
		isHidden = false;
		//save the image icon to be used whenever the card is flipped
		cardIcon = new ImageIcon(path);
		
		//set the pathname for comparison in the GameFrame class
		this.path = path;
		setIcon(cardBackIcon);
	}
	
	//flip the card, set facedown to either true or false, depending on the last status, then set the icon based on it's new status
	public void flipCard()
	{
		if(!isHidden)
		{
			isFaceDown = (isFaceDown) ? false : true;
			setIcon((isFaceDown) ? cardBackIcon : cardIcon);
		}	
	}
	
	//returns the indes (for object comparison)
	public int getIndex()
	{
		return cardIndex;
	}
	
	//hides the card
	public void hide()
	{
		isHidden = true;
		setIcon(null);
	}
	
	//set the index (when an object is stored in an array, this will be useful)
	public void setIndex(int index)
	{
		cardIndex = index;
	}
	
	//returns the path
	public String getPath()
	{		
		return path;
	}
	
	//check if card is hidden or not
	public boolean isHidden()
	{
		return isHidden;
	}
	
	//shhh. nothing to see here
	public void cheat()
	{
		if(!isHidden)
		{
			cardBackIcon = cardIcon;
			setIcon(cardIcon);
		}
	}
}
