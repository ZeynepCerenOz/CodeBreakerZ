import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

//class to return best guess by the computer and number of remaining tokens for that guess. It is  used consistency check of user's answer
class bestGuessAndRemainingTokens{
    String bestGuess = "";
    ArrayList<Integer> foundNumberOfRemainingTokens = new ArrayList<Integer>();
}

//Class to keeps token structure
class TokenStructure{
    String theToken;
    int bestGuessStructureIndex;

}

public class CodeBreaker {
    

    //This keeps the items from which valid feedback can be constructed
    int [] validFeedbackItems = {  0, 1, 2,3,4,5,6};

    //This keeps all the possible token types. i.e. 420000 means a digit repeated 4 times, then another digit repeated 2 times?
    //ALGORİTHM STEP 1
    String [] bestGuessStructures={"330000","222000","321000","420000","411000","221100","311100","211110","111111","510000","600000"};


    ArrayList<TokenStructure>  allTokens;  //Keeps all of our current remaining tokens
    ArrayList<String> possibleFeedbacks; //This is all the possible valid feedbacks that can be taken from user


    private boolean isCurrentBestGuessStructureSameWithCountOfIntegers(String best,Integer[] CountOfIntegers){
        Arrays.sort(CountOfIntegers, Collections.reverseOrder());
        String CountOfIntegersString= Arrays.toString(CountOfIntegers).replaceAll("\\[|\\]|,|\\s", "");
                                                                       //removes parantheses and commas in CountOfIntegersString
        if(CountOfIntegersString.substring(0,6).equals(best))
            return true;

        return false;
    }

    //Method to construct all possible tokens for 6 digit number. It also returns the structure of than token i.e.
    public ArrayList<TokenStructure> GetAllpossibleTokensForEachBestGuessStructure()
    {
        ArrayList<TokenStructure> tokens = new ArrayList<TokenStructure>();

        for (int d1 = 0; d1 <= 999999; d1++) {


            TokenStructure token = new TokenStructure();
            token.theToken = String.format("%06d", d1);//pad zeros

            //should be changed
            //This is the token
            Integer[] CountOfIntegers = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            for (int k = 0; k < CountOfIntegers.length; k++) {
                int count = token.theToken.length() - token.theToken.replace(Integer.toString(k), "").length();
                CountOfIntegers[k] = count;
            }

            for (int j = 0; j < bestGuessStructures.length; j++) {

                if (isCurrentBestGuessStructureSameWithCountOfIntegers(bestGuessStructures[j], CountOfIntegers) == true) {
                    token.bestGuessStructureIndex = j;
                    break;
                }
            }

            tokens.add(token);
        }

        return tokens;
    }

    public ArrayList<String> GetAllPossibleFeedbacks()
    {
        ArrayList<String> feedbacks = new ArrayList<String>();

        for (int d1 = 0; d1 < validFeedbackItems.length; d1++){
            for (int d2 = 0; d2 < validFeedbackItems.length; d2++){

                if(validFeedbackItems[d1]+validFeedbackItems[d2]>validFeedbackItems.length-1){//i.e. (2, 5) is not a possible feedback since 2+5=7>6
                    continue;
                }

                if(validFeedbackItems[d2]<validFeedbackItems.length) {
                    if(validFeedbackItems[d1]<validFeedbackItems.length-2||validFeedbackItems[d2]==0) {
                        //if the first digit of the feedback is 5 or 6, the second digit of the feedback should be to 0
                        feedbacks.add((Integer.toString(validFeedbackItems[d1]) + Integer.toString(validFeedbackItems[d2])));

                    }
                }
            }
        }
        return feedbacks;
    }

    private int countAllTokensThatProvidesTheSameResponse(String possibleToken, String possibleResponse)
            //This method finds the tokens which provides the same response
    {
        int counter=0;
        for (int i = 0; i < allTokens.size(); i++)
        {
            String currentToken = allTokens.get(i).theToken;
            String result=compareGuessedTokenWithAnyToken(currentToken, possibleToken);

            if ( result.equals(possibleResponse)==true){
                counter++;
            }

        }
        return counter;
    }

    public String compareGuessedTokenWithAnyToken(String currentToken, String guessedToken) {
        char[] currentTokenChar = currentToken.toCharArray();
        char[] guessedTokenChar = guessedToken.toCharArray();

        int directHits = 0;
        int indirectHits = 0;
        boolean[] currentTokenTick = new boolean[validFeedbackItems.length - 1];
        Arrays.fill(currentTokenTick, false);
        boolean[] guessedTokenTick = new boolean[validFeedbackItems.length - 1];
        Arrays.fill(guessedTokenTick, false);

        // Check for direct hits
        for (int i = 0; i < validFeedbackItems.length - 1; i++) {
            if (currentTokenChar[i] == guessedTokenChar[i]) {
                directHits++;
                currentTokenTick[i] = true;
                guessedTokenTick[i] = true;
            }
        }

        // Check for indirect hits
        for (int i = 0; i < validFeedbackItems.length - 1; i++) {
            for (int j = 0; j < validFeedbackItems.length - 1; j++) {
                if (!currentTokenTick[i] && !guessedTokenTick[j] && currentTokenChar[i] == guessedTokenChar[j]) {
                    indirectHits++;
                    currentTokenTick[i] = true;
                    guessedTokenTick[j] = true;
                    break;
                }
            }
        }

        return Integer.toString(directHits) + Integer.toString(indirectHits);
    }



