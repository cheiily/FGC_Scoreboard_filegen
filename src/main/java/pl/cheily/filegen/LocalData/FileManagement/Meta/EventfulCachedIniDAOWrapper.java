package pl.cheily.filegen.LocalData.FileManagement.Meta;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.function.Predicate;

public class EventfulCachedIniDAOWrapper<T extends DAO<S>, S> implements DAO<S> {
    private final CachedIniDAOBase cached;
    private final DAO<S> dao;
    private final PropertyChangeSupport pcs;
    private final String pcsPropertyName;
    public EventfulCachedIniDAOWrapper(CachedIniDAOBase dao, Class<T> daoClass, String pcsPropertyName) {
        this.cached = dao;
        this.dao = daoClass.cast(dao);
        this.pcsPropertyName = pcsPropertyName;
        this.pcs = new PropertyChangeSupport(this);
    }

    public void subscribe(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void unsubscribe(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public List<S> getAll() {
        var change = cached.cacheChangeTime;
        var result = dao.getAll();
        if (change != cached.cacheChangeTime) {
            pcs.firePropertyChange(pcsPropertyName, null, null);
        }
        return result;
    }

    @Override
    public S get(String key) {
        var change = cached.cacheChangeTime;
        var result = dao.get(key);
        if (change != cached.cacheChangeTime) {
            pcs.firePropertyChange(pcsPropertyName, null, null);
        }
        return result;
    }

    @Override
    public List<S> find(String key, Predicate<S> filter) {
        var change = cached.cacheChangeTime;
        var result = dao.find(key, filter);
        if (change != cached.cacheChangeTime) {
            pcs.firePropertyChange(pcsPropertyName, null, null);
        }
        return result;
    }

    @Override
    public void set(String key, S value) {
        dao.set(key, value);
        pcs.firePropertyChange(pcsPropertyName, null, null);
    }

    @Override
    public void setAll(List<String> keys, List<S> values) {
        dao.setAll(keys, values);
        pcs.firePropertyChange(pcsPropertyName, null, null);
    }

    @Override
    public void delete(String key) {
        dao.delete(key);
        pcs.firePropertyChange(pcsPropertyName, null, null);
    }

    @Override
    public void deleteAll() {
        dao.deleteAll();
        pcs.firePropertyChange(pcsPropertyName, null, null);
    }
}
