package com.example.project1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

// CS499 Capstone enhancement: Tests for sorting and the low stock filter 
public class InventoryOrganizerTest {

    // Builds the sample inventory used by every test
    private List<InventoryRepository.InventoryItem> sampleItems() {
        List<InventoryRepository.InventoryItem> items = new ArrayList<>();
        items.add(new InventoryRepository.InventoryItem(1, "water", 3));
        items.add(new InventoryRepository.InventoryItem(2, "Apples", 6));
        items.add(new InventoryRepository.InventoryItem(3, "bananas", 4));
        items.add(new InventoryRepository.InventoryItem(4, "test", 5));
        return items;
    }

    @Test
    public void sortByNameIsAlphabeticalIgnoringCase() {
        List<InventoryRepository.InventoryItem> sorted = InventoryOrganizer.getSortedItems(
                sampleItems(), InventoryOrganizer.SortKey.NAME);
        assertEquals("Apples", sorted.get(0).name);
        assertEquals("bananas", sorted.get(1).name);
        assertEquals("test", sorted.get(2).name);
        assertEquals("water", sorted.get(3).name);
    }

    @Test
    public void sortByQuantityIsLowestFirst() {
        List<InventoryRepository.InventoryItem> sorted = InventoryOrganizer.getSortedItems(
                sampleItems(), InventoryOrganizer.SortKey.QUANTITY);
        assertEquals(3, sorted.get(0).quantity);
        assertEquals(4, sorted.get(1).quantity);
        assertEquals(5, sorted.get(2).quantity);
        assertEquals(6, sorted.get(3).quantity);
    }

    @Test
    public void sortingDoesNotChangeTheOriginalList() {
        List<InventoryRepository.InventoryItem> original = sampleItems();
        InventoryOrganizer.getSortedItems(original, InventoryOrganizer.SortKey.NAME);
        assertEquals("water", original.get(0).name);
    }

    @Test
    public void lowStockReturnsOnlyItemsBelowThreshold() {
        List<InventoryRepository.InventoryItem> low = InventoryOrganizer.getLowStockItems(sampleItems(), 5);
        assertEquals(2, low.size());
        assertEquals("water", low.get(0).name);
        assertEquals("bananas", low.get(1).name);
    }

    @Test
    public void itemExactlyAtThresholdIsNotLowStock() {
        List<InventoryRepository.InventoryItem> low = InventoryOrganizer.getLowStockItems(sampleItems(), 5);
        for (InventoryRepository.InventoryItem item : low) {
            assertTrue(item.quantity < 5);
        }
    }

    @Test
    public void lowStockIsEmptyWhenNothingIsLow() {
        List<InventoryRepository.InventoryItem> low = InventoryOrganizer.getLowStockItems(sampleItems(), 1);
        assertTrue(low.isEmpty());
    }
}
