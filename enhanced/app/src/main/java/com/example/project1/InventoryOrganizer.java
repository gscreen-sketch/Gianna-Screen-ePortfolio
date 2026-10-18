package com.example.project1;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// CS499 Capstone enhancement: Sorted and filtered inventory views
public class InventoryOrganizer {

    public enum SortKey { NAME, QUANTITY }

    // Sorts a copy of the list with Collections.sort;  does not touch the original
    public static List<InventoryRepository.InventoryItem> getSortedItems(
            List<InventoryRepository.InventoryItem> rawList, SortKey key) {
        List<InventoryRepository.InventoryItem> sorted = new ArrayList<>(rawList);
        if (key == SortKey.QUANTITY) {
            Collections.sort(sorted, Comparator.comparingInt(item -> item.quantity));
        } else {
            Collections.sort(sorted, Comparator.comparing(item -> item.name.toLowerCase()));
        }
        return sorted;
    }

    public interface Matcher {
        boolean matches(InventoryRepository.InventoryItem item);
    }

    // Single pass over the list
    public static List<InventoryRepository.InventoryItem> filterItems(
            List<InventoryRepository.InventoryItem> rawList, Matcher matcher) {
        List<InventoryRepository.InventoryItem> result = new ArrayList<>();
        for (InventoryRepository.InventoryItem item : rawList) {
            if (matcher.matches(item)) {
                result.add(item);
            }
        }
        return result;
    }

    // Built on filterItems - everything currently below the threshold
    public static List<InventoryRepository.InventoryItem> getLowStockItems(
            List<InventoryRepository.InventoryItem> rawList, int threshold) {
        return filterItems(rawList, item -> item.quantity < threshold);
    }
}
