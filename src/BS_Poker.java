import java.io.*;
import java.util.*;
import java.lang.Math;

class BS_Poker {
    public int numPlayers;
    public int round;
    public int value;

    static final Scanner scanner = new Scanner(System.in);

    ArrayList<Player> Players = new ArrayList<Player>();
    ArrayList<Card> Deck = new ArrayList<Card>();
    ArrayList<Card> GameDeck = new ArrayList<Card>();

    String[] Suits = {"Clubs", "Diamonds", "Hearts", "Spades"};
    String[] Ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

    public static void main(String args[]) {
        BS_Poker newGame = new BS_Poker();
        newGame.GameRun();
    }

    // Main game manager
    public void GameRun() {
        System.out.print("\nHow many players? (1-8): ");
        numPlayers = scanner.nextInt();
        boolean selected = false;
        while(!selected) {
            if(numPlayers > 8) {
                System.out.print("Too many players.\nReenter number of players (1-8): ");
            } else {
                selected = true;
            }
        }

        // Run game
        round = 1;
        int currentPlayer = 0;
        boolean gameOver = false;

        while(!gameOver) {
            System.out.println("\nROUND " + round);
            newRound();
            round++;
            value = 0;
            int turn = 1;
            int prevHand = 0;
            boolean roundOver = false;

            // Run round
            while(!roundOver) {
                System.out.print("Turn #" + turn + "\n");
                System.out.println(Players.toString() + "\n");
                turn++;
                currentPlayer = nextPlayer(currentPlayer);

                // Get hand input
                prevHand = value;
                boolean isGreater = false;
                while(!isGreater) {
                    handSelect(currentPlayer);
                    System.out.println("Hand Value: " + value);
                    // Different check for full, complex, 6-7-8
                    if((value > 70000 & value < 75000) || (value > 120000)) {
                        if(value > prevHand) {
                            isGreater = true;
                        } else {
                            System.out.println("\nChoose hand larger than previous.");
                        }
                    } else {
                        if(value > prevHand + 50) {
                            isGreater = true;
                        } else {
                            System.out.println("\nChoose hand larger than previous.");
                        }
                    }
                }

                System.out.print("\nCall BS? (Player # or 'N'): ");
                scanner.nextLine();
                String bsCall = scanner.nextLine();
                if(isInt(bsCall)) {
                    int bsPlayer = Integer.parseInt(bsCall);
                    if(bsPlayer >= 1 && bsPlayer <= numPlayers) {
                        for(Player p : Players) {
                            for(Card c : p.cards) {
                                GameDeck.add(c);
                            }
                        }
                        ArrayList<Card> output = handExists();
                        //System.out.println("BS Player: " + bsPlayer); //BS Sequence
                        //System.out.println("Current Player: " + currentPlayer);
                        roundOver = true;
                    }
                }

            }
        }

        //Players.get(2).numCards += 1;
    }

    // Reset cards
    public void newRound() {
        if(round > 1) {
            for(int i = 0; i < Players.size(); i++) // Clear previous round cards
                Players.get(i).cards.clear();
            Deck.clear();
        } else {
            for(int i = 1; i <= numPlayers; i++)    // Add players
                Players.add(new Player(i));
        }
        prepareDeck();
        dealCards();
        middleCards();
    }

    // Add cards and shuffle deck
    public void prepareDeck() {
        for(int suit = 0; suit < 4; suit++)
            for(int rank = 0; rank < 13; rank++)
                Deck.add(new Card(Suits[suit], Ranks[rank]));
        Collections.shuffle(Deck);
    }

    // Deal number of cards to all players specified by numCards variable
    public void dealCards() {
        for(int i = 0; i < Players.size(); i++) {
            Player player = Players.get(i);
            for(int j = 0; j < player.numCards; j++)
                player.addCard(Deck.remove(0));
            Players.set(i, player);
        }
    }

    // Place cards in middle depending on number of players
    public void middleCards() {
        if(GameDeck.size() != 0) {
            GameDeck.clear();
        }
        if(numPlayers <= 3) {
            GameDeck.add(Deck.remove(0));
            GameDeck.add(Deck.remove(0));
            GameDeck.add(Deck.remove(0));
        } else if(numPlayers <= 5) {
            GameDeck.add(Deck.remove(0));
            GameDeck.add(Deck.remove(0));
        } else {
            GameDeck.add(Deck.remove(0));
        }
        System.out.println("Middle Cards: " + GameDeck.toString());
    }

