/**
 * Name: Mark Brian Jacildo, Sam Welch
 * Date: Dec. 05, 2013
 * Purpose: create the game frame, components of the game frame, timers, panels, buttons and instances of cards to be displayed in the center of the frame
 */

package memorygame;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GameFrame extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//declarations
	private final int MAX_CHOSEN_CARDS = 8;
	private final int MAX_DISPLAYED_CARDS = MAX_CHOSEN_CARDS * 2; //the card array will be filled with 8 pairs of cards
	private final int AVAILABLE_CARDS = 52;
	private final int CARD_NUMBERS = 13;
	private final int BONUS_TIME = 10;
	private final int STARTING_TIME = 60;
	private final int BUFFER_SIZE = 128000;
	private final int MAX_SHOWED_SCORES = 10;	
	private int timeLeft;
	private int score;
	
	private JTextField timerTextField = new JTextField();
	private JTextField messageTextField = new JTextField();
	private JTextField scoreTextField = new JTextField();
	private JButton playAgainButton;
	private JButton exitButton;
	private JButton cheatButton;
	
	//the flipped cards
	private Card firstFlippedCard;
	private Card secondFlippedCard;
	
	//constraints for the grid bag layout, and the grid bag layout
	GridBagConstraints constraint = new GridBagConstraints();
	GridBagLayout gridbag = new GridBagLayout();
	
	Container gamePane;
	JPanel cardPanel, topPanel, bottomPanel;
	Card[] cardArray = new Card[MAX_DISPLAYED_CARDS];
	String[] cardPath = new String[AVAILABLE_CARDS]; 
	
	private TimerThread timerThread;
	private boolean timerRunning = false;
	
	//declare variables for audio

	private File soundFile;
	private AudioFormat audioFormat;
	private AudioInputStream audioStream;
	private SourceDataLine sourceLine;
	
	
	public GameFrame()
	{
		
		setLayout(new BorderLayout());

		//set the content pane as the game pane and set it's layout using a grid bag layout
		gamePane = getContentPane();
		gamePane.setLayout(gridbag);
		
		//create the panel that contains the 16 cards in a 4x4 dimension
		cardPanel = new JPanel();
		cardPanel.setLayout(new GridLayout(4,4));

		//create the panel that contains the score board and timer, and the panel that shows the start and reset game buttons
		topPanel = new JPanel();
		bottomPanel = new JPanel();
		
		//create the top panel items		

		timeLeft = STARTING_TIME; //initialize the time left to 60 seconds
		timerTextField.setText("Time Left: " + timeLeft); 
		timerTextField.setPreferredSize(new Dimension(100, 20));
		
		
		messageTextField.setText("Pick a card, any card"); //set the beginning message on the message text field
		messageTextField.setPreferredSize(new Dimension(150, 20));

		
		score = 0; //initialize score to 0
		scoreTextField.setText("Score: " + score);
		
		//set the items to not editable
		timerTextField.setEditable(false);
		scoreTextField.setEditable(false);
		messageTextField.setEditable(false);
		
		//add the items to the top panel
		constraint.weightx = 1.0;
		constraint.weighty = 1.5;
		add(timerTextField, constraint);
		add(messageTextField, constraint);
		constraint.gridwidth = GridBagConstraints.REMAINDER;
		add(scoreTextField, constraint);
		
		
		//MIDDLE AREA
		add(cardPanel, constraint);
		

		//BOTTOM AREA
		playAgainButton = new JButton("PlayAgain");
		exitButton = new JButton("Exit");
		cheatButton = new JButton("Cheat");
		
		playAgainButton.setEnabled(false); //set it to disabled initially
		
		//exits the program when the exit button is clicked
		exitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent exit) {
				System.exit(0);
			}
		});
		
		//resets the game when the play again button is pressed
		playAgainButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent reset) {
				resetGame();
			}
		});

		//the cheat button would reveal all cards, but you still would have to play the game
		cheatButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cheat();
			}
		});
		
		bottomPanel.add(playAgainButton);
		bottomPanel.add(exitButton);
		bottomPanel.add(cheatButton);
		
		gridbag.setConstraints(bottomPanel, constraint);
		add(bottomPanel);
		//END AREA
		
		
		//populate the cardPaths array list of paths of the card images
		getCardPaths();
		
	    resetCards();
	    
	}
	
	//just a lazy way of storing the card paths into an array list. For every card number, concatenate 4 different suits to the base path, then store it in the array
	private void getCardPaths()
	{
		int suitsPerCard = 4;
		String basePath = "res/img/card_";
		for(int num = 0; num < CARD_NUMBERS; num++)
		{
			
			cardPath[(num*suitsPerCard)] = basePath + (num + 1) + "c.png";
			cardPath[(num*suitsPerCard) + 1] = basePath + (num + 1) + "s.png";
			cardPath[(num*suitsPerCard) + 2] = basePath + (num + 1) + "h.png";
			cardPath[(num*suitsPerCard) + 3] = basePath + (num + 1) + "d.png";
		}
		
		//tests the above codes
//		for(int i = 0; i < 52; i++)
//		{
//			System.out.println((i + 1) + " " + cardPath[i]);
//		}
	}
	
	
