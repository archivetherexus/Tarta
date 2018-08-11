package se.fikaware.misc;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.lang.UnsupportedOperationException;

public class TinyMap<K, V> implements Map<K, V> {
    private class TinyMapEntry implements Entry<K, V> {
        K key;
        V value;
        TinyMapEntry next;

        TinyMapEntry(K key, V value, TinyMapEntry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            this.value = value;
            return value;
        }
    }

    private int entries = 0;
    private TinyMapEntry first = null;

    private TinyMapEntry findEntry(Object key) {
        var entry = first;
        if (key == null) {
            while (entry != null) {
                if (entry.key == null) {
                    return entry;
                }
                entry = entry.next;
            }
        } else {
            while (entry != null) {
                if (key.equals(entry.key)) {
                    return entry;
                }
                entry = entry.next;
            }
        }
        return null;
    }

    public TinyMap<K, V> add(K key, V value) {
        put(key, value);
        return this;
    }

    @Override
    public int size() {
        return entries;
    }

    @Override
    public boolean isEmpty() {
        return entries == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return findEntry(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        var entry = first;
        if (value == null) {
            while (entry != null) {
                if (entry.key == null) {
                    return true;
                }
                entry = entry.next;
            }
        } else {
            while (entry != null) {
                if (value.equals(entry.key)) {
                    return true;
                }
                entry = entry.next;
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
       var entry = findEntry(key);
       return entry == null ? null : entry.value;
    }

    @Override
    public V put(K key, V value) {
        first = new TinyMapEntry(key, value, first);
        entries++;
        return value;
    }

    @Override
    public V remove(Object key) {
        if (first == null) {
            return null;
        } else if (key == null ? first.key == null : key.equals(first.key)) {
            var value = first.value;
            first = first.next;
            return value;
        } else {
            var entry = first;
            while(entry.next != null) {
                if (key == null ? entry.next.key == null : key.equals(entry.next.key)) {
                    entry.next = entry.next.next;
                }
                entry = entry.next;
            }
            return null;
        }

    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (var entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        entries = 0;
        first = null;
    }

    @Override
    public Set<K> keySet() {
        return new Set<>() {

            @Override
            public int size() {
                return entries;
            }

            @Override
            public boolean isEmpty() {
                return entries == 0;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<K> iterator() {
                return new Iterator<>() {
                    TinyMapEntry entry = null;

                    @Override
                    public boolean hasNext() {
                        if (entry == null) {
                            return first != null;
                        } else {
                            return entry.next != null;
                        }
                    }

                    @Override
                    public K next() {
                        if (entry == null) {
                            entry = first;
                        } else {
                            entry = entry.next;
                        }
                        return entry.key;
                    }
                };
            }

            @Override
            public Object[] toArray() {
                return toArray(new Object[entries]);
            }

            @Override
            public boolean add(Object o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                return TinyMap.this.remove(o) != null;
            }

            @Override
            public boolean addAll(Collection c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                entries = 0;
                first = null;
            }

            @Override
            public boolean removeAll(Collection c) {
                var hasChanged = false;
                while(first != null && c.contains(first)) {
                    first = first.next;
                    entries--;
                    hasChanged = true;
                }
                var entry = first;
                while(entry != null) {
                    while(entry.next != null && c.contains(entry.next)) {
                        entry.next = entry.next.next;
                        entries--;
                        hasChanged = true;
                    }
                    entry = entry.next;
                }
                return hasChanged;
            }

            @Override
            public boolean retainAll(Collection c) {
                var hasChanged = false;
                while(first != null && !c.contains(first)) {
                    first = first.next;
                    entries--;
                    hasChanged = true;
                }
                var entry = first;
                while(entry != null) {
                    while(entry.next != null && !c.contains(entry.next)) {
                        entry.next = entry.next.next;
                        entries--;
                        hasChanged = true;
                    }
                    entry = entry.next;
                }
                return hasChanged;
            }

            @Override
            public boolean containsAll(Collection c) {
                var entry = first;
                while(entry != null) {
                    if (!c.contains(entry)) {
                        return false;
                    }
                    entry = entry.next;
                }
                return true;
            }

            @Override
            public <T> T[] toArray(T[] a) {
                if (a == null) {
                    throw new NullPointerException();
                } else {
                    @SuppressWarnings("unchecked")
                    var array = a.length >= entries ? a : (T[]) Array.newInstance(a.getClass().getComponentType(), entries);
                    var entry = first;
                    int index = 0;
                    while (entry != null) {
                        //noinspection unchecked
                        array[index++] = (T) entry.key;
                        entry = entry.next;
                    }
                    return array;
                }
            }
        };
    }

    @Override
    public Collection<V> values() {
        return new Collection<>() {
            @Override
            public int size() {
                return entries;
            }

            @Override
            public boolean isEmpty() {
                return entries == 0;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<V> iterator() {
                return new Iterator<>() {
                    TinyMapEntry entry = null;

                    @Override
                    public boolean hasNext() {
                        if (entry == null) {
                            return first != null;
                        } else {
                            return entry.next != null;
                        }
                    }

                    @Override
                    public V next() {
                        if (entry == null) {
                            entry = first;
                        } else {
                            entry = entry.next;
                        }
                        return entry.value;
                    }
                };
            }

            @Override
            public Object[] toArray() {
                return toArray(new Object[entries]);
            }

            @Override
            public boolean add(Object o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                return TinyMap.this.remove(o) != null;
            }

            @Override
            public boolean addAll(Collection c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                entries = 0;
                first = null;
            }

            @Override
            public boolean removeAll(Collection c) {
                var hasChanged = false;
                while(first != null && c.contains(first)) {
                    first = first.next;
                    entries--;
                    hasChanged = true;
                }
                var entry = first;
                while(entry != null) {
                    while(entry.next != null && c.contains(entry.next)) {
                        entry.next = entry.next.next;
                        entries--;
                        hasChanged = true;
                    }
                    entry = entry.next;
                }
                return hasChanged;
            }

            @Override
            public boolean retainAll(Collection c) {
                var hasChanged = false;
                while(first != null && !c.contains(first)) {
                    first = first.next;
                    entries--;
                    hasChanged = true;
                }
                var entry = first;
                while(entry != null) {
                    while(entry.next != null && !c.contains(entry.next)) {
                        entry.next = entry.next.next;
                        entries--;
                        hasChanged = true;
                    }
                    entry = entry.next;
                }
                return hasChanged;
            }

            @Override
            public boolean containsAll(Collection c) {
                var entry = first;
                while(entry != null) {
                    if (!c.contains(entry)) {
                        return false;
                    }
                    entry = entry.next;
                }
                return true;
            }

            @Override
            public <T> T[] toArray(T[] a) {
                if (a == null) {
                    throw new NullPointerException();
                } else {
                    @SuppressWarnings("unchecked")
                    var array = a.length >= entries ? a : (T[]) Array.newInstance(a.getClass().getComponentType(), entries);
                    var entry = first;
                    int index = 0;
                    while (entry != null) {
                        //noinspection unchecked
                        array[index++] = (T) entry.value;
                        entry = entry.next;
                    }
                    return array;
                }
            }
        };
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new Set<>() {

            @Override
            public int size() {
                return entries;
            }

            @Override
            public boolean isEmpty() {
                return entries == 0;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<>() {
                    TinyMapEntry entry = null;

                    @Override
                    public boolean hasNext() {
                        if (entry == null) {
                            return first != null;
                        } else {
                            return entry.next != null;
                        }
                    }

                    @Override
                    public Entry<K, V> next() {
                        if (entry == null) {
                            entry = first;
                        } else {
                            entry = entry.next;
                        }
                        return entry;
                    }
                };
            }

            @Override
            public Object[] toArray() {
                return toArray(new Object[entries]);
            }

            @Override
            public boolean add(Entry<K, V> entry) {
                TinyMap.this.put(entry.getKey(), entry.getValue());
                return true;
            }

            @Override
            public boolean remove(Object o) {
                return TinyMap.this.remove(o) != null;
            }

            @Override
            public boolean addAll(Collection<? extends Entry<K, V>> c) {
                for (var e : c) {
                    TinyMap.this.put(e.getKey(), e.getValue());
                }
                return true;
            }

            @Override
            public void clear() {
                entries = 0;
                first = null;
            }

            @Override
            public boolean removeAll(Collection c) {
                var hasChanged = false;
                while(first != null && c.contains(first)) {
                    first = first.next;
                    entries--;
                    hasChanged = true;
                }
                var entry = first;
                while(entry != null) {
                    while(entry.next != null && c.contains(entry.next)) {
                        entry.next = entry.next.next;
                        entries--;
                        hasChanged = true;
                    }
                    entry = entry.next;
                }
                return hasChanged;
            }

            @Override
            public boolean retainAll(Collection c) {
                var hasChanged = false;
                while(first != null && !c.contains(first)) {
                    first = first.next;
                    entries--;
                    hasChanged = true;
                }
                var entry = first;
                while(entry != null) {
                    while(entry.next != null && !c.contains(entry.next)) {
                        entry.next = entry.next.next;
                        entries--;
                        hasChanged = true;
                    }
                    entry = entry.next;
                }
                return hasChanged;
            }

            @Override
            public boolean containsAll(Collection c) {
                var entry = first;
                while(entry != null) {
                    if (!c.contains(entry)) {
                        return false;
                    }
                    entry = entry.next;
                }
                return true;
            }

            @Override
            public <T> T[] toArray(T[] a) {
                if (a == null) {
                    throw new NullPointerException();
                } else {
                    @SuppressWarnings("unchecked")
                    var array = a.length >= entries ? a : (T[]) Array.newInstance(a.getClass().getComponentType(), entries);
                    var entry = first;
                    int index = 0;
                    while (entry != null) {
                        //noinspection unchecked
                        array[index++] = (T) entry.key;
                        entry = entry.next;
                    }
                    return array;
                }
            }
        };
    }
}