    // Determines next player
    public int nextPlayer(int currentPlayer) {
        if(currentPlayer == numPlayers) {
            return 1;
        } else {
            return currentPlayer + 1;
        }
    }

    // BS Prompt Decoder
    public static boolean isInt(String s) {
        boolean isInteger = false;
        try {
            // Is an integer
            Integer.parseInt(s);
            isInteger = true;
        } catch (NumberFormatException ex) {
            // Not an integer
        }
        return isInteger;
    }

    // Select a hand
    public void handSelect(int playerNum) {
        // Selects hand
        System.out.println("\n1[High]     2[Pair] 3[2 Pair]      4[3 of a kind]     5[Flush]");
        System.out.println("6[Straight] 7[Full] 8[4 of a Kind] 9[Straight Flush] 10[Royal Flush]");
        System.out.println("11[5 of a Kind]    12[Complex]    13[Complex 6-7-8]");
        System.out.print("\nPlayer " + playerNum + ", please select a hand: ");
        boolean selected = false;
        int hand = 0;
        while(!selected) {
            hand = scanner.nextInt();
            if(hand > 13 || hand < 1) {
                System.out.print("Invalid response.\nSelect another hand: ");
            } else {
                selected = true;
            }
        }

        // Selects rank/suit as needed
        value = 0;
        int suit = 0;
        int rank = 0;
        int rank2 = 0;
        int rank3 = 0;
        boolean validHand = false;
        switch(hand) {
            case 1: // High | 10300-11400 (1[high]00)
                displayRanks();
                System.out.print("\nSelect a rank: ");
                while(!validHand) {
                    rank = scanner.nextInt();
                    if(rank < 3 || rank > 14) {
                        System.out.print("\nInvalid response.\nSelect another rank: ");
                    } else {
                        value = 10000 + rank*100;
                        validHand = true;
                    }
                }
                break;
            case 2: // Pair | 20300-21400 (2[pair]00)
                displayRanks();
                System.out.print("\nSelect a rank: ");
                while(!validHand) {
                    rank = scanner.nextInt();
                    if(rank < 3 || rank > 14) {
                        System.out.print("\nInvalid response.\nSelect another rank: ");
                    } else {
                        value = 20000 + rank*100;
                        validHand = true;
                    }
                }
                break;
            case 3: // 2 Pair | 30300-31400 (3[high pair]00)
                displayRanks();
                System.out.print("\nSelect a rank: ");
                while(!validHand) {
                    rank = scanner.nextInt();
                    if(rank < 3 || rank > 14) {
                        System.out.print("\nInvalid response.\nSelect another rank: ");
                    } else {
                        System.out.print("\nSelect second rank: ");
                        rank2 = scanner.nextInt();
                        if(rank2 < 3 || rank2 > 14 || rank == rank2) {
                            System.out.print("\nInvalid response.\nSelect first rank again: ");
                        } else {
                            value = rank>rank2 ? rank:rank2;
                            value = value*100 + 30000;
                            validHand = true;
                        }
                    }
                }
                break;
            case 4: // 3 of a kind | 40300-41400 (4[3kind]00)
                displayRanks();
                System.out.print("\nSelect a rank: ");
                while(!validHand) {
                    rank = scanner.nextInt();
                    if(rank < 3 || rank > 14) {
                        System.out.print("\nInvalid response.\nSelect another rank: ");
                    } else {
                        value = 40000 + rank*100;
                        validHand = true;
                    }
                }
                break;
            case 5: // Flush | 50001-50004 (5000[suit])
                displaySuits();
                System.out.print("\nSelect a suit: ");
                while(!validHand) {
                    suit = scanner.nextInt();
                    if(suit < 1 || suit > 4) {
                        System.out.print("\nInvalid response.\nSelect another suit: ");
                    } else {
                        value = 50000 + suit;
                        validHand = true;
                    }
                }
                break;
            case 6: // Straight | 60700-61400 (6[high5]00)
                displayRanks();
                System.out.print("\nSelect the highest of 5 cards rank: ");
                while(!validHand) {
                    rank = scanner.nextInt();
                    if(rank < 7 || rank > 14) {
                        System.out.print("\nInvalid response.\nSelect another rank: ");
                    } else {
                        value = 60000 + rank*100;
                        validHand = true;
                    }
                }
                break;
            case 7: // Full | 70303-71414 (7[3kind][pair])
                displayRanks();
                System.out.print("\nSelect 3 of a kind rank: ");
                while(!validHand) {
                    rank = scanner.nextInt();
                    if(rank < 3 || rank > 14) {
                        System.out.print("\nInvalid response.\nSelect another rank: ");
                    } else {
                        System.out.print("\nSelect pair rank: ");
                        rank2 = scanner.nextInt();
                        if(rank2 < 3 || rank2 > 14 || rank == rank2) {
                            System.out.print("\nInvalid response.\nSelect first rank again: ");
                        } else {
                            value = 70000 + rank*100 + rank2;
                            validHand = true;
                        }
                    }
                }
                break;
            case 8: // 4 of a kind | 80300-81400 (8[4kind]00)
                displayRanks();
                System.out.print("\nSelect a rank: ");
                while(!validHand) {
                    rank = scanner.nextInt();
                    if(rank < 3 || rank > 14) {
                        System.out.print("\nInvalid response.\nSelect another rank: ");
                    } else {
                        value = 80000 + rank*100;
                        validHand = true;
                    }
                }
                break;
            case 9: // Straight flush | 90701-91404 (9[high][suit])
                displaySuits();
                System.out.print("\nSelect a suit: ");
                while(!validHand) {
                    suit = scanner.nextInt();
                    if(suit < 1 || suit > 4) {
                        System.out.print("\nInvalid response.\nSelect another suit: ");
                    } else {
                        displayRanks();
                        System.out.print("\nSelect the highest of 5 cards rank: ");
                        rank = scanner.nextInt();
                        if(rank < 7 || rank > 14) {
                            System.out.print("\nInvalid response.\nSelect suit again: ");
                        } else {
                            value = 90000 + rank*100 + suit;
                            validHand = true;
                        }
                    }
                }
                break;
            case 10: // Royal flush | 100001-100004 (1000[suit])
                displaySuits();
                System.out.print("\nSelect a suit: ");
                while(!validHand) {
                    suit = scanner.nextInt();
                    if(suit < 1 || suit > 4) {
                        System.out.print("\nInvalid response.\nSelect another suit: ");
                    } else {
                        value = 100000 + suit;
                        validHand = true;
                    }
                }
                break;
            case 11: // 5 of a kind | 110300-111400 (11[5kind]00)
                displayRanks();
                System.out.print("\nSelect a rank: ");
                while(!validHand) {
                    rank = scanner.nextInt();
                    if(rank < 3 || rank > 14) {
                        System.out.print("\nInvalid response.\nSelect another rank: ");
                    } else {
                        value = 110000 + rank*100;
                        validHand = true;
                    }
                }
                break;
            case 12: // Complex | 120303-121414 (12[5kind][4kind])
                displayRanks();
                System.out.print("\nSelect 5 of a kind rank: ");
                while(!validHand) {
                    rank = scanner.nextInt();
                    if(rank < 3 || rank > 14) {
                        System.out.print("\nInvalid response.\nSelect another rank: ");
                    } else {
                        System.out.print("\nSelect 4 of a kind rank: ");
                        rank2 = scanner.nextInt();
                        if(rank2 < 3 || rank2 > 14 || rank == rank2) {
                            System.out.print("\nInvalid response.\nSelect first rank again: ");
                        } else {
                            value = 120000 + rank*100 + rank2;
                            validHand = true;
                        }
                    }
                }
                break;
            case 13: // 6-7-8 | 13030303-13141414 (13[8kind][7kind][6kind])
                displayRanks();
                System.out.print("\nSelect 8 of a kind rank: ");
                while(!validHand) {
                    rank = scanner.nextInt();
                    if(rank < 3 || rank > 14) {
                        System.out.print("\nInvalid response.\nSelect another rank: ");
                    } else {
                        System.out.print("\nSelect 7 of a kind rank: ");
                        rank2 = scanner.nextInt();
                        if(rank2 < 3 || rank3 > 14 || rank == rank2) {
                            System.out.print("\nInvalid response.\nSelect first rank again: ");
                        } else {
                            System.out.print("\nSelect 6 of a kind rank: ");
                            rank3 = scanner.nextInt();
                            if(rank3 < 3 || rank3 > 14 || rank3 == rank || rank3 == rank2) {
                                System.out.print("\nInvalid response.\nSelect first rank again: ");
                            } else {
                                value = 13000000 + rank*10000 + rank2*100 + rank3;
                                validHand = true;
                            }
                        }
                    }
                }
                break;
            default:
                System.out.print("\nInvalid response. Select another hand: ");
                break;
        }
    }

