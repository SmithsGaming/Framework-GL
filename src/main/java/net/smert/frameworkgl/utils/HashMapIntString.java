/**
 * Copyright 2012 Jason Sorensen (sorensenj@smert.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.smert.frameworkgl.utils;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class HashMapIntString {

    private static int DEFAULT_INITIAL_CAPACITY = 16;
    private static int MAXIMUM_CAPACITY = 1 << 30;
    private static int NULL_BUCKET = 0;
    private static float DEFAULT_LOAD_FACTOR = .75f;
    // Not a true "not found" setting, just an unlikely value. Since we don't allow
    // for NULL there is no value which can be used. Means you can't have this value
    // under normal conditions.
    public static int NULL_KEY = -Integer.MAX_VALUE + 1;

    private float loadFactor;
    private int modCount;
    private int size;
    private int threshold;
    private Collection<String> values;
    private Entry[] table;
    private EntrySet entrySet;
    private KeySet keySet;

    public HashMapIntString() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public HashMapIntString(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public HashMapIntString(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }

        int capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }

        this.loadFactor = loadFactor;
        modCount = 0;
        size = 0;
        threshold = (int) Math.min(capacity * loadFactor, MAXIMUM_CAPACITY + 1);
        table = new Entry[capacity];
        entrySet = null;
        keySet = null;
    }

    private void addEntry(int hash, int key, String value, int bucketIndex) {
        if ((size >= threshold) && (table[bucketIndex] != null)) {
            resize(2 * table.length);
            bucketIndex = indexFor(hash, table.length);
        }
        Entry e = table[bucketIndex];
        table[bucketIndex] = new Entry(hash, key, value, e);
        size++;
    }

    private void copyTable(Entry[] newTable) {
        int newCapacity = newTable.length;
        for (Entry e : table) {
            while (e != null) {
                Entry next = e.next;
                int i = indexFor(e.hash, newCapacity);
                e.next = newTable[i];
                newTable[i] = e;
                e = next;
            }
        }
    }

    private Entry getEntry(int key) {
        if (size == 0) {
            return null;
        }
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        for (Entry e = table[i]; e != null; e = e.next) {
            if ((e.hash == hash) && (e.key == key)) {
                return e;
            }
        }
        return null;
    }

    private String getForNullKey() {
        if (size == 0) {
            return null;
        }
        for (Entry e = table[NULL_BUCKET]; e != null; e = e.next) {
            if (e.key == NULL_KEY) {
                return e.value;
            }
        }
        return null;
    }

    private int hash(int k) {
        int h = k;
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    private Iterator<Entry> newEntryIterator() {
        return new EntryIterator();
    }

    private Iterator<Integer> newKeyIterator() {
        return new KeyIterator();
    }

    private Iterator<String> newValueIterator() {
        return new ValueIterator();
    }

    private String putForNullKey(String value) {
        for (Entry e = table[NULL_BUCKET]; e != null; e = e.next) {
            if (e.key == NULL_KEY) {
                String oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }

        modCount++;
        addEntry(0, NULL_KEY, value, 0);
        return null;
    }

    private Entry removeEntryForKey(int key) {
        if (size == 0) {
            return null;
        }

        int hash = (key == NULL_KEY) ? 0 : hash(key);
        int i = indexFor(hash, table.length);
        Entry prev = table[i];
        Entry e = prev;

        while (e != null) {
            Entry next = e.next;
            if ((e.hash == hash) && (e.key == key)) {
                modCount++;
                size--;
                if (prev == e) {
                    table[i] = next;
                } else {
                    prev.next = next;
                }
                return e;
            }
            prev = e;
            e = next;
        }

        return null;
    }

    private Entry removeEntry(Entry entry) {
        if (size == 0) {
            return null;
        }

        int key = entry.getKey();
        int hash = (key == NULL_KEY) ? 0 : hash(key);
        int i = indexFor(hash, table.length);
        Entry prev = table[i];
        Entry e = prev;

        while (e != null) {
            Entry next = e.next;
            if ((e.hash == hash) && e.equals(entry)) {
                modCount++;
                size--;
                if (prev == e) {
                    table[i] = next;
                } else {
                    prev.next = next;
                }
                return e;
            }
            prev = e;
            e = next;
        }

        return null;
    }

    private void resize(int newCapacity) {
        if (table.length == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity];
        copyTable(newTable);
        table = newTable;
        threshold = (int) Math.min(newCapacity * loadFactor, MAXIMUM_CAPACITY + 1);
    }

    public void clear() {
        modCount++;
        Arrays.fill(table, null);
        size = 0;
    }

    public boolean containsKey(int key) {
        return getEntry(key) != null;
    }

    public boolean containsValue(String value) {
        for (Entry e : table) {
            for (Entry ie = e; ie != null; ie = ie.next) {
                if (ie.value.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<Entry> entrySet() {
        Set<Entry> es = entrySet;
        return es != null ? es : (entrySet = new EntrySet());
    }

    public String get(int key) {
        if (key == NULL_KEY) {
            return getForNullKey();
        }
        Entry e = getEntry(key);

        return (e == null) ? null : e.getValue();
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Set<Integer> keySet() {
        Set<Integer> ks = keySet;
        return (ks != null ? ks : (keySet = new KeySet()));
    }

    public String put(int key, String value) {
        if (key == NULL_KEY) {
            return putForNullKey(value);
        }

        int hash = hash(key);
        int i = indexFor(hash, table.length);
        for (Entry e = table[i]; e != null; e = e.next) {
            if ((e.hash == hash) && (e.key == key)) {
                String oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }

        modCount++;
        addEntry(hash, key, value, i);
        return null;
    }

    public String remove(int key) {
        Entry e = removeEntryForKey(key);
        return (e == null) ? null : e.value;
    }

    public int size() {
        return size;
    }

    public Collection<String> values() {
        Collection<String> vs = values;
        return (vs != null ? vs : (values = new Values()));
    }

    public static class Entry {

        private int hash;
        private int key;
        private Entry next;
        private String value;

        public Entry(int h, int k, String v, Entry n) {
            hash = h;
            key = k;
            next = n;
            value = v;
        }

        public String getValue() {
            return value;
        }

        public int getKey() {
            return key;
        }

        public String setValue(String newValue) {
            String oldValue = value;
            value = newValue;
            return oldValue;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + this.key;
            hash = 71 * hash + Objects.hashCode(this.next);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Entry other = (Entry) obj;
            if (this.key != other.key) {
                return false;
            }
            return Objects.equals(this.next, other.next);
        }

        @Override
        public String toString() {
            return getKey() + "=" + getValue();
        }

    }

    private class EntryIterator extends HashIterator<Entry> {

        @Override
        public Entry next() {
            return nextEntry();
        }

    }

    private class EntrySet extends AbstractSet<Entry> {

        @Override
        public void clear() {
            HashMapIntString.this.clear();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry e = (Entry) o;
            Entry candidate = getEntry(e.getKey());
            return ((candidate != null) && candidate.equals(e));
        }

        @Override
        public Iterator<Entry> iterator() {
            return newEntryIterator();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            return removeEntry((Entry) o) != null;
        }

        @Override
        public int size() {
            return size;
        }

    }

    private abstract class HashIterator<E> implements Iterator<E> {

        private int expectedModCount;
        private int index;
        private Entry current;
        private Entry next;

        public HashIterator() {
            expectedModCount = modCount;
            if (size > 0) {
                while ((index < table.length) && ((next = table[index++]) == null));
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        public Entry nextEntry() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            Entry e = next;
            if (e == null) {
                throw new NoSuchElementException();
            }
            if ((next = e.next) == null) {
                while ((index < table.length) && ((next = table[index++]) == null));
            }
            current = e;
            return e;
        }

        @Override
        public void remove() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (current == null) {
                throw new IllegalStateException();
            }
            int k = current.key;
            current = null;
            HashMapIntString.this.removeEntryForKey(k);
            expectedModCount = modCount;
        }

    }

    private class KeyIterator extends HashIterator<Integer> {

        @Override
        public Integer next() {
            return nextEntry().getKey();
        }

    }

    private class KeySet extends AbstractSet<Integer> {

        @Override
        public void clear() {
            HashMapIntString.this.clear();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Integer)) {
                return false;
            }
            return containsKey((Integer) o);
        }

        @Override
        public Iterator<Integer> iterator() {
            return newKeyIterator();
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Integer)) {
                return false;
            }
            return HashMapIntString.this.removeEntryForKey((Integer) o) != null;
        }

        @Override
        public int size() {
            return size;
        }

    }

    private class Values extends AbstractCollection<String> {

        @Override
        public void clear() {
            HashMapIntString.this.clear();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof String)) {
                return false;
            }
            return containsValue((String) o);
        }

        @Override
        public Iterator<String> iterator() {
            return newValueIterator();
        }

        @Override
        public int size() {
            return size;
        }

    }

    private class ValueIterator extends HashIterator<String> {

        @Override
        public String next() {
            return nextEntry().value;
        }

    }

}
