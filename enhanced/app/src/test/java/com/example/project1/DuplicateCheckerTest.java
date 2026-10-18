package com.example.project1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

// CS499 Capstone enhancement: Tests for the duplicate item checker (HashMap index) in InventoryRepository
public class DuplicateCheckerTest {

    // Stands in for the server so the duplicate logic can be tested on its own
    private static class FakeInventoryRepository extends InventoryRepository {
        List<InventoryItem> stored = new ArrayList<>();
        boolean addCalled = false;
        boolean updateCalled = false;
        int updatedId = -1;
        int updatedQuantity = -1;

        @Override
        public List<InventoryItem> getAllItems() {
            return stored;
        }

        @Override
        public void addItem(String itemName, int quantity) {
            addCalled = true;
            stored.add(new InventoryItem(stored.size() + 1, itemName, quantity));
        }

        @Override
        public void updateItemQuantity(int id, int newQuantity) {
            updateCalled = true;
            updatedId = id;
            updatedQuantity = newQuantity;
        }
    }

    @Test
    public void existingItemUpdatesQuantityInsteadOfAddingARow() throws Exception {
        FakeInventoryRepository repo = new FakeInventoryRepository();
        repo.stored.add(new InventoryRepository.InventoryItem(7, "apples", 6));

        repo.addOrUpdateItem("apples", 4);

        assertTrue(repo.updateCalled);
        assertFalse(repo.addCalled);
        assertEquals(7, repo.updatedId);
        assertEquals(10, repo.updatedQuantity);
    }

    @Test
    public void newItemIsAddedAsANewRow() throws Exception {
        FakeInventoryRepository repo = new FakeInventoryRepository();
        repo.stored.add(new InventoryRepository.InventoryItem(1, "apples", 6));

        repo.addOrUpdateItem("water", 3);

        assertTrue(repo.addCalled);
        assertFalse(repo.updateCalled);
        assertEquals(2, repo.stored.size());
    }

    @Test
    public void emptyInventoryAddsTheFirstItem() throws Exception {
        FakeInventoryRepository repo = new FakeInventoryRepository();

        repo.addOrUpdateItem("apples", 2);

        assertTrue(repo.addCalled);
        assertEquals(1, repo.stored.size());
    }
}
