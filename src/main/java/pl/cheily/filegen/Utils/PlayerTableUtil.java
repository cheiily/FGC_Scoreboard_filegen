package pl.cheily.filegen.Utils;

import javafx.scene.control.TableView;
import pl.cheily.filegen.LocalData.Player;

import java.util.List;

public class PlayerTableUtil {
    public static void incrementSelectedSeedAndSwap(TableView<Player> player_table) {
        int selectedI = player_table.getSelectionModel().getSelectedIndex();
        Player selected = player_table.getItems().get(selectedI);
        int oldSeed = selected.getSeed();
        int newSeed = oldSeed + 1;

        selected.setSeed(newSeed);

        Player next;
        if ( selectedI != player_table.getItems().size() - 1
            && (next = player_table.getItems().get(selectedI + 1)).getSeed() == newSeed
        ) {
            next.setSeed(oldSeed);
        }

        player_table.sort();
    }

    public static void decrementSelectedSeedAndSwap(TableView<Player> player_table) {
        int selectedI = player_table.getSelectionModel().getSelectedIndex();
        Player selected = player_table.getItems().get(selectedI);
        int oldSeed = selected.getSeed();
        int newSeed = oldSeed - 1;

        selected.setSeed(newSeed);

        Player previous;
        if ( selectedI != 0
            && (previous = player_table.getItems().get(selectedI - 1)).getSeed() == newSeed
        ) {
            previous.setSeed(oldSeed);
        }

        player_table.sort();
    }

    /**
     * Adjusts seeds for other players in a list to avoid seed collision with the newly inserted player.
     * @param players list of players
     * @param newPlayer to be inserted
     */
    public static void adjustSeeds(List<Player> players, Player newPlayer) {
        while (true) {
            Player collidingPlayer = null;

            for (Player player : players) {
                if (player != newPlayer && player.getSeed() == newPlayer.getSeed()) {
                    collidingPlayer = player;
                    break;
                }
            }

            if (collidingPlayer == null) {
                break; // No colliding player, end algorithm
            } else {
                collidingPlayer.setSeed(collidingPlayer.getSeed() + 1);
                newPlayer = collidingPlayer;
            }
        }
    }
}
