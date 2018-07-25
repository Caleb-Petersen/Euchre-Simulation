
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Created by Caleb Petersen
 * on June 15, 2018
 * To calculate statistics on euchre games played
 */
public class euchreSimulation {

    /**
     */
    public static ArrayList<card> allCards = new ArrayList();
    public static ArrayList<card> A = new ArrayList();
    public static ArrayList<card> B = new ArrayList();
    public static ArrayList<card> C = new ArrayList();
    public static ArrayList<card> D = new ArrayList();
    
    public static ArrayList<output> outputArray = new ArrayList();

    public static int playerCounter = 0;
    public static String trump, requiredSuit, leftSuit;
    public static int netHandValue, playerHandValue, teammateHandValue, opponentOneHandValue, opponentTwoHandValue, pickedUpCardValue, numberSuits, numberTrump, tricksWon = 0;
    public static boolean dataCompromized;
    
    public static void main(String[] args) {
        /*
        *Function purpose: To generate a .csv file that includes information on
        *the euchre games played. The number of games played can be varied by 
        *changing the number of times this function loops through proccess play
        */
        for (int i = 0; i < 1000; i++) {
            proccessPlay();
            /*if the data is compromised it is unlikely the dealer who have picked
            the card, so the data is excluded*/
            if (dataCompromized == false) {
                outputArray.add(new output(netHandValue, playerHandValue, teammateHandValue, opponentOneHandValue,
                        opponentTwoHandValue, pickedUpCardValue, numberSuits, numberTrump, tricksWon));
            }
            //Reset all of the variables so the next game is not "polluted"
            netHandValue = 0;
            playerHandValue = 0;
            teammateHandValue = 0;
            opponentOneHandValue = 0;
            opponentTwoHandValue = 0;
            pickedUpCardValue = 0;
            numberSuits = 0;
            numberTrump = 0;
            tricksWon = 0;
            A.clear();
            B.clear();
            C.clear();
            D.clear();
            allCards.clear();
            trump = "";
            requiredSuit = "";
            dataCompromized = false;

        }
        //Write to the .csv file using the information in the outputArray (information generated in proccessPlay())
        try {
            try (PrintWriter writer = new PrintWriter("euchre_data.csv")) {
                writer.println("Net Hand Value" + ',' + "Player Hand Value" + ',' + "Teammate Hand Value" + ',' + "Opponent One Hand Value" + ','
                        + "Opponent Two Hand Value" + ',' + "Card Value" + ',' + "Number of Suits" + ',' + "Number of Trump" + ',' + "Number of Tricks Won");
                
                for (int i = 0; i < outputArray.size(); i++) {
                    writer.println(String.valueOf(outputArray.get(i).netHandValue) + ',' + String.valueOf(outputArray.get(i).playerHandValue) + ','
                            + String.valueOf(outputArray.get(i).teammateHandValue) + ',' + String.valueOf(outputArray.get(i).opponentOneHandValue) + ','
                            + String.valueOf(outputArray.get(i).opponentTwoHandValue) + ',' + String.valueOf(outputArray.get(i).cardValue) + ','
                            + String.valueOf(outputArray.get(i).numberSuits) + ',' + String.valueOf(outputArray.get(i).numberTrump) + ','
                            + String.valueOf(outputArray.get(i).tricksWon));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(euchreSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void proccessPlay() {
        /*
        *Function purpose: To act as the first HQ of the project. The deck is 
        *initialized, some hand value statistics calculated, and the game play begun
        */
        //Find the card that will be in the kitty
        String cardSuit;
        int cardValue, randomNum, discardCardIndex;
        initDeck();

        randomNum = ThreadLocalRandom.current().nextInt(0, (allCards.size() - 1));
        cardSuit = allCards.get(randomNum).suit;
        cardValue = allCards.get(randomNum).val;

        //For now player A will permanately be the dealer
        trump = cardSuit;
        leftSuit = getSameColorSuit(trump);

        if (numberTrump(B) > 3 || numberTrump(D) > 3) {
            dataCompromized = true;
        }
        if (!(numberTrump(A) >= 1)) {
            dataCompromized = true;
        }
        if (dataCompromized == false) {
            discardCardIndex = discardCardDecider(A, cardSuit);
            allCards.add(new card(A.get(discardCardIndex).suit, A.get(discardCardIndex).val));
            
            A.remove(discardCardIndex);
            A.add(new card(cardSuit, cardValue));
            
            netHandValue = handValue(A, trump) + handValue(C, trump) - handValue(B, trump) - handValue(D, trump);
            playerHandValue = handValue(A, trump);
            teammateHandValue = handValue(C, trump);
            opponentOneHandValue = handValue(B, trump);
            opponentTwoHandValue = handValue(D, trump);
            pickedUpCardValue = cardValue(trump, cardSuit, cardValue);

            numberTrump = numberTrump(A) + numberTrump(C) - numberTrump(B) - numberTrump(D);
            numberSuits = numberSuits(A);

            if (dataCompromized == false) {
                
                gamePlay(B, C, D, A);
            }
        }
    }
    public static void gamePlay(ArrayList<card> firstToLay, ArrayList<card> second,
            ArrayList<card> third, ArrayList<card> fourth) {
        /*
        *Function purpose: To act as the HQ for all of the logic functions. The
        *cards to be laid are determined for each player, and the winner determined.
        *NOTE: This function is called until all cards have been laid.
        */
        if (!firstToLay.isEmpty()) {
            int randomNum;
            int cardRemovedFirst = 0, cardRemovedSecond = 0, cardRemovedThird = 0, cardRemovedFourth = 0;
            ArrayList<card> trick = new ArrayList();

            cardRemovedFirst = determineFirstPlay(firstToLay);
            trick.add(firstToLay.get(cardRemovedFirst));

            cardRemovedSecond = determinePlay(second, trick);
            trick.add(second.get(cardRemovedSecond));

            cardRemovedThird = determinePlay(third, trick);
            trick.add(third.get(cardRemovedThird));

            cardRemovedFourth = determinePlay(fourth, trick);
            trick.add(fourth.get(cardRemovedFourth));

            int highestValue = trick.get(0).val;
            int highestValueIndex = 0;

            if (pickHighestTrump(trick) != -1) {
                highestValueIndex = pickHighestTrump(trick);
                //highestValue = trick.get(highestValueIndex).val;
            } else {
                for (int i = 1; i < 4; i++) {
                    if (trick.get(i).suit.equals(requiredSuit)) {
                        if (trick.get(i).val >= highestValue) {
                            highestValue = trick.get(i).val;
                            highestValueIndex = i;
                        }
                    }
                }
            }

            //Removed the cards played
            firstToLay.remove(cardRemovedFirst);
            second.remove(cardRemovedSecond);
            third.remove(cardRemovedThird);
            fourth.remove(cardRemovedFourth);

            if (!firstToLay.isEmpty()) {
                switch (highestValueIndex) {
                    case 0:
                        if (firstToLay.equals(A) || firstToLay.equals(C)) {
                            tricksWon++;
                        }
                        gamePlay(firstToLay, second, third, fourth);
                        break;
                    case 1:
                        if (second.equals(A) || second.equals(C)) {
                            tricksWon++;
                        }
                        gamePlay(second, third, fourth, firstToLay);
                        break;
                    case 2:
                        if (third.equals(A) || third.equals(C)) {
                            tricksWon++;
                        }
                        gamePlay(third, fourth, firstToLay, second);
                        break;
                    default:
                        if (fourth.equals(A) || fourth.equals(C)) {
                            tricksWon++;
                        }
                        gamePlay(fourth, firstToLay, second, third);
                        break;
                }
            } else {
                switch (highestValueIndex) {
                    case 0:
                        if (firstToLay.equals(A) || firstToLay.equals(C)) {
                            tricksWon++;
                        }
                        break;
                    case 1:
                        if (second.equals(A) || second.equals(C)) {
                            tricksWon++;
                        }
                        break;
                    case 2:
                        if (third.equals(A) || third.equals(C)) {
                            tricksWon++;
                        }
                        break;
                    default:
                        if (fourth.equals(A) || fourth.equals(C)) {
                            tricksWon++;
                        }
                        break;
                }
            }
        }
    }
     public static int determinePlay(ArrayList<card> cardsHeld, ArrayList<card> trick) {
        /*
        *Function purpose: To determine what card a player should lay (though the
        *first card has already been laid). This is the most logic intensive function
        */
        int index = -1, partner = -1;

        if (trick.size() == 2) {
            partner = 0;
        } else if (trick.size() == 3) {
            partner = 1;
        }

        //Check to see if the player holds the suit required
        if (requiredSuitHand(cardsHeld, requiredSuit)) {
            //Is your partner winning the trick?
            if (winningTrick(trick) == partner) {
                //Make more complex to account for probability of partner winning  
                index = lowestRequiredSuit(cardsHeld);
            } else {
                //evaluate your chances of winning
                int highestCardHand = highestRequiredSuit(cardsHeld), highestCardTrick = highestRequiredSuit(trick);
                //determine whether or not you have the highest card of the trick
                if (winningRound(trick, cardsHeld.get(highestCardHand).val, cardsHeld.get(highestCardHand).suit)) {
                    index = highestCardHand;
                } else {
                    index = lowestRequiredSuit(cardsHeld);
                }

            }

            //If the suit laid is trump, check to see if you can win, otherwise lay the lowest trump
        } else if (requiredSuit.equals(trump)) {
            index = discardCardDecider(cardsHeld, trump);

        } else {
            if (winningTrick(trick) == partner) {
                index = discardCardDecider(cardsHeld, trump);
            } else {
                for (int i = 0; i < cardsHeld.size(); i++) {
                    if (isTrump(cardsHeld.get(i).suit, cardsHeld.get(i).val)) {
                        if (winningRound(trick, cardsHeld.get(i).val, cardsHeld.get(i).suit)) {
                            if (index == -1) {
                                index = i;
                            } else if (cardValue(trump, cardsHeld.get(i).suit, cardsHeld.get(i).val) < cardValue(trump, cardsHeld.get(index).suit, cardsHeld.get(index).val)) {
                                index = i;
                            }
                        }
                    }
                }
            }
            if (index == -1) {
                index = discardCardDecider(cardsHeld, trump);
            }
        }
        return index;
    }

    public static int determineFirstPlay(ArrayList<card> cardsHeld) {
        /*
        *Function purpose: To determine the best card for a player who is leading
        *to lay. 
        */
        int index, value = -1;
        float size = cardsHeld.size();
        float percentage = numberTrump / size;

        if (percentage >= 0.34) {
            index = pickLowestTrump(cardsHeld);
            requiredSuit = cardsHeld.get(index).suit;

        } else {

            index = pickHighestValue(cardsHeld);
            requiredSuit = cardsHeld.get(index).suit;
        }
        if ((cardsHeld.get(index).suit).equals(leftSuit) && cardsHeld.get(index).val == 11) {
            requiredSuit = trump;
        }
        return index;
    }

    public static int pickLowestTrump(ArrayList<card> c) {
        /*
        *Function purpose: To pick the lowest trump that a player has (index
        *returned)
        */
        int index = -1, value = 20;

        for (int i = 0; i < c.size(); i++) {
            if (isTrump(c.get(i).suit, c.get(i).val)) {
                if (cardValue(trump, c.get(i).suit, c.get(i).val) < value) {
                    index = i;
                    value = cardValue(trump, c.get(i).suit, c.get(i).val);
                }
            }
        }
        return index;
    }

    public static boolean isTrump(String suit, int val) {
        /*
        *Function purpose: To determine if a card is trump
        */
        if (suit.equals(trump) || (suit.equals(leftSuit) && val == 11)) {
            return true;
        }
        return false;
    }

    public static int numberSuits(ArrayList<card> c) {
        /*
        *Function purpose: to collect information on the number of suits that 
        *a player has in their hand. This information is only used in the statistics
        */
        boolean[] suits = new boolean[4];
        int total = 0;
        for (int i = 0; i < 4; i++) {
            suits[i] = false;
        }

        for (int i = 0; i < c.size(); i++) {
            String suit = c.get(i).suit;
            int val = c.get(i).val;
            if (suit.equals("DIAMONDS")) {
                if (val == 11 && leftSuit.equals(suit)) {
                    suits[0] = true;
                } else {
                    suits[1] = true;
                }
            } else if (suit.equals("HEARTS")) {
                if (val == 11 && leftSuit.equals(suit)) {
                    suits[1] = true;
                } else {
                    suits[0] = true;
                }
            } else if (suit.equals("SPADES")) {
                if (val == 11 && leftSuit.equals(suit)) {
                    suits[2] = true;
                } else {
                    suits[3] = true;
                }
            } else {
                if (val == 11 && leftSuit.equals(suit)) {
                    suits[3] = true;
                } else {
                    suits[2] = true;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            if (suits[i]) {
                total++;
            }
        }
        return total;
    }

    public static int pickHighestTrump(ArrayList<card> c) {
        /*
        *Function purpose: to pick the highest trump in a hand (index of hand returned)
        */
        int index = -1, value = -1;

        for (int i = 0; i < c.size(); i++) {
            if (isTrump(c.get(i).suit, c.get(i).val)) {
                if (cardValue(trump, c.get(i).suit, c.get(i).val) > value) {
                    index = i;
                    value = cardValue(trump, c.get(i).suit, c.get(i).val);
                }
            }
        }
        return index;
    }


    public static int pickLowestValue(ArrayList<card> c) {
        /*
        *Function purpose: to pick the lowest valued card (index of hand returned)
        */
        int index = 0;
        int value = cardValue(trump, c.get(0).suit, c.get(0).val);

        for (int i = 0; i < c.size(); i++) {
            if (cardValue(trump, c.get(i).suit, c.get(i).val) < value) {
                index = i;
                value = cardValue(trump, c.get(i).suit, c.get(i).val);
            } else if (cardValue(trump, c.get(i).suit, c.get(i).val) == value && c.get(i).val < c.get(index).val) {
                index = i;
                value = cardValue(trump, c.get(i).suit, c.get(i).val);
            }
        }
        return index;
    }

    public static int pickHighestValue(ArrayList<card> hand) {
        /*
        *Function purpose: to pick the highest valued card (index of hand returned)
        */
        int index = -1, value = -1;
        for (int i = 0; i < hand.size(); i++) {
            if (!isTrump(hand.get(i).suit, hand.get(i).val)) {
                if ((hand.get(i).val > value) && !(leftSuit.equals(hand.get(i).suit) && hand.get(i).val == 11)) {
                    index = i;
                    value = hand.get(i).val;
                }
            }
        }
        return index;
    }

    public static boolean requiredSuitHand(ArrayList<card> cardsHeld, String suitRequired) {
        /*
        *Function purpose: to let a player know if they must select a card of 
        *the same suit that was first laid 
        */
        for (int i = 0; i < cardsHeld.size(); i++) {
            if (isRequiredSuit(cardsHeld.get(i).suit, cardsHeld.get(i).val)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRequiredSuit(String suit, int val) {
        /*
        *Function purpose: To determine whether or not a card is of the suit that
        *is required to be played
        */
        if ((suit.equals(requiredSuit) && !(suit.equals(leftSuit) && val == 11)) || (requiredSuit.equals(trump) && val == 11 && leftSuit.equals(suit))) {
            return true;
        }
        return false;
    }

    public static boolean winningRound(ArrayList<card> trick, int val, String suit) {
        /*
        *Function purpose: To let a player know if they will be winning a trick
        *if they lay a specific card
        */
        for (int i = 0; i < trick.size(); i++) {
            //if the card in the trick is trump and the card considered is trump
            if (isTrump(trick.get(i).suit, trick.get(i).val) && isTrump(suit, val)) {
                if (cardValue(trump, trick.get(i).suit, trick.get(i).val) > cardValue(trump, suit, val)) {
                    return false;
                }
            } else if (isTrump(trick.get(i).suit, trick.get(i).val)) {
                return false;
            } else if (!isRequiredSuit(suit, val) && !isTrump(suit, val)) {
                return false;
            } else if (isRequiredSuit(trick.get(i).suit, trick.get(i).val) && isRequiredSuit(suit, val)) {
                if (cardValue(trump, trick.get(i).suit, trick.get(i).val) > cardValue(trump, suit, val)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int numberTrump(ArrayList<card> hand) {
        /*
        *Function purpose: To determine the number of trump in a hand
        */
        int counter = 0;
        for (int i = 0; i < hand.size(); i++) {
            if ((trump.equals(hand.get(i).suit) || (leftSuit.equals(hand.get(i).suit) && hand.get(i).val == 11))) {
                counter++;
            }
        }
        return counter;
    }

    public static int winningTrick(ArrayList<card> trick) {
        /*
        *Function purpose: To determine which player is winning the trick. The
        *player is identified based off of the index of the trick
        */
        int value = -1, index = -1;
        for (int i = 0; i < trick.size(); i++) {
            if (isRequiredSuit(trick.get(i).suit, trick.get(i).val) || isTrump(trick.get(i).suit, trick.get(i).val)) {
                if (cardValue(trump, trick.get(i).suit, trick.get(i).val) > value) {
                    index = i;
                    value = cardValue(trump, trick.get(i).suit, trick.get(i).val);
                }
            }
        }
        return index;
    }

    public static int discardCardDecider(ArrayList<card> hand, String trump) {
        /*
        *Function purpose: To determine which card to discard as the dealer.It is 
        *also used as a way to decide which card is of lowest value when a player
        *is unable to win a trick.
        *NOTE: It is assumed that the kitty card is the 6th card (so index 5)
        */
        int lowestValue = cardValue(trump, hand.get(0).suit, hand.get(0).val);
        int index = 0;
        for (int i = 1; i < hand.size(); i++) {
            if (cardValue(trump, hand.get(i).suit, hand.get(i).val) < lowestValue) {
                lowestValue = cardValue(trump, hand.get(i).suit, hand.get(i).val);
                index = i;
            } else if (cardValue(trump, hand.get(i).suit, hand.get(i).val) == lowestValue && hand.get(i).val < hand.get(index).val) {
                lowestValue = cardValue(trump, hand.get(i).suit, hand.get(i).val);
                index = i;
            }
        }
        return index;
    }

    public static int lowestRequiredSuit(ArrayList<card> cardsHeld) {
        /*
        *Function purpose: To return the index of the lowest valued card of the suit
        *that has to be played (required by the suit of the first card played)
        */
        int value = 100, index = -1;
        for (int i = 0; i < cardsHeld.size(); i++) {
            if (isRequiredSuit(cardsHeld.get(i).suit, cardsHeld.get(i).val)) {
                if (cardValue(trump, cardsHeld.get(i).suit, cardsHeld.get(i).val) < value) {
                    index = i;
                    value = cardValue(trump, cardsHeld.get(i).suit, cardsHeld.get(i).val);
                } else if (cardValue(trump, cardsHeld.get(i).suit, cardsHeld.get(i).val) == value) {
                    if (cardsHeld.get(i).val < cardsHeld.get(index).val) {
                        index = i;
                        value = cardValue(trump, cardsHeld.get(i).suit, cardsHeld.get(i).val);
                    }
                }
            }
        }
        return index;
    }

    public static int highestRequiredSuit(ArrayList<card> cardsHeld) {
        /*
        *Function purpose: To return the index of the highest valued card of the suit
        *that has to be played (required by the suit of the first card played)
        */
        int value = -1, index = -1;
        for (int i = 0; i < cardsHeld.size(); i++) {
            if (isRequiredSuit(cardsHeld.get(i).suit, cardsHeld.get(i).val)) {
                if (cardValue(trump, cardsHeld.get(i).suit, cardsHeld.get(i).val) > value) {
                    index = i;
                    value = cardValue(trump, cardsHeld.get(i).suit, cardsHeld.get(i).val);
                } else if (cardValue(trump, cardsHeld.get(i).suit, cardsHeld.get(i).val) == value) {
                    if (cardsHeld.get(i).val > cardsHeld.get(index).val) {
                        index = i;
                        value = cardValue(trump, cardsHeld.get(i).suit, cardsHeld.get(i).val);
                    }
                }
            }
        }
        return index;
    }

    public static int handValue(ArrayList<card> hand, String trump) {
        /*
        *Function purpose: To calculate a player's hand strength. This hand strength
        *is collected and exported to the .csv file.
        */
        String sameColor, suit;
        int handValue, val;
        handValue = 0;
        sameColor = getSameColorSuit(trump);

        for (int i = 0; i < hand.size(); i++) {
            val = hand.get(i).val;
            suit = hand.get(i).suit;
            if (suit.equals(trump)) {
                switch (val) {
                    case 14:
                        /*trump ace*/
                        handValue += 10;
                        break;
                    case 13:
                        /*trump king*/
                        handValue += 8;
                        break;
                    case 12:
                        /*trump queen*/
                        handValue += 7;
                        break;
                    case 11:
                        /*trump jack*/
                        handValue += 12;
                        break;
                    case 10:
                        /*trump ten*/
                        handValue += 5;
                        break;
                    case 9:
                        /*trump nine*/
                        handValue += 4;
                        break;
                    default:
                        break;
                }
            } else if (suit.equals(sameColor) && val == 11) {
                handValue += 11;
            } else {
                switch (val) {
                    case 14:
                        /*random ace*/
                        handValue +=6;
                        break;
                    case 13:
                        /*random king*/
                        handValue += 2;
                        break;
                    default:
                        break;
                }
            }
        }
        return handValue;
    }
    
    public static int cardValue(String trump, String suit, int val) {
        /*
        *Function Purpose: To return a card value (as oppposed to card.val) that accounts 
        *for which cards are trump. This is extrememly important for the left in particular
        */
        String sameColor = getSameColorSuit(trump);
        int cardValue = 0;

        if (suit.equals(trump)) {
            switch (val) {
                case 14:
                    /*trump ace*/
                    return 10;
                case 13:
                    /*trump king*/
                    return 8;
                case 12:
                    /*trump queen*/
                    return 7;
                case 11:
                    /*trump jack*/
                    return 12;
                case 10:
                    /*trump ten*/
                    return 5;
                case 9:
                    /*trump nine*/
                    return 4;
                default:
                    return 0;
            }
        } else if (suit.equals(sameColor) && val == 11) {
            return 11;
        } else {
            switch (val) {
                case 14:
                    /*random ace*/
                    return 6;
                case 13:
                    /*random king*/
                    return 2;
            }
        }

        return cardValue;
    }

    public static String toString(String suit, int value) {
        /*
        *Function Purpose: To convert a card (suit and value) to a single string name
        *Parameters: A card (suit and value)
        */
        String ret;
        switch (value) {
            case 9:
                ret = "nine";
                break;
            case 10:
                ret = "ten";
                break;
            case 11:
                ret = "jack";
                break;
            case 12:
                ret = "queen";
                break;
            case 13:
                ret = "king";
                break;
            case 14:
                ret = "ace";
                break;
            default:
                ret = "NOT VALID";

        }
        ret += "" + suit;
        return ret;
    }

    public static String getSameColorSuit(String trump) {
        /*
        *Function Purpose: To return the suit that is the same colour as the current
        *trump is. This is used to determine which card is the left
        */
        String sameColor;
        switch (trump) {

            case "CLUBS":
                sameColor = "SPADES";
                break;
            case "SPADES":
                sameColor = "CLUBS";
                break;
            case "HEARTS":
                sameColor = "DIAMONDS";
                break;
            default:
                sameColor = "HEARTS";
                break;
        }
        return sameColor;
    }
    
    public static ArrayList<card> dealCards() {
        /*
        *Function Purpose: To give each player a 5 card randomly generated hand.
        */
        ArrayList<card> player = new ArrayList();
        String cardSuit;
        int cardValue, randomNum;

        for (int i = 0; i < 5; i++) {
            randomNum = ThreadLocalRandom.current().nextInt(0, (allCards.size() - 1));
            cardSuit = allCards.get(randomNum).suit;
            cardValue = allCards.get(randomNum).val;

            player.add(new card(cardSuit, cardValue));
            allCards.remove(randomNum);
        }

        return player;
    }

    public static void initDeck() {
        /*
        *Function Purpose: To initialize the deck and deal hands to all players
        */
        for (int x = 9; x < 15; x++) {
            allCards.add(new card("CLUBS", x));
            allCards.add(new card("DIAMONDS", x));
            allCards.add(new card("HEARTS", x));
            allCards.add(new card("SPADES", x));
        }

        A = dealCards();
        B = dealCards();
        C = dealCards();
        D = dealCards();
    }
    
    public static class card {
    //This is the main class for the data structure. It holds a card (suit and integer value)  

        String suit;
        int val;

        card(String _suit, int _val) {
            suit = _suit;
            val = _val;
        }
    }

    public static class output {
        //This is the format for the output of the data
        int netHandValue, playerHandValue, teammateHandValue, opponentOneHandValue, opponentTwoHandValue, cardValue, numberSuits, numberTrump, tricksWon;

        output(int _netHandValue, int _playerHandValue, int _teammateHandValue, int _opponentOneHandValue, int _opponentTwoHandValue,
                int _cardValue, int _numberSuits, int _numberTrump, int _tricksWon) {
            netHandValue = _netHandValue;
            playerHandValue = _playerHandValue;
            teammateHandValue = _teammateHandValue;
            opponentOneHandValue = _opponentOneHandValue;
            opponentTwoHandValue = _opponentTwoHandValue;
            cardValue = _cardValue;
            numberSuits = _numberSuits;
            numberTrump = _numberTrump;
            tricksWon = _tricksWon;
        }
    }
}