    public void displaySuits() {
        System.out.println("1[Clubs]  2[Diamonds]  3[Hearts]  4[Spades]");
    }

    public void displayRanks() {
        System.out.println("3[3]   4[4]   5[5]   6[6]   7[7]   8[8]");
        System.out.println("9[9]  10[10] 11[J]  12[Q]  13[K]  14[A]");
    }

    // Checks if called for hand exists in play
    public ArrayList<Card> handExists() {
        ArrayList<Card> output = new ArrayList<Card>();
        int handID = value/10000;
        int failCount = 0;
        int suit = 0;
        int rank = 0;
        int rank2 = 0;
        int rank3 = 0;
        System.out.println("Hand ID: " + handID);
        switch(handID) {
            case 1: // High | 10300-11400 (1[high]00)
                rank = value/100-100;
                // Checks if card of rank is in GameDeck
                for(Card card : GameDeck) {
                    if(card.getRankInt() == rank) {
                        System.out.println("Added card " + card.getRankInt());
                        output.add(card);
                        break;
                    }
                }
                failCount = 1 - output.size();  //Used to factor in wild card (2)
                System.out.println("Fail count: " + failCount);
                break;
            case 2: // Pair | 20300-21400 (2[pair]00)
                rank = value/100-200;
                int counter = 0;
                for(Card card : GameDeck) {
                    if(card.getRankInt() == rank) {
                        output.add(card);
                        counter++;
                        if(counter == 2) { break; }
                    }
                }
                failCount = 2 - output.size();
                System.out.println("Fail count: " + failCount);
                break;
            case 3: // 2 Pair | 30300-31400 (3[high pair]00)
                break;
            case 4: // 3 of a kind | 40300-41400 (4[3kind]00)
                break;
            case 5: // Flush | 50001-50004 (5000[suit])

                break;
            case 6: // Straight | 60700-61400 (6[high5]00)
                break;
            case 7: // Full | 70303-71414 (7[3kind][pair])
                break;
            case 8: // 4 of a kind | 80300-81400 (8[4kind]00)
                break;
            case 9: // Straight flush | 90701-91404 (9[high][suit])
                break;
            case 10: // Royal flush | 100001-100004 (1000[suit])
                break;
            case 11: // 5 of a kind | 110300-111400 (11[5kind]00)
                break;
            case 12: // Complex | 120303-121414 (12[5kind][4kind])
                break;
            default: // 6-7-8 | 13030303-13141414 (13[8kind][7kind][6kind])
                break;
        }

        return output;
    }
}



