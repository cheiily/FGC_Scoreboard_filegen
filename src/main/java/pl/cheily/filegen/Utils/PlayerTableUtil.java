package pl.cheily.filegen.Utils;

import javafx.event.ActionEvent;
import javafx.scene.control.TableView;
import pl.cheily.filegen.LocalData.Player;
import pl.cheily.filegen.UI.PlayersUI;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PlayerTableUtil {

    /**
     * Increments the seed of the selected element by 1.
     * If there is overlap of the new seed with another element - decrements that element's seed by 1 resulting in a seed/position swap.
     * <p>
     * Table must be sorted by seed beforehand for this to work correctly.
     * @param player_table table containing the selected element.
     * @see PlayersUI#onButtonDown(ActionEvent)
     * @see PlayersUI#onButtonUp(ActionEvent)
     */
    public static void incrementSelectedSeedAndSwap(TableView<Player> player_table) {
        int selectedI = player_table.getSelectionModel().getSelectedIndex();
        Player selected = player_table.getItems().get(selectedI);
        int oldSeed = selected.getRemoteSeed();
        int newSeed = oldSeed + 1;

        selected.setRemoteSeed(newSeed);

        Player next;
        if ( selectedI != player_table.getItems().size() - 1
            && (next = player_table.getItems().get(selectedI + 1)).getRemoteSeed() == newSeed
        ) {
            next.setRemoteSeed(oldSeed);
        }

        player_table.sort();
    }

    /**
     * Decrements the seed of the selected element by 1.
     * If there is overlap of the new seed with another element - increments that element's seed by 1 resulting in a seed/position swap.
     * <p>
     * Table must be sorted by seed beforehand for this to work correctly.
     * @param player_table table containing the selected element.
     * @see PlayersUI#onButtonDown(ActionEvent)
     * @see PlayersUI#onButtonUp(ActionEvent)
     */
    public static void decrementSelectedSeedAndSwap(TableView<Player> player_table) {
        int selectedI = player_table.getSelectionModel().getSelectedIndex();
        Player selected = player_table.getItems().get(selectedI);
        int oldSeed = selected.getRemoteSeed();
        int newSeed = oldSeed - 1;

        selected.setRemoteSeed(newSeed);

        Player previous;
        if ( selectedI != 0
            && (previous = player_table.getItems().get(selectedI - 1)).getRemoteSeed() == newSeed
        ) {
            previous.setRemoteSeed(oldSeed);
        }

        player_table.sort();
    }

    /**
     * Adjusts seeds for other players in a list to avoid seed collision with the newly inserted player.
     * @param players list of players
     * @param newPlayer to be inserted
     * @see PlayersUI#initialize(URL, ResourceBundle)
     */
    public static void adjustSeeds(List<Player> players, Player newPlayer) {
        while (true) {
            Player collidingPlayer = null;

            for (Player player : players) {
                if (player != newPlayer && player.getRemoteSeed() == newPlayer.getRemoteSeed()) {
                    collidingPlayer = player;
                    break;
                }
            }

            if (collidingPlayer == null) {
                break; // No colliding player, end algorithm
            } else {
                collidingPlayer.setRemoteSeed(collidingPlayer.getRemoteSeed() + 1);
                newPlayer = collidingPlayer;
            }
        }
    }
}
