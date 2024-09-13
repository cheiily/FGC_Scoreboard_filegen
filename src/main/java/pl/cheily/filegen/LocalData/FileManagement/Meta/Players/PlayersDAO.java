package pl.cheily.filegen.LocalData.FileManagement.Meta.Players;

import pl.cheily.filegen.LocalData.FileManagement.Meta.DAO;
import pl.cheily.filegen.LocalData.Player;

import java.util.List;

public interface PlayersDAO extends DAO<Player> {
    List<Player> findByTag(String tag);
    List<Player> findByNationality(String nationality);
    List<Player> findByName(String name);
    List<Player> findByRemoteId(long remoteId);
    // setAll
}
