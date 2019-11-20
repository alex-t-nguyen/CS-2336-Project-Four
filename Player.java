// Alex Nguyen
// atn170001

public class Player{

    // Player stats
    private int hit;
    private int out;
    private int strikeout;
    private int walk;
    private int hitByPitch;
    private int sacrifice;
    private int error;
    private String name;
    private String team;

    /**
     * Default constructor of player
     */
    public Player()
    {
        hit = 0;
        out = 0;
        strikeout = 0;
        walk = 0;
        hitByPitch = 0;
        sacrifice = 0;
        error = 0;
        name = null;
        team = null;
    }

    /**
     * Constructor for specific player
     * @param h number of hits
     * @param o number of outs
     * @param k nmber of strikeouts
     * @param w number of walks
     * @param p number of hit by pitches
     * @param s number of sacrifices
     * @param n name of player
     */
    public Player(int h, int o, int k, int w, int p, int s, int e, String n, String t)
    {
        hit = h;
        out = o;
        strikeout = k;
        walk = w;
        hitByPitch = p;
        sacrifice = s;
        error = e;
        name = n;
        team = t;
    }
    
    /**
     * Returns number of hits
     * @return number of hits
     */
    public int getHits()
    {
    	return hit;
    }
    
    /**
     * Returns number of outs
     * @return number of outs
     */
    public int getOuts()
    {
    	return out;
    }
    
    /**
     * Returns number of strikeouts
     * @return number of strikeouts
     */
    public int getStrikeouts()
    {
    	return strikeout;
    }
    
    /**
     * Returns number of walks
     * @return number of walks
     */
    public int getWalks()
    {
    	return walk;
    }
    
    /**
     * Returns number of hit by pitches
     * @return number of hit by pitches
     */
    public int getHitByPitches()
    {
    	return hitByPitch;
    }
    
    /**
     * Returns number of sacrifices
     * @return number of sacrifices
     */
    public int getSacrifices()
    {
    	return sacrifice;
    }

    /**
     * Returns number of errors
     * @return number of errors
     */
    public int getErrors()
    {
        return error;
    }
    
    /**
     * Returns name of player
     * @return name of player
     */
    public String getName()
    {
    	return name;
    }

    /**
     * Returns team of player
     * @return team of player
     */
    public String getTeam()
    {
        return team;
    }
    
    /**
     * Sets the number of hits
     * @param h number of hits
     */
    public void setHits(int h)
    {
    	hit = h;
    }
    
    /**
     * Sets the number of outs
     * @param o number of outs
     */
    public void setOuts(int o)
    {
    	out = o;
    }
    
    /**
     * Sets the number of strikeouts
     * @param k number of strikeouts
     */
    public void setStrikeOuts(int k)
    {
    	strikeout = k;
    }
    
    /**
     * Sets the number of walks
     * @param w number of walks
     */
    public void setWalks(int w)
    {
    	walk = w;
    }
    
    /**
     * Sets the number of hit by pitches
     * @param p number of hit by pitches
     */
    public void setHitByPitches(int p)
    {
    	hitByPitch = p;
    }
    
    /**
     * Sets the number of sacrifices
     * @param s number of sacrifices
     */
    public void setSacrifices(int s)
    {
    	sacrifice = s;
    }

    /**
     * Sets the number of errors
     * @param e number of errors
     */
    public void setError(int e)
    {
        error = e;
    }

    /**
     * Sets the name of player
     * @param name name of player
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Sets the team of player
     * @param t team of player
     */
    public void setTeam(String t)
    {
        team = t;
    }
    
    /**
     * Calculates the number of at bats
     * @param hits number of hits 
     * @param out number of outs
     * @param strikeouts number of strikeouts
     * @param errors number of errors
     * @return the number of at bats of player
     */
    public int calculateNumAtBats(int hits, int out, int strikeouts, int errors)
    {
        return hits + out + strikeouts + errors;
    }
    
    /**
     * Calculates the batting average
     * @param h number of hits
     * @param numAtBats number of bats
     * @return the batting average of player
     */
    public double calculateBattingAverage(int h, int numAtBats)
    {
        if(numAtBats == 0)
            return 0.0;
        else
            return (double)h / numAtBats;
    }
    
    /**
     * Calcultes the number of plate appearances
     * @param hits number of hits
     * @param out number of outs
     * @param strikeouts number of strikeouts
     * @param walks number of walks
     * @param hitByPitch number of hit by pitches
     * @param sacrifice number of sacrifices
     * @return the number of plate appearances of player
     */
    public int calculatePlateAppearances(int hits, int out, int strikeouts, int walks, int hitByPitch, int sacrifice, int error)
    {
        return hits + out + strikeouts + walks + hitByPitch + sacrifice + error;
    }
    
    /**
     * Calculates the on base percentage
     * @param h number of hits
     * @param w number of walks
     * @param p number of hit by pitches
     * @param plateAppearances number of plate appearances     
     * @return the on base percentage of player
     */
    public double calculateOnBasePercentage(int h, int w, int p, int plateAppearances)
    {
        if(plateAppearances == 0)
            return 0.0;
        else
            return ((double)(h + w + p)) / plateAppearances;
    }

    /**
     * Overrided toString method to display player's data
     * @return player's data
     */
    @Override
    public String toString()
    {
        int numAtBats = this.calculateNumAtBats(this.getHits(), this.getOuts(), this.getStrikeouts(), this.getErrors());
        int numPlateAppearances = this.calculatePlateAppearances(this.getHits(), this.getOuts(), this.getStrikeouts(), this.getWalks(), this.getHitByPitches(), this.getSacrifices(), this.getErrors());
        return  this.getName() + "\t"
                + numAtBats + "\t"
                + this.getHits() + "\t"
                + this.getWalks() + "\t"
                + this.getStrikeouts() + "\t"
                + this.getHitByPitches() + "\t"
                + this.getSacrifices() + "\t"
                + String.format("%.3f", this.calculateBattingAverage(this.getHits(), numAtBats)) + "\t"
                + String.format("%.3f", this.calculateOnBasePercentage(this.getHits(), this.getWalks(), this.getHitByPitches(), numPlateAppearances))
                + "\n";
    }

    /**
     * Overloaded compareTo to compare names of players by inputing specific name
     * @param name name of player to compare to
     * @return number to determine if names are equal or not
     */
    public int compareTo(String name)
    {
        int result = this.getName().toLowerCase().compareTo(name.toLowerCase());
        if(result == 0)
        {
            return 0;
        }
        else
        {
            return result;
        }
    }
    
}
