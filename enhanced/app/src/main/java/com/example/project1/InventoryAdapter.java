package com.example.project1;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// CS499 Capstone enhancement: RecyclerView-based inventory list that Replaces loadItems() clearing and rebuilding every row by hand on every change.
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ItemViewHolder> {

    public interface Listener {
        void onQuantityCommitted(InventoryRepository.InventoryItem item, int newQuantity);
        void onInvalidQuantity(String errorMessage);
        void onDeleteClicked(InventoryRepository.InventoryItem item);
    }

    private List<InventoryRepository.InventoryItem> items;
    private final Listener listener;

    // Stores items and listener
    public InventoryAdapter(List<InventoryRepository.InventoryItem> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    // Swap in a fresh list
    public void setItems(List<InventoryRepository.InventoryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    // Inflates row layout
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_inventory, parent, false);
        return new ItemViewHolder(row);
    }

    // Binds item data to row
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        final InventoryRepository.InventoryItem item = items.get(position);

        holder.nameView.setText(item.name);
        holder.qtyView.setText(String.valueOf(item.quantity));

        // Highlights low stock rows
        if (item.quantity < AppConfig.LOW_STOCK_THRESHOLD) {
            holder.itemView.setBackgroundResource(R.color.low_stock_background);
        } else {
            holder.itemView.setBackgroundResource(R.color.row_background);
        }

        // CS499 Capstone enhancement: Quantity stepper and explicit save
        holder.decrementBtn.setOnClickListener(v -> {
            int current = displayedQuantity(holder);
            if (current > 0) {
                holder.qtyView.setText(String.valueOf(current - 1));
            }
        });

        // Increase button clicked - only adjusts what's on screen, does not save
        holder.incrementBtn.setOnClickListener(v -> {
            int current = displayedQuantity(holder);
            holder.qtyView.setText(String.valueOf(current + 1));
        });

        // Save button clicked - the only action that commits a quantity change
        holder.saveBtn.setOnClickListener(v -> {
            String newQtyText = holder.qtyView.getText().toString();
            InputValidator.QuantityResult result = InputValidator.parseQuantity(newQtyText);

            if (result.valid) {
                listener.onQuantityCommitted(item, result.value);
            } else {
                listener.onInvalidQuantity(result.errorMessage);
            }
        });

        // Delete button clicked
        holder.deleteBtn.setOnClickListener(v -> listener.onDeleteClicked(item));
    }

    // Reads whatever is currently typed into the quantity field for +/- to work off of
    private int displayedQuantity(ItemViewHolder holder) {
        InputValidator.QuantityResult result = InputValidator.parseQuantity(holder.qtyView.getText().toString());
        return result.valid ? result.value : 0;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        EditText qtyView;
        Button decrementBtn;
        Button incrementBtn;
        Button saveBtn;
        Button deleteBtn;

        ItemViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.rowItemName);
            qtyView = itemView.findViewById(R.id.rowItemQty);
            decrementBtn = itemView.findViewById(R.id.rowDecrementButton);
            incrementBtn = itemView.findViewById(R.id.rowIncrementButton);
            saveBtn = itemView.findViewById(R.id.rowSaveButton);
            deleteBtn = itemView.findViewById(R.id.rowDeleteButton);
        }
    }
}
