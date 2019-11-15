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
            //fileReader.close();
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
            if(line.isBlank())  // If line is blank (line before new header)
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
                //System.out.println(hashMapPlayers.get(playerKey));           
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

        displayLeaders(bWriter, hashMapPlayers);
        


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
                if(map.get(pKey).getHits() > firstBiggest)
                {
                    thirdBiggest = secondBiggest;
                    secondBiggest = firstBiggest;
                    firstBiggest = map.get(pKey).getHits();
                }
                else if(map.get(pKey).getHits() > secondBiggest)
                {
                    thirdBiggest = secondBiggest;
                    secondBiggest = map.get(pKey).getHits();
                }
                else if(map.get(pKey).getHits() > thirdBiggest)
                {
                    thirdBiggest = map.get(pKey).getHits();
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
    public static void getLeaderScoresOnBasePercentage(HashMap<Integer,Player> map, double[] scores)
    {
        double firstBiggest;
        double secondBiggest;
        double thirdBiggest;
        int plateAppearances;
        double onBasePercentage;
        firstBiggest = 0;

        for(int pKey: map.keySet()) // Gets first highest score
        {
            plateAppearances = map.get(pKey).calculatePlateAppearances(map.get(pKey).getHits(), map.get(pKey).getOuts(), map.get(pKey).getStrikeouts(), map.get(pKey).getWalks(), map.get(pKey).getHitByPitches(), map.get(pKey).getSacrifices());
            onBasePercentage = map.get(pKey).calculateOnBasePercentage(map.get(pKey).getHits(), map.get(pKey).getWalks(), map.get(pKey).getHitByPitches(), plateAppearances);
            if(onBasePercentage > firstBiggest)   // If batting average is bigger than first biggest
                firstBiggest = onBasePercentage;
        }
        scores[0] = firstBiggest;   // Return first place score

        firstBiggest = secondBiggest = 0;
        for(int pKey: map.keySet())   // Stores hash map of players into array
        {
            plateAppearances = map.get(pKey).calculatePlateAppearances(map.get(pKey).getHits(), map.get(pKey).getOuts(), map.get(pKey).getStrikeouts(), map.get(pKey).getWalks(), map.get(pKey).getHitByPitches(), map.get(pKey).getSacrifices());
            onBasePercentage = map.get(pKey).calculateOnBasePercentage(map.get(pKey).getHits(), map.get(pKey).getWalks(), map.get(pKey).getHitByPitches(), plateAppearances);
            if(onBasePercentage > firstBiggest)
            {
                secondBiggest = firstBiggest;
                firstBiggest = onBasePercentage;
            }
            else if(onBasePercentage < firstBiggest && onBasePercentage > secondBiggest)    // If batting average is bigger than first biggest, but less than second biggest
            {
                secondBiggest = onBasePercentage;
            }
        }
        scores[1] = secondBiggest; // Return second place score
        firstBiggest = secondBiggest = thirdBiggest = 0;
        for(int pKey:map.keySet())
        {
            plateAppearances = map.get(pKey).calculatePlateAppearances(map.get(pKey).getHits(), map.get(pKey).getOuts(), map.get(pKey).getStrikeouts(), map.get(pKey).getWalks(), map.get(pKey).getHitByPitches(), map.get(pKey).getSacrifices());
            onBasePercentage = map.get(pKey).calculateOnBasePercentage(map.get(pKey).getHits(), map.get(pKey).getWalks(), map.get(pKey).getHitByPitches(), plateAppearances);
            if(Math.abs(onBasePercentage - firstBiggest) < THRESHOLD && Math.abs(onBasePercentage - secondBiggest) < THRESHOLD && Math.abs(onBasePercentage - thirdBiggest) < THRESHOLD)
            {
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
                else if(onBasePercentage > thirdBiggest)  // If batting average is less first and second biggest batting average, but bigger than third biggest batting average
                {
                    thirdBiggest = onBasePercentage;
                }
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
        bWriter.write("LEADERS\n");
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
                        getLeaderScoresBattingAverage(map, scores2);
                        firstScoreDouble = scores2[0];
                        secondScoreDouble = scores2[1];
                        thirdScoreDouble = scores2[2];
                        int numAtBats;
                        double battingAverage;
                        //System.out.print("1: " + firstScoreDouble + ", 2: " + secondScoreDouble + ", 3: " + thirdScoreDouble + "\n");
                        for(Player player: map.values())
                        {
                            numAtBats = player.calculateNumAtBats(player.getHits(), player.getOuts(), player.getStrikeouts(), player.getErrors());
                            battingAverage = player.calculateBattingAverage(player.getHits(), numAtBats);
                            if(Math.abs(battingAverage - firstScoreDouble) < THRESHOLD)
                            {
                                playerArray[j] = player;
                                firstPlaceCounter++;
                                j++;
                            }
                        }
                        sortAlphabetically(playerArray);    // Sort first place leaders alphabetically
                        for(int x = 0; x < playerArray.length; x++)
                        {
                            if(playerArray[x] != null)
                            {
                                if(x == 0)
                                {
                                    bWriter.write(firstScoreDouble + "\t");
                                    bWriter.write(playerArray[x].getName());
                                }
                                else
                                    bWriter.write(", " + playerArray[x].getName());
                            }
                        }
                        totalNumLeaders += firstPlaceCounter;
                        if(totalNumLeaders < 3) // If 2 or less leaders, find second place leaders
                        {
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
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null)
                                {
                                    if(x == 0)
                                    {
                                        bWriter.write(secondScoreDouble + "\t");
                                        bWriter.write(playerArray[x].getName());
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            totalNumLeaders += secondPlaceCounter;
                        }
                        else // If ties for first were >= 3 do not print out any more leaders and end loop for that stat
                        {
                            bWriter.write("\n");
                            break;
                        }
                        if(totalNumLeaders < 3) // If 2 or less leaders, find 3rd place leader
                        {
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
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null)
                                {
                                    if(x == 0)
                                    {
                                        bWriter.write(thirdScoreDouble + "\t");
                                        bWriter.write(playerArray[x].getName());
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                        }
                        Arrays.fill(playerArray, null); // Reset array of leaders                        
                        bWriter.write("\n");
                        break;     
                    }
                case 1: // Case for displaying ON BASE PERCENTAGE leaders
                    {
                        bWriter.write("ON BASE PERCENTAGE\n");
                        int j = 0;
                        getLeaderScoresBattingAverage(map, scores2);
                        firstScoreDouble = scores2[0];
                        secondScoreDouble = scores2[1];
                        thirdScoreDouble = scores2[2];
                        int plateAppearances;
                        double onBasePercentage;
                        //System.out.print("1: " + firstScoreDouble + ", 2: " + secondScoreDouble + ", 3: " + thirdScoreDouble + "\n");
                        for(Player player: map.values())
                        {
                            plateAppearances = player.calculatePlateAppearances(player.getHits(), player.getOuts(), player.getStrikeouts(), player.getWalks(), player.getHitByPitches(), player.getSacrifices());
                            onBasePercentage = player.calculateOnBasePercentage(player.getHits(), player.getWalks(), player.getHitByPitches(), plateAppearances);
                            if(Math.abs(onBasePercentage - firstScoreDouble) < THRESHOLD)
                            {
                                playerArray[j] = player;
                                firstPlaceCounter++;
                                j++;
                            }
                        }
                        sortAlphabetically(playerArray);    // Sort first place leaders alphabetically
                        for(int x = 0; x < playerArray.length; x++)
                        {
                            if(playerArray[x] != null)
                            {
                                if(x == 0)
                                {
                                    bWriter.write(firstScoreDouble + "\t");
                                    bWriter.write(playerArray[x].getName());
                                }
                                else
                                    bWriter.write(", " + playerArray[x].getName());
                            }
                        }
                        totalNumLeaders += firstPlaceCounter;
                        if(totalNumLeaders < 3) // If 2 or less leaders, find second place leaders
                        {
                            bWriter.write("\n");
                            j = 0;
                            Arrays.fill(playerArray, null); // Reset array of leaders
                            for(Player player: map.values())
                            {
                                plateAppearances = player.calculatePlateAppearances(player.getHits(), player.getOuts(), player.getStrikeouts(), player.getWalks(), player.getHitByPitches(), player.getSacrifices());
                                onBasePercentage = player.calculateOnBasePercentage(player.getHits(), player.getWalks(), player.getHitByPitches(), plateAppearances);
                                if(Math.abs(onBasePercentage - secondScoreDouble) < THRESHOLD)
                                {
                                    playerArray[j] = player;
                                    secondPlaceCounter++;
                                    j++;
                                }
                            }
                            sortAlphabetically(playerArray);    // Sort second place leaders alphabetically
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null)
                                {
                                    if(x == 0)
                                    {
                                        bWriter.write(secondScoreDouble + "\t");
                                        bWriter.write(playerArray[x].getName());
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            totalNumLeaders += secondPlaceCounter;
                        }
                        else // If ties for first were >= 3 do not print out any more leaders and end loop for that stat
                        {
                            bWriter.write("\n");
                            break;
                        }
                        if(totalNumLeaders < 3) // If 2 or less leaders, find 3rd place leader
                        {
                            bWriter.write("\n");
                            j = 0;
                            Arrays.fill(playerArray, null); // Reset array of leaders
                            for(Player player: map.values())
                            {
                                plateAppearances = player.calculatePlateAppearances(player.getHits(), player.getOuts(), player.getStrikeouts(), player.getWalks(), player.getHitByPitches(), player.getSacrifices());
                                onBasePercentage = player.calculateOnBasePercentage(player.getHits(), player.getWalks(), player.getHitByPitches(), plateAppearances);
                                if(Math.abs(onBasePercentage - thirdScoreDouble) < THRESHOLD)
                                {
                                    playerArray[j] = player;
                                    j++;
                                }
                            }
                            sortAlphabetically(playerArray);    // Sort second place leaders alphabetically
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null)
                                {
                                    if(x == 0)
                                    {
                                        bWriter.write(thirdScoreDouble + "\t");
                                        bWriter.write(playerArray[x].getName());
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                        }
                        Arrays.fill(playerArray, null); // Reset array of leaders                        
                        bWriter.write("\n");
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
                        //System.out.print("1: " + firstScore + ", 2: " + secondScore + ", 3: " + thirdScore + "\n");
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
                        for(int x = 0; x < playerArray.length; x++)
                        {
                            if(playerArray[x] != null)
                            {
                                if(x == 0)
                                {
                                    bWriter.write(firstScore + "\t");   // Print first place leader score
                                    bWriter.write(playerArray[x].getName());
                                }
                                else
                                    bWriter.write(", " + playerArray[x].getName());
                            }
                        }
                        totalNumLeaders += firstPlaceCounter;
                        if(totalNumLeaders < 3)
                        {
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
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null)
                                {
                                    if(x == 0)
                                    {
                                        bWriter.write(secondScore + "\t");  // Print second place leader score
                                        bWriter.write(playerArray[x].getName());
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                            totalNumLeaders += secondPlaceCounter;
                        }
                        else // If ties for first were >= 3 do not print out any more leaders and end loop for that stat
                        {
                            bWriter.write("\n");
                            break;
                        }
                        if(totalNumLeaders < 3)
                        {
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
                            for(int x = 0; x < playerArray.length; x++)
                            {
                                if(playerArray[x] != null)
                                {
                                    if(x == 0)
                                    {
                                        bWriter.write(thirdScore + "\t");   // Print third place leader score
                                        bWriter.write(playerArray[x].getName());
                                    }
                                    else
                                        bWriter.write(", " + playerArray[x].getName());
                                }
                            }
                        }
                        Arrays.fill(playerArray, null);                        
                        bWriter.write("\n");
                        break;     
                    }
                case 3:
                    {
                        
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
        bWriter.write("Player\tAB\tH\tBB\tK\tHBP\tSac\tBA\tOBP\tPA\n"); // Header for player data display
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
        boolean swap = false;
        while(swap)
        {
            swap = false;
            for(int index = 0; index < arr.length; index++)
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