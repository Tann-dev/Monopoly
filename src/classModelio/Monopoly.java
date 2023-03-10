package classModelio;

import classModelio.tile.PropertyTile;
import classModelio.tile.StartTile;
import classModelio.tile.StreetTile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Monopoly {

    public boolean debug = false;
    private int nbHouses = 32;

    private int nbHotels = 12;

    private List<Player> players;

    private Player currentPlayer;

    public Monopoly(List<Player> players, StartTile startTile) {
        if(!players.isEmpty()){
            this.players = players;
            for (Player player : players) {
                player.setTile(startTile);
            }
        }
    }

    public void rollDice() {
        int diceResult = 0;

        System.out.println("Lancez les dés (appuyez sur entrée)");
        if(this.debug) {
            System.out.println("Mode DEBUG, entrez votre avancement");
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            diceResult = Integer.parseInt(br.readLine());
        } catch (Exception e) {
            System.out.println("Caractère invalide");
        }

        if(!this.debug) {
            diceResult = new Random().nextInt(11) + 1;
        }

        currentPlayer.advance(diceResult);
    }

    public void buyConstruction(StreetTile streetTile) {
        if ( streetTile.getNbHouses() == 4 && nbHotels > 0){
            if ( streetTile.buyHouse(currentPlayer) == 1 ) {
                nbHotels--;
                nbHouses += 4;
            }
        } else if (nbHouses > 0 && streetTile.getNbHouses() < 4){
            if ( streetTile.buyHouse(currentPlayer) == 0 ) nbHouses--;
        }else {
            System.out.println("Tu ne peux pas construire.");
            System.out.println("Il reste " + nbHotels + " hotels et " + nbHouses + " maisons.");
        }
    }

    public void startGame() {
        while (players.size() != 1) {
            ArrayList<Player> loosers = new ArrayList<>();
            for (Player player : this.players) {
                currentPlayer = player;
                System.out.println("\n----------Tour de " + currentPlayer.getName() + "----------------");
                System.out.println("Votre solde s'élève à " + currentPlayer.getMoney() + " €");

                rollDice();

                System.out.println("Voici votre solde : " + currentPlayer.getMoney() + " €");

                if(currentPlayer.isBankrupt()){
                    loosers.add(currentPlayer);
                }else{
                    manageProperties(currentPlayer);
                }
            }

            for(Player looser : loosers) {
                players.remove(looser);
            }

        }

        System.out.println(players.get(0).getName() + " a gagné ! Bravo à lui !");
    }

    public void manageProperties(Player player) {
        ArrayList<StreetTile> streetTilesOfPlayer = new ArrayList<>();
        System.out.println("Liste des propriétés de " + currentPlayer.getName() + " :");
        for (PropertyTile propertyTile : player.getPropertyTiles()) {
            if(propertyTile instanceof StreetTile) {
                streetTilesOfPlayer.add((StreetTile) propertyTile);
            }
        }

        if(streetTilesOfPlayer.size() != 0) {
            for (int i = 0; i < streetTilesOfPlayer.size(); i++) {
                System.out.println(i + " : " + streetTilesOfPlayer.get(i).getName());
            }
            int answer = 0;
            while(answer != -1 && player.getPropertyTiles().size() > 0) {
                System.out.println("Souhaitez vous construire sur une de vos propriétés ? Si oui donner le numéro correspondant ou -1 pour quitter");
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    answer = Integer.parseInt(br.readLine());
                } catch (IOException e) {
                    System.out.println("Caractère invalide");
                }

                if(answer != -1){
                    if(answer >= 0 && answer < streetTilesOfPlayer.size()){
                        StreetTile propertyTile = streetTilesOfPlayer.get(answer);
                        this.buyConstruction(propertyTile);
                    }
                }

                System.out.println("Il vous reste " + player.getMoney() + " €");
            }
        }

    }

    public void toggleDebugMode() {
        this.debug = !this.debug;
    }

}
