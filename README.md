# Euchre-Simulation

This euchre simulation was used to calculated the probability of winning (or perhaps more specifically the probability of gaining points in) a game of euchre if the dealer picks up. Values were assigned to each card, and the correlation between the net hand value (the sum of the values of the cards in a hand) and winning was found. If the dealer would reach a hand value of 17 or greater by picking up, it is statistically advantageous to pick up. The hand value can be calculated by summing up the values of the individual cards as assigned in the program. 
The simulation used four computer players that each used the same logic to determine which card to lay. Ten-thousand games were run giving a total of 8059 results. Simulations were considered invalid if it was determined that a player would have a hand strong enough to order up the dealer with, or the dealer had no card of the same suit as the one that was flipped over. 
The value of a card is loosely based on the probability that a given card will win a trick (though it is not strictly tied to it).

![Alt text](Images/statistics.jpg?raw=true "Statistics")
![Alt text](Images/tricksHandValue.jpg?raw=true "Hand Value")

The scatter plot above shows what the net hand value for each of the possible number of tricks won by the dealer and his partner. It is clear that the dealer’s team wins more tricks when the net hand value is higher. The net hand value has a correlation of 0.69 with the number of tricks won. As the net value of a hand increases, the dealer’s probability of winning also increases. The correlation is much stronger when taking into account the net value as opposed to the dealer’s hand value. This is because the hand value of your partner and opponents drastically affects the results. Fortunately, the probabilities of the net value being above or below a threshold can be calculated based on the dealer’s hand value. Other possible factors that were examined and rejected as being the main factor of correlation were the number of trump (correlation = 0.52), and the number of suits the dealer has (correlation = -0.31).

![Alt text](Images/histogramTricksWon.jpg?raw=true "Histogram")

As can be seen in the histogram above, the dealer’s team won the vast majority of the games. In fact, they won 88% of the games played. It was also discovered that when the net hand value was greater than zero, the dealer and their partner won 95.7% of the time while when it was less than 0, they won 61.6% of the time. These probabilities were found by creating a histogram of the net hand values when the dealer and their partner won. From that histogram it is possible to count the number of cases where the net hand value was greater than or equal to zero when the dealer’s team won, and divide that by the total number of cases in the range being considered. This gives the percentage of games above the threshold of the net hand value being greater than or equal to zero that were won by the dealer’s team. 

From the analysis of the data, the following conclusions were reached about how the dealer should make their decision to pick up the card based on their hand.

![Alt text](Images/expectedValue.jpg?raw=true "Dealer")

The expected value of a round of play based on the dealer’s hand value

According to these expected point value results, the dealer and his partner will gain points when the dealer makes trump on a hand value with a value greater than 18.
While for the specific simulation run these results are expected to be correct, they do not take into account multiple playing styles, or how different playing styles interact. Future steps could include making the program work for multiple types of players, as well as looking into the probabilities of winning when making it trump without picking up the extra card. 

