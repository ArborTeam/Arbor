/**
 * Taken from https://github.com/Winds-Studio/Leaf/blob/ver/1.21.7/leaf-server/src/main/java/org/dreeam/leaf/util/FastBitRadixSort.java
 * A fast utility class for entity distance-to sorting
 */
package org.dreeam.leaf.util;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference; // Luminol - fix memory leak
import java.lang.reflect.Array;
import java.util.List;

public final class FastBitRadixSort {

    private static final int SMALL_ARRAY_THRESHOLD = 6;
    private WeakReference<Entity>[] entityBuffer = new WeakReference[0]; // Luminol - fix memory leak
    private long[] bitsBuffer = new long[0];

    @SuppressWarnings("unchecked")
    public <T extends Entity, T_REF extends Entity> T[] sort(@NotNull List<T> entities, T_REF referenceEntity, Class<T> entityClass) {
        int size = entities.size();
        if (size <= 1) {
            T[] resultArray = (T[]) Array.newInstance(entityClass, size);
            return entities.toArray(resultArray);
        }

        if (this.entityBuffer.length < size) {
            this.entityBuffer = new WeakReference[size]; // Luminol - fix memory leak
            this.bitsBuffer = new long[size];
        }
        for (int i = 0; i < size; i++) {
            this.entityBuffer[i] = new WeakReference<>(entities.get(i)); // Luminol - fix memory leak
            this.bitsBuffer[i] = Double.doubleToRawLongBits(
                    referenceEntity.distanceToSqr(entities.get(i))
            );
        }

        fastRadixSort(this.entityBuffer, this.bitsBuffer, 0, size - 1, 62);

        T[] resultArray = (T[]) Array.newInstance(entityClass, size);
        for (int i = 0; i < size; i++) {
            // Luminol start - fix memory leak
            WeakReference<Entity> ref = this.entityBuffer[i];
            Entity entity = ref != null ? ref.get() : null;
            if (entity != null) {
                resultArray[i] = entityClass.cast(entity);
            }
            // Luminol end - fix memory leak
        }
        return resultArray;
    }

    private void fastRadixSort(
            WeakReference<Entity>[] ents, // Luminol - fix memory leak
            long[] bits,
            int low,
            int high,
            int bit
    ) {
        if (bit < 0 || low >= high) {
            return;
        }

        if (high - low <= SMALL_ARRAY_THRESHOLD) {
            insertionSort(ents, bits, low, high);
            return;
        }

        int i = low;
        int j = high;
        final long mask = 1L << bit;

        while (i <= j) {
            while (i <= j && (bits[i] & mask) == 0) {
                i++;
            }
            while (i <= j && (bits[j] & mask) != 0) {
                j--;
            }
            if (i < j) {
                swap(ents, bits, i++, j--);
            }
        }

        if (low < j) {
            fastRadixSort(ents, bits, low, j, bit - 1);
        }
        if (i < high) {
            fastRadixSort(ents, bits, i, high, bit - 1);
        }
    }

    private void insertionSort(
            WeakReference<Entity>[] ents, // Luminol - fix memory leak
            long[] bits,
            int low,
            int high
    ) {
        for (int i = low + 1; i <= high; i++) {
            int j = i;
            WeakReference<Entity> currentEntityRef = ents[j]; // Luminol - fix memory leak
            long currentBits = bits[j];

            // Luminol start - fix memory leak
            while (j > low) {
                WeakReference<Entity> prevEntityRef = ents[j - 1];
                Entity prevEntity = prevEntityRef != null ? prevEntityRef.get() : null;
                if (prevEntity == null || bits[j - 1] <= currentBits) {
                    break;
                }
                // Luminol end - fix memory leak
                ents[j] = ents[j - 1];
                bits[j] = bits[j - 1];
                j--;
            }
            ents[j] = currentEntityRef; // Luminol - fix memory leak
            bits[j] = currentBits;
        }
    }

    private void swap(WeakReference<Entity>[] ents, long[] bits, int a, int b) {
        WeakReference<Entity> tempEntity = ents[a];
        ents[a] = ents[b];
        ents[b] = tempEntity;

        long tempBits = bits[a];
        bits[a] = bits[b];
        bits[b] = tempBits;
    }
}
