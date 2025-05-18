package pl.cheily.filegen.LocalData.FileManagement.Meta.Players;

import org.ini4j.Profile;
import pl.cheily.filegen.LocalData.FileManagement.Meta.CachedIniDAOBase;
import pl.cheily.filegen.LocalData.Player;
import pl.cheily.filegen.LocalData.PlayerDeserializer;
import pl.cheily.filegen.LocalData.ResourcePath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PlayersDAOIni extends CachedIniDAOBase implements PlayersDAO {

    public PlayersDAOIni(ResourcePath path) {
        super(path);
    }

    private static Player deserializeFromIni(Profile.Section sec_player) {
        if (sec_player == null) return Player.getInvalid();
        return PlayerDeserializer.fromUuid(sec_player.getName())
                .withTag(sec_player.getOrDefault(PlayerPropKey.TAG.toString(), ""))
                .withName(sec_player.getOrDefault(PlayerPropKey.NAME.toString(), ""))
                .withNationality(sec_player.getOrDefault(PlayerPropKey.NATIONALITY.toString(), ""))
                .withPronouns(sec_player.getOrDefault(PlayerPropKey.PRONOUNS.toString(), ""))
                .withSnsHandle(sec_player.getOrDefault(PlayerPropKey.SNS_HANDLE.toString(), ""))
                .withRemoteId(Long.parseLong(sec_player.getOrDefault(PlayerPropKey.REMOTE_ID.toString(), "0")))
                .withRemoteSeed(Integer.parseInt(sec_player.getOrDefault(PlayerPropKey.REMOTE_SEED.toString(), "0")))
                .withRemoteName(sec_player.getOrDefault(PlayerPropKey.REMOTE_NAME.toString(), ""))
                .withRemoteIconUrl(sec_player.getOrDefault(PlayerPropKey.REMOTE_ICON_URL.toString(), ""))
                .deserialize();
    }

    @Override
    public List<Player> getAll() {
        if (cacheInvalid()) refresh();

        List<Player> players = new ArrayList<>();
        for (String playerId : cache.keySet()) {
            Profile.Section sec_player = cache.get(playerId);
            players.add(deserializeFromIni(sec_player));
        }

        return players;
    }

    @Override
    public Player get(String uuid) {
        if (cacheInvalid()) refresh();

        Profile.Section sec_player = cache.get(uuid);
        return deserializeFromIni(sec_player);
    }

    @Override
    public List<Player> findByTag(String tag) {
        if (cacheInvalid()) refresh();
        return cache.values().stream()
                .filter(sec -> sec.get(PlayerPropKey.TAG.toString()).equals(tag))
                .map(PlayersDAOIni::deserializeFromIni)
                .toList();
    }

    @Override
    public List<Player> findByNationality(String nationality) {
        if (cacheInvalid()) refresh();
        return cache.values().stream()
                .filter(sec -> sec.get(PlayerPropKey.NATIONALITY.toString()).equals(nationality))
                .map(PlayersDAOIni::deserializeFromIni)
                .toList();
    }

    @Override
    public List<Player> findByName(String name) {
        if (cacheInvalid()) refresh();
        return cache.values().stream()
                .filter(sec -> sec.get(PlayerPropKey.NAME.toString()).equals(name))
                .map(PlayersDAOIni::deserializeFromIni)
                .toList();
    }

    @Override
    public List<Player> findByRemoteId(long remoteId) {
        if (cacheInvalid()) refresh();
        return cache.values().stream()
                .filter(sec -> sec.get(PlayerPropKey.REMOTE_ID.toString(), long.class) == remoteId)
                .map(PlayersDAOIni::deserializeFromIni)
                .toList();
    }

    @Override
    public void set(String uuid, Player value) {
        serialize(uuid, value);
        store();
    }

    private void serialize(String uuid, Player value) {
        cache.put(uuid, PlayerPropKey.TAG.toString(), value.getTag());
        cache.put(uuid, PlayerPropKey.NAME.toString(), value.getName());
        cache.put(uuid, PlayerPropKey.NATIONALITY.toString(), value.getNationality());
        cache.put(uuid, PlayerPropKey.PRONOUNS.toString(), value.getPronouns());
        cache.put(uuid, PlayerPropKey.REMOTE_ID.toString(), value.getRemoteId());
        cache.put(uuid, PlayerPropKey.REMOTE_SEED.toString(), value.getRemoteSeed());
        cache.put(uuid, PlayerPropKey.REMOTE_NAME.toString(), value.getRemoteName());
        cache.put(uuid, PlayerPropKey.REMOTE_ICON_URL.toString(), value.getRemoteIconUrl());
    }

    @Override
    public void delete(String key) {
        cache.remove(key);
        store();
    }

    @Override
    public List<Player> find(String key, Predicate<Player> filter) {
        return getAll().stream().filter(filter).toList();
    }

    @Override
    public void setAll(List<String> keys, List<Player> values) {
        if (keys.size() != values.size()) {
            logger.error("PlayersDAO tried to setAll from list of mismatched sizes.");
            return;
        }

        for (int i = 0; i < keys.size(); i++) {
            serialize(keys.get(i), values.get(i));
        }
        store();
    }

    @Override
    public void deleteAll() {
        cache.clear();
        store();
    }
}