class Card {
    String suit;
    String rank;
    boolean checked = false;

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public int getSuitInt() {
        switch(suit) {
            case "Clubs":
                return 1;
            case "Diamonds":
                return 2;
            case "Hearts":
                return 3;
            case "Spades":
                return 4;
        }
        return 0;
    }

    public int getRankInt() {
        int rankInt = 0;
        if(isInt(rank)) {
            rankInt = Integer.parseInt(rank);
        } else {
            switch(rank) {
                case "J":
                    rankInt = 11;
                    break;
                case "Q":
                    rankInt = 12;
                    break;
                case "K":
                    rankInt = 13;
                    break;
                case "A":
                    rankInt = 14;
                    break;
            }
        }
        return rankInt;
    }

    public static boolean isInt(String s) {
        boolean isInteger = false;
        try {
            // Is an integer
            Integer.parseInt(s);
            isInteger = true;
        } catch (NumberFormatException ex) {
            // Not an integer
        }
        return isInteger;
    }


    public String toString() {
        return("[" + rank + " " + suit + "]");
    }
}

class Player {
    int playerNum;
    int numCards = 1;

    ArrayList<Card> cards = new ArrayList<Card>();

    public Player(int playerNum) {
        this.playerNum = playerNum;
    }

    public void addCard(Card newCard) {
        cards.add(newCard);
        //numCards++;  if not doing +1 in GameRun
    }

    public void removeCards() {
        cards.clear();
    }

    public String toString() {
        String output = "Player " + playerNum + ": ";
        for(int i = 0; i < cards.size(); i++) {
            output = output + cards.get(i).toString();

            if(i < cards.size() - 1)
                output += " ";
        }
        return output;
    }
}
