/**
 * Name: Mark Brian Jacildo, Sam Welch
 * Date: Dec. 06, 2013
 * Purpose: This class handles the score file. It can return the scores, top scores, and even
 * 		add a new record to the file
 */

package memorygame;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class ReadAndWriteScores {

	//declare variables
	private Scanner input = null;
	private PrintWriter output = null;
	private final String outputScoreFile = "res/files/scoresBackup.dat";
	private final String inputScoreFile = "res/files/scores.dat";
	
	//this method returns the sorted list of scored from the input file. later to be trimmed in the game frame
	public ArrayList<Score> getTopScores()
	{
		ArrayList<Score> scoreList = new ArrayList<Score>();
		
		try {
			input = new Scanner(new FileReader(inputScoreFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (input.hasNext()) {
			String[] nameAndScore = input.next().split("=");
			String name = nameAndScore[0];
			int score = Integer.parseInt(nameAndScore[1]);
			scoreList.add(new Score(name, score));
		}
		
		//sorts the list from lowest to highest
		Collections.sort(scoreList, new Comparator<Score>()
				{

					@Override
					public int compare(Score score1, Score score2) {
						return score2.getScore() - score1.getScore();
					}
			
				});		
		
		
		return scoreList;
	}
	
	//add score to the file. gets all the scores from the input file, and save it as a string, then add the new score and save it to the output file
	public void addScore(Score score)
	{
		
		try {
			input = new Scanner(new FileReader(inputScoreFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			output = new PrintWriter(new FileWriter(outputScoreFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String allScores = "";
		while (input.hasNext()) {
			String name = input.next();
			allScores = allScores + " " +  name;
		}
		
		
		allScores = allScores + " " + score.getName() + "=" + score.getScore();
		
		output.print(allScores);
		input.close();
		output.close();
		
		backupScores();

	}
	
	//backs up the score. in this case, since output file is where the new score is added, the output file will overwrite the input file
	private void backupScores()
	{
		try {
			input = new Scanner(new FileReader(outputScoreFile));
		} catch (FileNotFoundException e) {
			System.err
					.println("Input file " + outputScoreFile + " is not found.");
			// closeFiles();
			input.close();
			System.exit(1);
		}// of catch

		try {
			output = new PrintWriter(new FileWriter(inputScoreFile));
		} catch (IOException e) {
			System.err.println("Output file " + inputScoreFile
					+ " cannot be created.");
			output.close();
			System.exit(1);
		}
		
		String allScores = "";
		while (input.hasNext()) {
			String name = input.next();
			allScores = allScores + " " +  name;
		}
		
		output.print(allScores);
		input.close();
		output.close();
		
	}
	
}
