// Alex Nguyen
// atn170001

import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class Main {
    static final int NUM_LEADER_STATS = 6;
    static final double THRESHOLD = 0.00000001; // Threshold value to compare doubles
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Scanner input = new Scanner(System.in);
        Scanner fileReader = null;
        Scanner keyFile = null;
        String filename = input.nextLine();
        try
        {
            fileReader = new Scanner(new File(filename));   // Open player stats file
            keyFile = new Scanner(new File("keyfile.txt")); // Open keys file
        }
        catch(FileNotFoundException ex)
        {
            input.close();
            throw ex;
        }

        String header = null;
        HashMap<Integer, Character> hashMapStats = new HashMap<>(); // Hash map for player's plays
        HashMap<Integer, Player> hashMapPlayers = new HashMap<>();  // Hash map for players

        // Reads in keys to set up hash map
        while(keyFile.hasNext())
        {
            String line = keyFile.nextLine();
            if(line.contains("#"))  // If line is a header line
            {
                header = line.substring(line.indexOf(' ') + 1, line.length() - 3);  // Get stat header in line
                continue;
            }
            if(line.contains("\n"))  // If line is blank (line before new header)
                continue;   // Skip the line
            switch(header)
            {
                case "OUTS":    // If key is under OUTS header
                    {
                        int key = line.hashCode();
                        hashMapStats.put(key, 'O');     // Set value for key as Out    
                        break;
                    }  
                case "STRIKEOUT":   // If key is under STRIKEOUT header
                    {
                        int key = line.hashCode();
                        hashMapStats.put(key, 'K');     // Set value for key as Strikeout    
                        break;
                    } 
                case "HITS":    // If key is under HITS header
                    {
                        int key = line.hashCode();
                        hashMapStats.put(key, 'H');     // Set value for key as Hit    
                        break;
                    }   
                case "WALK":    // If key is under WALK header
                    {
                        int key = line.hashCode();
                        hashMapStats.put(key, 'W');     // Set value for key as Walk
                        break;
                    }   
                case "SACRIFICE":   // If key is under SACRIFICE header
                    {
                        int key = line.hashCode();
                        hashMapStats.put(key, 'S');     // Set value for key as Sacrifice 
                        break;
                    }
                case "HIT BY PITCH":    // If key is under HIT BY PITCH header
                    {
                        int key = line.hashCode();
                        hashMapStats.put(key, 'P');     // Set value for key as Hit By Pitch
                        break;
                    }
                case "ERRORS":  // If key is under ERRORS header
                    {
                        int key = line.hashCode();
                        hashMapStats.put(key, 'E');    // Set value for key as Error
                        break;
                    } 
                default:    break;
            }
        }

        // Reads in player-stats file
        while(fileReader.hasNextLine())
        {
            String line = fileReader.nextLine();
            String team = line.substring(0, line.indexOf(' ')); // Team name is 1 character at beginning of string
            int spaceCounter = 0;
            int index = 0;
            while(spaceCounter < 2) // Find index of player's play
            {
                if(line.charAt(index) == ' ')
                    spaceCounter++;
                index++;    // index of line
            }
            String name = line.substring(2, index - 1); // Player name is always 2nd index of line
            String stat = line.substring(index, line.length()); // String containing player's play/stat
            int statKey = stat.hashCode();  // hash player's stat
            int playerKey = name.hashCode();    // hash player's name

            char playerStat = hashMapStats.get(statKey);    // Gets correct stat
            Player player;
            boolean multipleEntries = false;    // Flag for if player is already in hash map

            if(hashMapPlayers.get(playerKey) == null)   // If player is not already in hash map
            {   
                player = new Player();   // Create new player
                player.setName(name);   // Set player's name
                player.setTeam(team);   // Set player's team
            }
            else    // If player is already in hash map
            {
                player = hashMapPlayers.get(playerKey);
                multipleEntries = true;
            }

            switch(playerStat)  // Set player's stat
            {
                case 'H': // If character is H increase hits by 1
                    player.setHits(player.getHits() + 1);
                    break;
                case 'O': // If character is O increase outs by 1
                    player.setOuts(player.getOuts() + 1);
                    break;
                case 'K': // If character is K increase strikeouts by 1
                    player.setStrikeOuts(player.getStrikeouts() + 1);
                    break;
                case 'W': // If character is W increase walks by 1
                    player.setWalks(player.getWalks() + 1);
                    break;
                case 'P': // If character is P increase hit by pitch by 1
                    player.setHitByPitches(player.getHitByPitches() + 1);
                    break;
                case 'S': // If character is S increase sacrifice by 1
                    player.setSacrifices(player.getSacrifices() + 1);
                    break;
                case 'E': // If character is E increase error by 1
                    player.setError(player.getErrors() + 1);
                    break;
                default:
                    break;
            }
            if(!multipleEntries)    // Add player to hash map if not already in map
                hashMapPlayers.put(playerKey, player);          
        }

        FileWriter outFile = new FileWriter("leaders.txt");
        BufferedWriter bWriter = new BufferedWriter(outFile);
        
        Player[] playerArray = new Player[hashMapPlayers.size()];   // Array to store players in for sorting
        int i = 0;
        for(int pKey: hashMapPlayers.keySet())   // Stores hash map of players into array
        {
            if(hashMapPlayers.get(pKey) != null)
            {
                playerArray[i] = hashMapPlayers.get(pKey);
                i++;
            }
        }
        sortAlphabetically(playerArray); // Sort player array alphabetically
        i = 0; // Reset index to beginning of array
        displayPlayers(bWriter, playerArray, i);    // Display all players and their stats

        displayLeaders(bWriter, hashMapPlayers);    // Display all leaders

        input.close();
        keyFile.close();
        fileReader.close();

        bWriter.close();
        outFile.close();
    }

    /**
     * Gets top 3 hits scores
     * @param map hash map to search for scores
     * @param scores array to store scores in
     */
    public static void getLeaderScoresHits(HashMap<Integer,Player> map, int[] scores)
    {
        int firstBiggest;
        int secondBiggest;
        int thirdBiggest;
        firstBiggest = 0;

        for(int pKey: map.keySet()) // Gets first highest score
        {
            if(map.get(pKey).getHits() > firstBiggest)
                firstBiggest = map.get(pKey).getHits();
        }
        scores[0] = firstBiggest;   // Return first place score

        firstBiggest = secondBiggest = 0;
        for(int pKey: map.keySet())   // Stores hash map of players into array
        {
            if(map.get(pKey).getHits() > firstBiggest)
            {
                secondBiggest = firstBiggest;
                firstBiggest = map.get(pKey).getHits();
            }
            else if(map.get(pKey).getHits() < firstBiggest && map.get(pKey).getHits() > secondBiggest)
            {
                secondBiggest = map.get(pKey).getHits();
            }
        }
        scores[1] = secondBiggest; // Return second place score
        firstBiggest = secondBiggest = thirdBiggest = 0;
        for(int pKey:map.keySet())
        {
            if(map.get(pKey).getHits() != firstBiggest && map.get(pKey).getHits() != secondBiggest && map.get(pKey).getHits() != thirdBiggest)
            {
                if(map.get(pKey).getHits() > firstBiggest)  // If greater than most number of hits
                {
                    thirdBiggest = secondBiggest;
                    secondBiggest = firstBiggest;
                    firstBiggest = map.get(pKey).getHits();
                }
                else if(map.get(pKey).getHits() > secondBiggest)    // If less than most number of hits, but greater than second most
                {
                    thirdBiggest = secondBiggest;
                    secondBiggest = map.get(pKey).getHits();
                }
                else if(map.get(pKey).getHits() > thirdBiggest) // If less than first and second most number of hits, but greater than third most
                {
                    thirdBiggest = map.get(pKey).getHits();
                }
            }
        }
        scores[2] = thirdBiggest;   // Return third place score
    }
    
    /**
     * Gets top 3 walks scores
     * @param map hash map to search for scores
     * @param scores array to store scores in
     */
    public static void getLeaderScoresWalks(HashMap<Integer,Player> map, int[] scores)
    {
        int firstBiggest;
        int secondBiggest;
        int thirdBiggest;
        firstBiggest = 0;

        for(int pKey: map.keySet()) // Gets first highest score
        {
            if(map.get(pKey).getWalks() > firstBiggest)
                firstBiggest = map.get(pKey).getWalks();
        }
        scores[0] = firstBiggest;   // Return first place score

        firstBiggest = secondBiggest = 0;
        for(int pKey: map.keySet())   // Stores hash map of players into array
        {
            if(map.get(pKey).getWalks() > firstBiggest)
            {
                secondBiggest = firstBiggest;
                firstBiggest = map.get(pKey).getWalks();
            }
            else if(map.get(pKey).getWalks() < firstBiggest && map.get(pKey).getWalks() > secondBiggest)
            {
                secondBiggest = map.get(pKey).getWalks();
            }
        }
        scores[1] = secondBiggest; // Return second place score
        firstBiggest = secondBiggest = thirdBiggest = 0;
        for(int pKey:map.keySet())
        {
            if(map.get(pKey).getWalks() != firstBiggest && map.get(pKey).getWalks() != secondBiggest && map.get(pKey).getWalks() != thirdBiggest)
            {
                if(map.get(pKey).getWalks() > firstBiggest) // If greater than most number of walks
                {
                    thirdBiggest = secondBiggest;
                    secondBiggest = firstBiggest;
                    firstBiggest = map.get(pKey).getWalks();
                }
                else if(map.get(pKey).getWalks() > secondBiggest)   // If less than most number of walks, but greater than second most
                {
                    thirdBiggest = secondBiggest;
                    secondBiggest = map.get(pKey).getWalks();
                }
                else if(map.get(pKey).getWalks() > thirdBiggest)    // If less than first and second most number of walks, but greater than third most
                {
                    thirdBiggest = map.get(pKey).getWalks();
                }
            }
        }
        scores[2] = thirdBiggest;   // Return third place score
    }

    /**
     * Gets top 3 strikeouts scores
     * @param map hash map to search for scores
     * @param scores array to store scores in
     */
    public static void getLeaderScoresStrikeouts(HashMap<Integer,Player> map, int[] scores)
    {
        int firstLowest;
        int secondLowest;
        int thirdLowest;
        firstLowest = 0;

        for(int pKey: map.keySet()) // Gets first highest score
        {
            if(map.get(pKey).getWalks() < firstLowest)
                firstLowest = map.get(pKey).getWalks();
        }
        scores[0] = firstLowest;   // Return first place score

        firstLowest = secondLowest = 0;
        for(int pKey: map.keySet())   // Stores hash map of players into array
        {
            if(map.get(pKey).getWalks() < firstLowest)
            {
                secondLowest = firstLowest;
                firstLowest = map.get(pKey).getWalks();
            }
            else if(map.get(pKey).getWalks() > firstLowest && map.get(pKey).getWalks() < secondLowest)
            {
                secondLowest = map.get(pKey).getWalks();
            }
        }
        scores[1] = secondLowest; // Return second place score
        firstLowest = secondLowest = thirdLowest = 0;
        for(int pKey:map.keySet())
        {
            if(map.get(pKey).getWalks() != firstLowest && map.get(pKey).getWalks() != secondLowest && map.get(pKey).getWalks() != thirdLowest)
            {
                if(map.get(pKey).getWalks() < firstLowest)  // If lower than current lowest number of strikeout
                {
                    thirdLowest = secondLowest;
                    secondLowest = firstLowest;
                    firstLowest = map.get(pKey).getWalks();
                }
                else if(map.get(pKey).getWalks() < secondLowest)    // If lower than second lowest number of strikeout but greater than lowest number of strikeouts
                {
                    thirdLowest = secondLowest;
                    secondLowest = map.get(pKey).getWalks();
                }
                else if(map.get(pKey).getWalks() < thirdLowest) // If great than first and second lowest number of strikeouts but less than third lowest number of strikeouts
                {
                    thirdLowest = map.get(pKey).getWalks();
                }
            }
        }
        scores[2] = thirdLowest;   // Return third place score
    }
    
    /**
     * Gets top 3 hit by pitches scores
     * @param map hash map to search for scores
     * @param scores array to store scores in
     */
    public static void getLeaderScoresHitByPitch(HashMap<Integer,Player> map, int[] scores)
    {
        int firstBiggest;
        int secondBiggest;
        int thirdBiggest;
        firstBiggest = 0;

        for(int pKey: map.keySet()) // Gets first highest score
        {
            if(map.get(pKey).getHitByPitches() > firstBiggest)
                firstBiggest = map.get(pKey).getHitByPitches();
        }
        scores[0] = firstBiggest;   // Return first place score

        firstBiggest = secondBiggest = 0;
        for(int pKey: map.keySet())   // Stores hash map of players into array
        {
            if(map.get(pKey).getHitByPitches() > firstBiggest)
            {
                secondBiggest = firstBiggest;
                firstBiggest = map.get(pKey).getHitByPitches();
            }
            else if(map.get(pKey).getHitByPitches() < firstBiggest && map.get(pKey).getHitByPitches() > secondBiggest)
            {
                secondBiggest = map.get(pKey).getHitByPitches();
            }
        }
        scores[1] = secondBiggest; // Return second place score
        firstBiggest = secondBiggest = thirdBiggest = 0;
        for(int pKey:map.keySet())
        {
            if(map.get(pKey).getHitByPitches() != firstBiggest && map.get(pKey).getHitByPitches() != secondBiggest && map.get(pKey).getHitByPitches() != thirdBiggest)
            {
                if(map.get(pKey).getHitByPitches() > firstBiggest)  // If greater than greatest number of hit by pitches
                {
                    thirdBiggest = secondBiggest;
                    secondBiggest = firstBiggest;
                    firstBiggest = map.get(pKey).getHitByPitches();
                }
                else if(map.get(pKey).getHitByPitches() > secondBiggest)    // If less than greatest number of hit by pitches, but less than second most
                {
                    thirdBiggest = secondBiggest;
                    secondBiggest = map.get(pKey).getHitByPitches();
                }
                else if(map.get(pKey).getHitByPitches() > thirdBiggest)
                {
                    thirdBiggest = map.get(pKey).getHitByPitches(); // If less than first and second most number of hit by pitches, but greater than third most
                }
            }
        }
        scores[2] = thirdBiggest;   // Return third place score
    }
    
    /**
     * Gets top 3 batting average scores
     * @param map hash map to get scores from
     * @param scores array to store scores in
     */
    public static void getLeaderScoresBattingAverage(HashMap<Integer,Player> map, double[] scores)
    {
        double firstBiggest;
        double secondBiggest;
        double thirdBiggest;
        int numAtBats;
        double battingAverage;
        firstBiggest = 0;

        for(int pKey: map.keySet()) // Gets first highest score
        {
            numAtBats = map.get(pKey).calculateNumAtBats(map.get(pKey).getHits(), map.get(pKey).getOuts(), map.get(pKey).getStrikeouts(), map.get(pKey).getErrors());
            battingAverage = map.get(pKey).calculateBattingAverage(map.get(pKey).getHits(), numAtBats);
            if(battingAverage > firstBiggest)   // If batting average is bigger than first biggest
                firstBiggest = battingAverage;
        }
        scores[0] = firstBiggest;   // Return first place score

        firstBiggest = secondBiggest = 0;
        for(int pKey: map.keySet())   // Stores hash map of players into array
        {
            numAtBats = map.get(pKey).calculateNumAtBats(map.get(pKey).getHits(), map.get(pKey).getOuts(), map.get(pKey).getStrikeouts(), map.get(pKey).getErrors());
            battingAverage = map.get(pKey).calculateBattingAverage(map.get(pKey).getHits(), numAtBats);
            if(battingAverage > firstBiggest)
            {
                secondBiggest = firstBiggest;
                firstBiggest = battingAverage;
            }
            else if(battingAverage < firstBiggest && battingAverage > secondBiggest)    // If batting average is bigger than first biggest, but less than second biggest
            {
                secondBiggest = battingAverage;
            }
        }
        scores[1] = secondBiggest; // Return second place score
        firstBiggest = secondBiggest = thirdBiggest = 0;
        for(int pKey:map.keySet())
        {
            // Number of at bats of player
            numAtBats = map.get(pKey).calculateNumAtBats(map.get(pKey).getHits(), map.get(pKey).getOuts(), map.get(pKey).getStrikeouts(), map.get(pKey).getErrors());
            
            // Batting average of player
            battingAverage = map.get(pKey).calculateBattingAverage(map.get(pKey).getHits(), numAtBats);
            if(Math.abs(battingAverage - firstBiggest) < THRESHOLD && Math.abs(battingAverage - secondBiggest) < THRESHOLD && Math.abs(battingAverage - thirdBiggest) < THRESHOLD)
            {
                if(battingAverage > firstBiggest)   // If batting average is bigger than biggest batting average
                {
                    thirdBiggest = secondBiggest;
                    secondBiggest = firstBiggest;
                    firstBiggest = battingAverage;
                }
                else if(battingAverage > secondBiggest) // If batting average is less than biggest batting average, but less than second biggest batting average
                {
                    thirdBiggest = secondBiggest;
                    secondBiggest = battingAverage;
                }
                else if(battingAverage > thirdBiggest)  // If batting average is less first and second biggest batting average, but bigger than third biggest batting average
                {
                    thirdBiggest = battingAverage;
                }
            }
        }
        scores[2] = thirdBiggest;   // Return third place score
    }

    /**
     * Gets top 3 on base percentage scores
     * @param map hash map to search for
     * @param scores array to store scores in
     */
    public static void getLeaderScoresOnBasePercentage(HashMap<Integer,Player> map, double[] scores) throws IOException
    {
        double firstBiggest;
        double secondBiggest;
        double thirdBiggest;
        int plateAppearances;
        double onBasePercentage;
        firstBiggest = 0;
        for(Player player: map.values())
        {
            plateAppearances = player.calculatePlateAppearances(player.getHits(), player.getOuts(), player.getStrikeouts(), player.getWalks(), player.getHitByPitches(), player.getSacrifices(), player.getErrors());
            onBasePercentage = player.calculateOnBasePercentage(player.getHits(), player.getWalks(), player.getHitByPitches(), plateAppearances);
            if(onBasePercentage > firstBiggest)  // If batting average is bigger than first biggest
                firstBiggest = onBasePercentage;
        }
        scores[0] = firstBiggest;

        firstBiggest = secondBiggest = 0;
        for(Player player: map.values())
        {
            plateAppearances = player.calculatePlateAppearances(player.getHits(), player.getOuts(), player.getStrikeouts(), player.getWalks(), player.getHitByPitches(), player.getSacrifices(), player.getErrors());
            onBasePercentage = player.calculateOnBasePercentage(player.getHits(), player.getWalks(), player.getHitByPitches(), plateAppearances);
            if(onBasePercentage > firstBiggest)  // If batting average is bigger than first biggest
            {
                secondBiggest = firstBiggest;
                firstBiggest = onBasePercentage;
            }
            else if(onBasePercentage < firstBiggest && onBasePercentage > secondBiggest)   // If batting average is less than first biggest, but bigger than second biggest
            {
                secondBiggest = onBasePercentage;
            }
        }
        scores[1] = secondBiggest;

        firstBiggest = secondBiggest = thirdBiggest = 0;
        for(Player player:map.values())
        {
            plateAppearances = player.calculatePlateAppearances(player.getHits(), player.getOuts(), player.getStrikeouts(), player.getWalks(), player.getHitByPitches(), player.getSacrifices(), player.getErrors());
            onBasePercentage = player.calculateOnBasePercentage(player.getHits(), player.getWalks(), player.getHitByPitches(), plateAppearances);
                if(onBasePercentage > firstBiggest)   // If batting average is bigger than biggest batting average
                {
                    thirdBiggest = secondBiggest;
                    secondBiggest = firstBiggest;
                    firstBiggest = onBasePercentage;
                }
                else if(onBasePercentage > secondBiggest) // If batting average is less than biggest batting average, but less than second biggest batting average
                {
                    thirdBiggest = secondBiggest;
                    secondBiggest = onBasePercentage;
                }
                else if(onBasePercentage > thirdBiggest)  // If batting average is less than first and second biggest batting average, but bigger than third biggest batting average
                {
                    thirdBiggest = onBasePercentage;
                }
        }
        scores[2] = thirdBiggest;   // Return third place score
    }

    /**
     * This function mainly calls displayPlayersAway and displayPlayersHome
     * @param bWriter output file
     * @param map hash map of players
     * @throws IOException
     */
    public static void displayLeaders(BufferedWriter bWriter, HashMap<Integer,Player> map) throws IOException
    {
        int totalNumLeaders;
        int firstPlaceCounter;
        int secondPlaceCounter;
        int firstScore, secondScore, thirdScore;
        double firstScoreDouble, secondScoreDouble, thirdScoreDouble;
        int[] scores = new int[3];  // Scores array for integer scores
        double[] scores2 = new double[3]; // Scores array for double scores
        Player[] playerArray = new Player[map.size()];  // Array to store leaders in for sorting alphabetically
        Player[] tempArray = new Player[map.size()];    // Temp array to store leaders to check if already displayed to prevent repeat display of leaders
        bWriter.write("LEAGUE LEADERS\n");
        for(int i = 0; i < NUM_LEADER_STATS; i++)
        {
            totalNumLeaders = 0;    // Counter to keep track of total number of leaders
            firstPlaceCounter = 0;  // Counter to keep track of number of first place leaders
            secondPlaceCounter = 0; // Counter to keep track of number of second place leaders
            firstScore = secondScore = thirdScore = 0;
            switch(i)
            {
                case 0: // Case for displaying BATTING AVERAGE leaders
                    {
                        bWriter.write("BATTING AVERAGE\n");
                        int j = 0;
                        //boolean displayed = false;
                        getLeaderScoresBattingAverage(map, scores2);
                        firstScoreDouble = scores2[0];
                        secondScoreDouble = scores2[1];
                        thirdScoreDouble = scores2[2];
                        int numAtBats;
                        double battingAverage;
                        boolean awayLeaders = false;
                        for(Player player: map.values())
                        {
                            numAtBats = player.calculateNumAtBats(player.getHits(), player.getOuts(), player.getStrikeouts(), player.getErrors());
                            battingAverage = player.calculateBattingAverage(player.getHits(), numAtBats);
                            if(Math.abs(battingAverage - firstScoreDouble) < THRESHOLD)
                            {
                                playerArray[j] = player;
                                tempArray[j] = player;
                                firstPlaceCounter++;
                                j++;
                            }
                        }
                        sortAlphabetically(playerArray);    // Sort first place leaders alphabetically
                        bWriter.write(String.format("%.3f", firstScoreDouble) + "\t");  // Print out score
                        for(int x = 0; x < playerArray.length; x++)
                        {
                            if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                            {
                                if(!awayLeaders)
                                {
                                    bWriter.write(playerArray[x].getName());
                                    awayLeaders = true;
                                }
                                else
                                    bWriter.write(", " + playerArray[x].getName());
                            }
                        }
                        for(int x = 0; x < playerArray.length; x++)
                        {
                            if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                            {
                                if(!awayLeaders)
                                {
                                    bWriter.write(playerArray[x].getName());
                                    awayLeaders = true;
                                }
                                else
                                    bWriter.write(", " + playerArray[x].getName());
                            }
                        }
                        totalNumLeaders += firstPlaceCounter;
                        if(totalNumLeaders < 3 && Math.abs(secondScoreDouble - firstScoreDouble) > THRESHOLD) // If 2 or less leaders and second score isn't equal to 1st score, find second place leaders
                        {
                            awayLeaders = false;
                            bWriter.write("\n");
                            j = 0;
                            Arrays.fill(playerArray, null); // Reset array of leaders
                            for(Player player: map.values())
                            {
                                numAtBats = player.calculateNumAtBats(player.getHits(), player.getOuts(), player.getStrikeouts(), player.getErrors());
                                battingAverage = player.calculateBattingAverage(player.getHits(), numAtBats);
                                if(Math.abs(battingAverage - secondScoreDouble) < THRESHOLD)
                                {
                                    playerArray[j] = player;
                                    secondPlaceCounter++;
                                    j++;
                                }
                            }
                            sortAlphabetically(playerArray);    // Sort second place leaders alphabetically
                            bWriter.write(String.format("%.3f", secondScoreDouble) + "\t"); // Print out score
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // Display home leaders alphabetically second
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            totalNumLeaders += secondPlaceCounter;
                        }
                        else // If ties for first were >= 3 do not print out any more leaders and end loop for that stat
                        {
                            Arrays.fill(playerArray, null); 
                            bWriter.write("\n\n");
                            break;
                        }
                        if(totalNumLeaders < 3 && Math.abs(thirdScore - secondScore) > THRESHOLD)   // If 2 or less leaders and if third score isn't equal to second score, find third place leaders
                        {
                            awayLeaders = false;
                            bWriter.write("\n");
                            j = 0;
                            Arrays.fill(playerArray, null); // Reset array of leaders
                            for(Player player: map.values())
                            {
                                numAtBats = player.calculateNumAtBats(player.getHits(), player.getOuts(), player.getStrikeouts(), player.getErrors());
                                battingAverage = player.calculateBattingAverage(player.getHits(), numAtBats);
                                if(Math.abs(battingAverage - thirdScoreDouble) < THRESHOLD)
                                {
                                    playerArray[j] = player;
                                    j++;
                                }
                            }
                            sortAlphabetically(playerArray);    // Sort second place leaders alphabetically
                            bWriter.write(String.format("%.3f", thirdScoreDouble) + "\t");  // Print out score
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // Display home leaders alphabetically second
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                        }
                        Arrays.fill(playerArray, null); // Reset array of leaders                        
                        bWriter.write("\n\n");
                        break;     
                    }
                case 1: // Case for displaying ON BASE PERCENTAGE leaders
                    {
                        Arrays.fill(scores2, 0);
                        bWriter.write("ON-BASE PERCENTAGE\n");
                        int j = 0;
                        getLeaderScoresOnBasePercentage(map, scores2);
                        firstScoreDouble = scores2[0];
                        secondScoreDouble = scores2[1];
                        thirdScoreDouble = scores2[2];
                        /*bWriter.write("TEST: " + String.format("%.3f",firstScoreDouble) + "\n");
                        bWriter.write("TEST: " + String.format("%.3f",secondScoreDouble) + "\n");
                        bWriter.write("TEST: " + String.format("%.3f",thirdScoreDouble) + "\n");*/
                        int plateAppearances;
                        double onBasePercentage;
                        boolean awayLeaders = false;
                        for(Player player: map.values())
                        {
                            plateAppearances = player.calculatePlateAppearances(player.getHits(), player.getOuts(), player.getStrikeouts(), player.getWalks(), player.getHitByPitches(), player.getSacrifices(), player.getErrors());
                            onBasePercentage = player.calculateOnBasePercentage(player.getHits(), player.getWalks(), player.getHitByPitches(), plateAppearances);
                            if(Math.abs(onBasePercentage - firstScoreDouble) < THRESHOLD)
                            {
                                playerArray[j] = player;
                                firstPlaceCounter++;
                                j++;
                            }
                        }
                        sortAlphabetically(playerArray);    // Sort first place leaders alphabetically
                        bWriter.write(String.format("%.3f", firstScoreDouble) + "\t"); // Print out score
                        for(int x = 0; x < playerArray.length; x++)
                        {
                            if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                            {
                                if(!awayLeaders)
                                {
                                    bWriter.write(playerArray[x].getName());
                                    awayLeaders = true;
                                }
                                else
                                    bWriter.write(", " + playerArray[x].getName());
                            }
                        }
                        for(int x = 0; x < playerArray.length; x++)
                        {
                            if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                            {
                                if(!awayLeaders)
                                {
                                    bWriter.write(playerArray[x].getName());
                                    awayLeaders = true;
                                }
                                else
                                    bWriter.write(", " + playerArray[x].getName());
                            }
                        }
                        totalNumLeaders += firstPlaceCounter;
                        if(totalNumLeaders < 3 && Math.abs(secondScoreDouble - firstScoreDouble) > THRESHOLD) // If 2 or less leaders and first score isn't equal to second score, find second place leaders
                        {
                            awayLeaders = false;
                            bWriter.write("\n");
                            j = 0;
                            Arrays.fill(playerArray, null); // Reset array of leaders
                            for(Player player: map.values())
                            {
                                plateAppearances = player.calculatePlateAppearances(player.getHits(), player.getOuts(), player.getStrikeouts(), player.getWalks(), player.getHitByPitches(), player.getSacrifices(), player.getErrors());
                                onBasePercentage = player.calculateOnBasePercentage(player.getHits(), player.getWalks(), player.getHitByPitches(), plateAppearances);
                                if(Math.abs(onBasePercentage - secondScoreDouble) < THRESHOLD)
                                {
                                    playerArray[j] = player;
                                    secondPlaceCounter++;
                                    j++;
                                }
                            }
                            sortAlphabetically(playerArray);    // Sort second place leaders alphabetically
                            bWriter.write(String.format("%.3f", secondScoreDouble) + "\t"); // Print out score
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            totalNumLeaders += secondPlaceCounter;
                        }
                        else // If ties for first were >= 3 do not print out any more leaders and end loop for that stat
                        {
                            Arrays.fill(playerArray, null); 
                            bWriter.write("\n\n");
                            break;
                        }
                        if(totalNumLeaders < 3 && Math.abs(thirdScoreDouble - secondScoreDouble) > THRESHOLD)   // If 2 or less leaders and if third score isn't equal to second score, find third place leaders
                        {
                            awayLeaders = false;
                            bWriter.write("\n");
                            j = 0;
                            Arrays.fill(playerArray, null); // Reset array of leaders
                            for(Player player: map.values())
                            {
                                plateAppearances = player.calculatePlateAppearances(player.getHits(), player.getOuts(), player.getStrikeouts(), player.getWalks(), player.getHitByPitches(), player.getSacrifices(), player.getErrors());
                                onBasePercentage = player.calculateOnBasePercentage(player.getHits(), player.getWalks(), player.getHitByPitches(), plateAppearances);
                                if(Math.abs(onBasePercentage - thirdScoreDouble) < THRESHOLD)
                                {
                                    playerArray[j] = player;
                                    j++;
                                }
                            }
                            sortAlphabetically(playerArray);    // Sort second place leaders alphabetically
                            bWriter.write(String.format("%.3f", thirdScoreDouble) + "\t");  // Print out score
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                        }
                        Arrays.fill(playerArray, null); // Reset array of leaders                        
                        bWriter.write("\n\n");
                        break;     
                    }
                case 2: // Case for displaying HITS leaders
                    {
                        bWriter.write("HITS\n");
                        int j = 0;
                        getLeaderScoresHits(map, scores);
                        firstScore = scores[0];
                        secondScore = scores[1];
                        thirdScore = scores[2];
                        boolean awayLeaders = false;
                        for(Player player: map.values())
                        {
                            if(player.getHits() == firstScore)
                            {
                                playerArray[j] = player;
                                firstPlaceCounter++;
                                j++;
                            }
                        }
                        sortAlphabetically(playerArray);    // Sort first place leaders alphabetically
                        bWriter.write(firstScore + "\t");   // Print out first place leader score
                        for(int x = 0; x < playerArray.length; x++)
                        {
                            if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                            {
                                if(!awayLeaders)
                                {
                                    bWriter.write(playerArray[x].getName());
                                    awayLeaders = true;
                                }
                                else
                                    bWriter.write(", " + playerArray[x].getName());
                            }
                        }
                        for(int x = 0; x < playerArray.length; x++)
                        {
                            if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                            {
                                if(!awayLeaders)
                                {
                                    bWriter.write(playerArray[x].getName());
                                    awayLeaders = true;
                                }
                                else
                                    bWriter.write(", " + playerArray[x].getName());
                            }
                        }
                        totalNumLeaders += firstPlaceCounter;
                        if(totalNumLeaders < 3 && Math.abs(secondScore - firstScore) > THRESHOLD) // If 2 or less leaders and if second score isn't equal to first score, find second place leaders
                        {
                            awayLeaders = false;
                            bWriter.write("\n");
                            j = 0;
                            Arrays.fill(playerArray, null); // Reset player array
                            for(Player player: map.values())
                            {
                                if(player.getHits() == secondScore)
                                {
                                    playerArray[j] = player;
                                    secondPlaceCounter++;
                                    j++;
                                }
                            }
                            sortAlphabetically(playerArray);    // Sort second place leaders alphabetically
                            bWriter.write(secondScore + "\t");  // Print second place leader score
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            totalNumLeaders += secondPlaceCounter;
                        }
                        else // If ties for first were >= 3 do not print out any more leaders and end loop for that stat
                        {
                            Arrays.fill(playerArray, null); 
                            bWriter.write("\n\n");
                            break;
                        }
                        if(totalNumLeaders < 3 && Math.abs(thirdScore - secondScore) > THRESHOLD)   // If 2 or less leaders and if third score isn't equal to second score, find third place leaders
                        {
                            awayLeaders = false;
                            bWriter.write("\n");
                            j = 0;
                            Arrays.fill(playerArray, null); // Reset player array
                            for(Player player: map.values())
                            {
                                if(player.getHits() == thirdScore)
                                {
                                    playerArray[j] = player;
                                    j++;
                                }
                            }
                            sortAlphabetically(playerArray);    // Sort third place leaders alphabetically
                            bWriter.write(thirdScore + "\t");   // Print third place leader score
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                        }
                        Arrays.fill(playerArray, null);                        
                        bWriter.write("\n\n");
                        break;     
                    }
                case 3: // Case for displaying WALKS leaders
                    {
                        bWriter.write("WALKS\n");  
                        int j = 0;
                        getLeaderScoresWalks(map, scores);
                        firstScore = scores[0];
                        secondScore = scores[1];
                        thirdScore = scores[2];
                        boolean awayLeaders = false;
                        for(Player player: map.values())
                        {
                            if(player.getWalks() == firstScore)
                            {
                                playerArray[j] = player;
                                firstPlaceCounter++;
                                j++;
                            }
                            
                        }
                        sortAlphabetically(playerArray);    // Sort first place leaders alphabetically
                        bWriter.write(firstScore + "\t");   // Print first place leader score
                        for(int x = 0; x < playerArray.length; x++)
                        {
                            if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                            {
                                if(!awayLeaders)
                                {
                                    bWriter.write(playerArray[x].getName());
                                    awayLeaders = true;
                                }
                                else
                                    bWriter.write(", " + playerArray[x].getName());
                            }
                        }
                        for(int x = 0; x < playerArray.length; x++)
                        {
                            if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                            {
                                if(!awayLeaders)
                                {
                                    bWriter.write(playerArray[x].getName());
                                    awayLeaders = true;
                                }
                                else
                                    bWriter.write(", " + playerArray[x].getName());
                            }
                        }
                        totalNumLeaders += firstPlaceCounter;
                        if(totalNumLeaders < 3 && Math.abs(secondScore - firstScore) > THRESHOLD) // If 2 or less leaders and if second score isn't equal to first score, find second place leaders
                        {
                            awayLeaders = false;
                            bWriter.write("\n");
                            j = 0;
                            Arrays.fill(playerArray, null); // Reset player array
                            for(Player player: map.values())
                            {
                                if(player.getWalks() == secondScore)
                                {
                                    playerArray[j] = player;
                                    secondPlaceCounter++;
                                    j++;
                                }
                            }
                            sortAlphabetically(playerArray);    // Sort second place leaders alphabetically
                            bWriter.write(secondScore + "\t");  // Print second place leader score
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            totalNumLeaders += secondPlaceCounter;
                        }
                        else // If ties for first were >= 3 do not print out any more leaders and end loop for that stat
                        {
                            Arrays.fill(playerArray, null); 
                            bWriter.write("\n\n");
                            break;
                        }
                        if(totalNumLeaders < 3 && Math.abs(thirdScore - secondScore) > THRESHOLD)   // If 2 or less leaders and if third score isn't equal to second score, find third place leaders
                        {
                            awayLeaders = false;
                            bWriter.write("\n");
                            j = 0;
                            Arrays.fill(playerArray, null); // Reset player array
                            for(Player player: map.values())
                            {
                                if(player.getWalks() == thirdScore)
                                {
                                    playerArray[j] = player;
                                    j++;
                                }
                            }
                            sortAlphabetically(playerArray);    // Sort third place leaders alphabetically
                            bWriter.write(thirdScore + "\t");   // Print third place leader score
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                        }
                        Arrays.fill(playerArray, null);                        
                        bWriter.write("\n\n");
                        break;     
                    }
                case 4: // Case for displaying STRIKEOUTS leaders
                    {
                        bWriter.write("STRIKEOUTS\n");   
                        int j = 0;
                        getLeaderScoresStrikeouts(map, scores);
                        firstScore = scores[0];
                        secondScore = scores[1];
                        thirdScore = scores[2];
                        boolean awayLeaders = false;
                        for(Player player: map.values())
                        {
                            if(player.getStrikeouts() == firstScore)
                            {
                                playerArray[j] = player;
                                firstPlaceCounter++;
                                j++;
                            }
                        }
                        sortAlphabetically(playerArray);    // Sort first place leaders alphabetically
                        bWriter.write(firstScore + "\t");   // Print first place leader score
                        for(int x = 0; x < playerArray.length; x++)
                        {
                            if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                            {
                                if(!awayLeaders)
                                {
                                    bWriter.write(playerArray[x].getName());
                                    awayLeaders = true;
                                }
                                else
                                    bWriter.write(", " + playerArray[x].getName());
                            }
                        }
                        for(int x = 0; x < playerArray.length; x++)
                        {
                            if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                            {
                                if(!awayLeaders)
                                {
                                    bWriter.write(playerArray[x].getName());
                                    awayLeaders = true;
                                }
                                else
                                    bWriter.write(", " + playerArray[x].getName());
                            }
                        }
                        totalNumLeaders += firstPlaceCounter;
                        if(totalNumLeaders < 3 && Math.abs(secondScore - firstScore) > THRESHOLD) // If 2 or less leaders and if second score isn't equal to first score, find second place leaders)
                        {
                            awayLeaders = false;
                            bWriter.write("\n");
                            j = 0;
                            Arrays.fill(playerArray, null); // Reset player array
                            for(Player player: map.values())
                            {
                                if(player.getStrikeouts() == secondScore)
                                {
                                    playerArray[j] = player;
                                    secondPlaceCounter++;
                                    j++;
                                }
                            }
                            sortAlphabetically(playerArray);    // Sort second place leaders alphabetically
                            bWriter.write(secondScore + "\t");  // Print second place leader score
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            totalNumLeaders += secondPlaceCounter;
                        }
                        else // If ties for first were >= 3 do not print out any more leaders and end loop for that stat
                        {
                            Arrays.fill(playerArray, null); 
                            bWriter.write("\n\n");
                            break;
                        }
                        if(totalNumLeaders < 3 && Math.abs(thirdScore - secondScore) > THRESHOLD)   // If 2 or less leaders and if third score isn't equal to third score, find second place leaders
                        {
                            awayLeaders = false;
                            bWriter.write("\n");
                            j = 0;
                            Arrays.fill(playerArray, null); // Reset player array
                            for(Player player: map.values())
                            {
                                if(player.getStrikeouts() == thirdScore)
                                {
                                    playerArray[j] = player;
                                    j++;
                                }
                            }
                            sortAlphabetically(playerArray);    // Sort third place leaders alphabetically
                            bWriter.write(thirdScore + "\t");   // Print third place leader score
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                        }
                        Arrays.fill(playerArray, null);                        
                        bWriter.write("\n\n");
                        break;  
                    }
                case 5: // Case for displaying HIT BY PITCH leaders
                    {
                        bWriter.write("HIT BY PITCH\n");   
                        int j = 0;
                        getLeaderScoresHitByPitch(map, scores);
                        firstScore = scores[0];
                        secondScore = scores[1];
                        thirdScore = scores[2];
                        boolean awayLeaders = false;
                        for(Player player: map.values())
                        {
                            if(player.getHitByPitches() == firstScore)
                            {
                                playerArray[j] = player;
                                firstPlaceCounter++;
                                j++;
                            }
                        }
                        sortAlphabetically(playerArray);    // Sort first place leaders alphabetically
                        bWriter.write(firstScore + "\t");   // Print first place leader score
                        for(int x = 0; x < playerArray.length; x++)
                        {
                            if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                            {
                                if(!awayLeaders)
                                {
                                    bWriter.write(playerArray[x].getName());
                                    awayLeaders = true;
                                }
                                else
                                    bWriter.write(", " + playerArray[x].getName());
                            }
                        }
                        for(int x = 0; x < playerArray.length; x++)
                        {
                            if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                            {
                                if(!awayLeaders)
                                {
                                    bWriter.write(playerArray[x].getName());
                                    awayLeaders = true;
                                }
                                else
                                    bWriter.write(", " + playerArray[x].getName());
                            }
                        }
                        totalNumLeaders += firstPlaceCounter;
                        if(totalNumLeaders < 3 && Math.abs(secondScore - firstScore) > THRESHOLD) // If 2 or less leaders and if second score isn't equal to first score, find second place leaders)
                        {
                            awayLeaders = false;
                            bWriter.write("\n");
                            j = 0;
                            Arrays.fill(playerArray, null); // Reset player array
                            for(Player player: map.values())
                            {
                                if(player.getHitByPitches() == secondScore)
                                {
                                    playerArray[j] = player;
                                    secondPlaceCounter++;
                                    j++;
                                }
                            }
                            sortAlphabetically(playerArray);    // Sort second place leaders alphabetically
                            bWriter.write(secondScore + "\t");  // Print second place leader score
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            totalNumLeaders += secondPlaceCounter;
                        }
                        else // If ties for first were >= 3 do not print out any more leaders and end loop for that stat
                        {
                            Arrays.fill(playerArray, null); 
                            bWriter.write("\n\n");
                            break;
                        }
                        if(totalNumLeaders < 3 && Math.abs(thirdScore - secondScore) > THRESHOLD)   // If 2 or less leaders and if third score isn't equal to second score, find third place leaders
                        {
                            awayLeaders = false;
                            bWriter.write("\n");
                            j = 0;
                            Arrays.fill(playerArray, null); // Reset player array
                            for(Player player: map.values())
                            {
                                if(player.getHitByPitches() == thirdScore)
                                {
                                    playerArray[j] = player;
                                    j++;
                                }
                            }
                            sortAlphabetically(playerArray);    // Sort third place leaders alphabetically
                            bWriter.write(thirdScore + "\t");   // Print third place leader score
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("A"))  // Display away leaders alphabetically first
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null && playerArray[x].getTeam().equals("H"))  // DIsplay home leaders alphabetically second
                                {
                                    if(!awayLeaders)
                                    {
                                        bWriter.write(playerArray[x].getName());
                                        awayLeaders = true;
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                        }
                        bWriter.write("\n\n");
                        Arrays.fill(playerArray, null);                     
                        break;  
                    }
                default:    System.out.println("Other Cases!");
                            break;
            }
        }
    }

    /**
     * This function mainly calls displayPlayersAway and displayPlayersHome
     * @param bWriter output file
     * @param arr array of players
     * @param index index of array
     * @throws IOException
     */
    public static void displayPlayers(BufferedWriter bWriter, Player[] arr, int index) throws IOException
    {
        //bWriter.write("Player" + "\t" + "AB" + "\t" + "H" + "\t" + "BB" + "\t" + "K" + "\t" + "HBP" + "\t" + "Sac" + "\t" + "BA" + "\t" + "OBP" + "\n"); // Header for player data display
        bWriter.write("AWAY\n");
        displayPlayersAway(bWriter, arr, index);    // Display AWAY team
        bWriter.write("\nHOME\n");
        index = 0;
        displayPlayersHome(bWriter, arr, index);    // Display HOME team
        bWriter.write("\n");
    }

    /**
     * Displays AWAY team with each players' stats in alphabetical order
     * @param bWriter output file
     * @param arr sorted array of players
     * @param index index of array
     * @throws IOException
     */
    public static void displayPlayersAway(BufferedWriter bWriter, Player[] arr, int index) throws IOException
    {
        if(index == arr.length) // If reached end of array
            return;
        else
        {
                if(arr[index].getTeam().equals("A")) // Check if player is on AWAY team
                    bWriter.write(arr[index].toString());   // Display player's data
                index++;    // Increment index
                displayPlayersAway(bWriter, arr, index);
        }
    }

    /**
     * Displays HOME team with each players' stats in alphabetical order
     * @param bWriter output file
     * @param arr sorted array of players
     * @param index index of array
     * @throws IOException
     */
    public static void displayPlayersHome(BufferedWriter bWriter, Player[] arr, int index) throws IOException
    {
        if(index == arr.length) // If reached end of array
            return;
        else
        {
            if(arr[index].getTeam().equals("H"))    // Check if player is on HOME team
                bWriter.write(arr[index].toString());   // Display player's data
            index++;    // Increment index
            displayPlayersHome(bWriter, arr, index);

        }
    }

    /**
     * Sorts array of players alphabetically
     * @param arr array of players
     */
    public static void sortAlphabetically(Player[] arr)
    {
        // Sort player array alphabetically
        boolean swap = true;
        while(swap)
        {
            swap = false;
            for(int index = 0; index < arr.length - 1; index++)
            {
                if(arr[index] != null && arr[index + 1] != null)
                {
                    int result = arr[index].compareTo(arr[index + 1].getName());
                    if(result > 0)
                    {
                        Player temp = arr[index];
                        arr[index] = arr[index + 1];
                        arr[index + 1] = temp;
                        swap = true;
                    }
                }
            }
        }
    }

}
