/**
 * Name: Mark Brian Jacildo, Sam Welch
 * Date: Dec. 06, 2013
 * Purpose: This is a class that could hold a name-score pair when made into an object;
 */
package memorygame;

public class Score {

	//declare variables
	private String name;
	private int score;
	
	public Score(String name, int score)
	{
		//set the score and names
		this.name = name;
		this.score = score;
	}
	
	//gets the name of the score owner
	public String getName()
	{
		return name;
	}
	
	//gets the score of the score owner
	public int getScore()
	{
		return score;
	}
	
}