        public ArrayList<String> FindPossibleTokensForEachBestGuessStructure() //return one possible token for each BestGuessStructure
            //this method finds where the form of the token fits in the "bestGuessStructures"
            //i.e. token:111222---> fits the 330000 structure
            //i.e. token:235555---> fits the 411000 structure
            //we can basically think that it creates the upper side of the diagram in the video (11 and 12) but it chooses one token per  bestGuessStructures
    {
        ArrayList<String> possibleTokensForEachBestGuessStructure = new ArrayList<String>();


        boolean Exit=true;//?

        for (int j = 0; j < bestGuessStructures.length; j++){
            for (int i = 0; i < allTokens.size(); i++) {

                TokenStructure token = allTokens.get(i);

                if(token.bestGuessStructureIndex==j){
                    possibleTokensForEachBestGuessStructure.add(token.theToken);
                    break;
                }
            }
        }


        return possibleTokensForEachBestGuessStructure;
    }


    private bestGuessAndRemainingTokens FindTheBestGuessAndRemainingTokensForTheBestGuess(ArrayList<String> possibleTokensForEachBestGuessStructure ) {
        //it finds the "worst case scenario" (explained in the video)  for each one of the possible tokens then chooses the smallest one
        // smallest one belongs to the best guess
        //checks 11 tokens at a time

        ArrayList<Integer> foundNumberOfRemainingTokens = new ArrayList<Integer>();

        String foundToken = "";

        for (int i = 0; i < possibleTokensForEachBestGuessStructure.size(); i++) {

            ArrayList<Integer> currentNumberOfRemainingTokens = new ArrayList<Integer>();

            for (int j = 0; j < possibleFeedbacks.size(); j++) {
                int count = countAllTokensThatProvidesTheSameResponse(possibleTokensForEachBestGuessStructure.get(i), possibleFeedbacks.get(j));
                currentNumberOfRemainingTokens.add(count);
            }

            int maxCurrent = Collections.max(currentNumberOfRemainingTokens);
            int maxPrevious = 0;

            if(foundNumberOfRemainingTokens.size()!=0)
                maxPrevious=Collections.max(foundNumberOfRemainingTokens);

            if (foundNumberOfRemainingTokens.size() == 0 || maxCurrent < maxPrevious) {
                foundNumberOfRemainingTokens = currentNumberOfRemainingTokens;
                foundToken = possibleTokensForEachBestGuessStructure.get(i);
            }


        }

        bestGuessAndRemainingTokens returnvalue=new bestGuessAndRemainingTokens();
        returnvalue.bestGuess=foundToken;
        returnvalue.foundNumberOfRemainingTokens=foundNumberOfRemainingTokens;
        return  returnvalue;


    }


    private void RemoveAllTokensThatDoNotProvideTheSameResponse(String bestGuessToken, String userResponse)
    {
        int counter=0;
        for (int i = 0; i < allTokens.size(); i++)
        {
            String currentToken = allTokens.get(i).theToken;
            String result=compareGuessedTokenWithAnyToken(currentToken, bestGuessToken);

            if ( result.equals(userResponse)!=true){
                allTokens.remove(i);
                i--;
            }

        }
    }

    public void StartGame()
    {
        ArrayList<String> possibleTokensForEachBestGuessStructure ;
        Scanner input= new Scanner(System.in);
        int directHit=0;
        int prevdirectHit=0;
        bestGuessAndRemainingTokens bestGuessAndRemainingTokens=new bestGuessAndRemainingTokens();

        //ALGORİTHM STEP 2
        allTokens = GetAllpossibleTokensForEachBestGuessStructure();
        //ALGORİTHM STEP 3
        possibleFeedbacks=GetAllPossibleFeedbacks();

//main loop



        while(directHit!=validFeedbackItems.length-1){

            if(directHit>validFeedbackItems.length-1) {
                directHit=prevdirectHit;
                System.out.println("Guess: "+bestGuessAndRemainingTokens.bestGuess);
                continue;
            }

            //ALGORİTHM STEP 4
            possibleTokensForEachBestGuessStructure=FindPossibleTokensForEachBestGuessStructure();

            //ALGORİTHM STEP 5
            bestGuessAndRemainingTokens=FindTheBestGuessAndRemainingTokensForTheBestGuess(possibleTokensForEachBestGuessStructure);

            //ALGORİTHM STEP 6
            System.out.println("Guess: "+bestGuessAndRemainingTokens.bestGuess);

            //obtain user response
            //ALGORİTHM STEP 7
            System.out.println("Enter the amount of numbers that are right and in the right positions");
            directHit=input.nextInt();

            System.out.println("Enter the amount of numbers that are right but not in the right position");
            int indirectHit=input.nextInt();

            //ALGORİTHM STEP 8
            boolean isItInconsistant=false;
            for (int j = 0; j < possibleFeedbacks.size(); j++) {
                if(possibleFeedbacks.get(j).equals(Integer.toString(directHit)+Integer.toString(indirectHit))==true){
                    if(bestGuessAndRemainingTokens.foundNumberOfRemainingTokens.get(j)==0){
                        isItInconsistant=true;
                        break;
                    }
                }
            }

            if (isItInconsistant==true){
                System.out.println("INCONSISTENT FEEDBACK : Please check your current and previous answers. If you have been inconsistent in your previous answers you may need to restart the game! Otherwise make a consistent answer now please!");
                continue;
            }

            //ALGORİTHM STEP 9
            RemoveAllTokensThatDoNotProvideTheSameResponse(bestGuessAndRemainingTokens.bestGuess,directHit+""+indirectHit);

            System.out.println("Number of Remaining Tokens : "+allTokens.size());

            prevdirectHit=directHit;

        }

        System.out.println("Congratulations : "+bestGuessAndRemainingTokens.bestGuess);
    }

}