//	//empties the card array
//	private void clearCardArray()
//	{
//		Arrays.fill(cardArray, null);
//		
//	}
	
	//fills the card array with card objects randomly
	private void fillCardArray()
	{
		//declares a random int generator and an temporary array list of strings to store the chosen paths
		Random randomInt = new Random();
		ArrayList<String> chosenPaths = new ArrayList<String>();
		
		//selects non repeating paths and store them to the list of chosen paths until it reaches the max chosen cards, which is 16
		while(chosenPaths.size() < MAX_CHOSEN_CARDS)
		{
			int randomNumber = randomInt.nextInt(AVAILABLE_CARDS);
			String chosenPath = cardPath[randomNumber];
			boolean isInArray = false;
			
			//loop through the chosen paths and see if the chosen path is already in it
			for(Iterator<String> i = chosenPaths.iterator(); i.hasNext(); ) {
			    String item = i.next();
			    if(chosenPath.equals(item))
			    	isInArray = true;
			}

			//if the chosen path is not in the array of chosen paths, include it
			if(!isInArray)
				chosenPaths.add(chosenPath);
		}
		
		//tests if the above method works
//		for(Iterator<String> i = chosenPaths.iterator(); i.hasNext(); ) {
//		    String item = i.next();
//		    System.out.println(item);
//		}
		
		//make another temporary list to store the random positions of the pair or cards.
		String[] displayedPaths = new String[MAX_DISPLAYED_CARDS];
		
		//for every chosen path, store them twice in the displayed path array randomly
		for(Iterator<String> i = chosenPaths.iterator(); i.hasNext(); ) {
		    String item = i.next();

		    int timesStored = 0;
			while(timesStored < 2)
			{
				int randomNumber = randomInt.nextInt(MAX_DISPLAYED_CARDS);	
				if(displayedPaths[randomNumber] == null)
				{
					displayedPaths[randomNumber] = item;
					timesStored ++;
				}
			}		    
		}	
		//test if displayed paths is working
//		for(int i = 0; i < displayedPaths.length; i++) {
//	    String item = displayedPaths[i];
//	    System.out.println(item);
//		}
		
		//use all paths to create abstract card objects
		for(int i = 0; i < displayedPaths.length; i++) 
		{
			String item = displayedPaths[i];
			cardArray[i] = new Card(item);
		}		
	}
	
	//adds the cards to the card panel
	private void displayCards()
	{
		//add 16 cards to the cardPanel
		for(int i = 0; i < cardArray.length; i++)
		{
			cardPanel.add(cardArray[i]);
			//adds the action listener to the card
			cardArray[i].addActionListener(this);
			//set the index (the position of the card in the panel) of the card
			cardArray[i].setIndex(i);
		}
	}
	
	//dont tell the teacher
	private void cheat()
	{
		for(Card card : cardArray)
		{
			card.cheat();
		}
	}
	
	//self explanatory
	private void startGame()
	{
	    //set the timer that runs on the background depending on the situation (see TimerThread class at the bottom of this page)
	    timerThread = new TimerThread();
	    timerThread.setDaemon(true);
	    timerThread.start();
	    timerRunning = true;
	    
	    //enable the play again button
	    playAgainButton.setEnabled(true);
	}
	
	//resets the game
	private void resetGame()
	{
		//reset the cards
		resetCards();
		
		//reset the score
		score = 0;
		scoreTextField.setText("Score: " + score);
		
		//disable the play again button until the game is started
		playAgainButton.setEnabled(false);
		
		//reset the message to the original one
		setMessage("Pick a card, any card");
		
		//set timer running back to false
		timerRunning = false;
		
		//remove the timer and reset the clock
		timerThread.waitOneSec(); //let the timer settle first before resetting. The remainder time of the thread's tickTimer method is still active since it's daemon is set to true
		timeLeft = STARTING_TIME;
		timerTextField.setText("Time Left: " + timeLeft);
	}
	
	//this method adds a point to the score then sets the new score in the score text field. also shows a message if a pair is matched
	private void awardPoint()
	{
		score ++;
		scoreTextField.setText("Score: " + score);
		setMessage("Right!");
		

		
		//everytime all pairs have been found, add bonus time and reset the cards
		if(score % MAX_CHOSEN_CARDS == 0)
		{
			timeLeft += BONUS_TIME;
			timerTextField.setText("Time Left: " + timeLeft);
			
			//play a sound for time extension
			playSound("res/sounds/extension.wav");
			
			resetCards();
		}
		else
		{
			//play a sound for normal award
			playSound("res/sounds/point.wav");
		}
	}
	
	//remove all cards in the panel, clear the card array, fill the card array with random cards again, then display the new cards
	private void resetCards()
	{
		cardPanel.removeAll();
//		clearCardArray();
		fillCardArray();
		displayCards();
		cardPanel.revalidate(); //1 hour. 1 hour trying to figure this out. 1 hour... this updates the size and layout of the component. without this, there's only a blank panel
		cardPanel.repaint();
		firstFlippedCard = null;
	}
	
	//ends the game
	private void endGame()
	{
		//play a sound for end game
		playSound("res/sounds/game_end.wav");
		
		//show the player's score and ask if the player wants to register the score
		String name = JOptionPane.showInputDialog(null, "Your score is: " + score + ". Enter your name.");

		//if the player saved the score, add the score to the scores file
		if(name != null)
		{
			ReadAndWriteScores scoreWriter = new ReadAndWriteScores();
			scoreWriter.addScore(new Score(name, score));
		}
		
		getScores();
		resetGame();
	}
	
	//set the message box. it's called everywhere. this helps reduce typing.
	private void setMessage(String message)
	{
		messageTextField.setText(message);
	}
	
	//plays a sound
	private void playSound(String path)
	{
		PlaySound sound = new PlaySound(path);
		sound.start();
	}
	
	//
	public void actionPerformed(ActionEvent e)
	{
		if(!timerRunning) startGame(); //if the timer is not running, start the timer on the first flip
		
		//if the player mismatched two cards previously, flip them back then null the first and second flipped card variables
		if(firstFlippedCard != null && secondFlippedCard != null)
		{
			firstFlippedCard.flipCard();
			secondFlippedCard.flipCard();
			firstFlippedCard = null;
			secondFlippedCard = null;
		}
		
	    //save the card in a temporary variable then flip the card
		Card card = (Card)e.getSource();
		card.flipCard();

		
		//check if the card was already hidden, to avoid counting hidden cards to be counted
		if(!card.isHidden())
		{
			//check if there is no card flipped previously
			if(firstFlippedCard != null)
			{
				//if the card clicked is the flipped card, empty the flipped card variable
				if(card.getIndex() == firstFlippedCard.getIndex())
				{
			 		firstFlippedCard = null;
			 		setMessage("Pick a card, any card");
				}
				//if not, check the second flipped card if it matches the first
				else
				{
					//if both cards are equal, then hide the cards then award a point. also remove the first flipped card previously saved
					if(firstFlippedCard.getPath().equals(card.getPath()))
					{					
						cardArray[firstFlippedCard.getIndex()].hide();
						card.hide();
						firstFlippedCard = null;
						awardPoint();
					}
					//if not, save the second flipped card, then set the message and save the second flipped card to let the player view it until he choses another card
					else
					{
//						cardArray[firstFlippedCard.getIndex()].flipCard();
//						card.flipCard();
						secondFlippedCard = card;
						setMessage("Wrong! Pick again...");
						playSound("res/sounds/meep_merp.wav"); //plays the meep merp sound effect when there's a mismatch
					}
					
					//then empty the flipped card variable
//					firstFlippedCard = null;
				}
			}
			else
			{
				//play a flip card sound effect for the first card flipped
				playSound("res/sounds/card_flip.wav");
				firstFlippedCard = card;
				setMessage("Now pick another card");
			}
		}
		
	}
	
	//internal class timer thread. runs on the background, checking variables while running
	class TimerThread extends Thread
	{
		public void run()
		{
			//this happens in the background. ticks the timer while there is time left and timerRunning is true
			while(timerRunning && timeLeft != 0)
			{
				try {
					tickTimer();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		//tick the timer. subtract a second from the time left, set the time left 
		private void tickTimer() throws InterruptedException
		{
			Thread.sleep(1000);						
			timeLeft --;
			timerTextField.setText("Time Left: " + timeLeft);
			if(timeLeft <= 5)
			{
				if(timeLeft == 0) endGame(); 
				else playSound("res/sounds/tick.wav"); 				//play a tick sound if there's less than 5 seconds but ends game if it's zero
				
			}
		}
		
		//waits a second
		private void waitOneSec()
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}	
	
	//internal class. plays a sound file by providing a file path. 
	//but I know how it works.
	class PlaySound extends Thread
	{
        String strFilename;
		public PlaySound(String filename)
		{
			strFilename = filename;
		}
		public void run()
		{
			
			//given a file name/path, the following steps are the necessary steps to play a sound.

	        try {
	            soundFile = new File(strFilename);
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.exit(1);
	        }

	        try {
	            audioStream = AudioSystem.getAudioInputStream(soundFile);
	        } catch (Exception e){
	            e.printStackTrace();
	            System.exit(1);
	        }

	        audioFormat = audioStream.getFormat();

	        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	        try {
	            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
	            sourceLine.open(audioFormat);
	        } catch (LineUnavailableException e) {
	            e.printStackTrace();
	            System.exit(1);
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.exit(1);
	        }

	        sourceLine.start();

	        int nBytesRead = 0;
	        byte[] abData = new byte[BUFFER_SIZE];
	        while (nBytesRead != -1) {
	            try {
	                nBytesRead = audioStream.read(abData, 0, abData.length);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	            if (nBytesRead >= 0) {
	                @SuppressWarnings("unused")
	                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
	            }
	        }

	        sourceLine.drain();
	        sourceLine.close();
		}

	}
	
	//this displays the top scores in a dialog box
	private void getScores()
	{
		//read the scores from the score file
		ReadAndWriteScores score = new ReadAndWriteScores();
		ArrayList<Score> scores = score.getTopScores();
		
		//loop through the list and show the top ten scores
		Iterator<Score> iterator = scores.iterator();
		int scoresDisplayed = 0;
		String message = "TOP SCORES: \n ";
		
		while(iterator.hasNext() && scoresDisplayed < MAX_SHOWED_SCORES)
		{
			Score nextScore = iterator.next();
			message = message + nextScore.getName() + " . . . " + nextScore.getScore() + " \n ";
			scoresDisplayed++;			
		}
		
		JOptionPane.showMessageDialog(null, message);
	}

}
