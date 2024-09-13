package pl.cheily.filegen.LocalData.FileManagement.Meta.Players;

import pl.cheily.filegen.LocalData.FileManagement.Meta.DAO;
import pl.cheily.filegen.LocalData.Player;

import java.util.List;

public interface PlayersDAO extends DAO<Player> {
    public List<Player> findByTag(String tag);
    public List<Player> findByNationality(String nationality);
    public List<Player> findByName(String name);
    public List<Player> findByRemoteId(long remoteId);
}
