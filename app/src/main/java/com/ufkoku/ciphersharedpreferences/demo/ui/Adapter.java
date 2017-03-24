package com.ufkoku.ciphersharedpreferences.demo.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ufkoku.ciphersharedpreferences.R;
import com.ufkoku.ciphersharedpreferences.demo.entity.PrefsEntry;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ItemViewHolder> {

    private Listener listener;

    private LayoutInflater inflater;
    private List<PrefsEntry> entries = new ArrayList<>();

    public Adapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public Adapter(LayoutInflater inflater, List<PrefsEntry> entries) {
        this.inflater = inflater;
        this.entries.addAll(entries);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void addEntry(PrefsEntry entry) {
        entries.add(entry);
        notifyItemInserted(entries.size() - 1);
    }

    public void addEntries(List<PrefsEntry> entriesToAdd) {
        int oldSize = entries.size();
        entries.addAll(entriesToAdd);
        notifyItemRangeInserted(oldSize, entriesToAdd.size());
    }

    public void removeEntry(PrefsEntry entry){
        int index = entries.indexOf(entry);
        if (index != -1){
            entries.remove(index);
            notifyItemRemoved(index);
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(inflater.inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bindObject(entries.get(position));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        protected PrefsEntry binded;

        protected TextView tvKey;
        protected TextView tvValue;
        protected TextView tvEncryptedValue;

        public ItemViewHolder(View itemView) {
            super(itemView);

            tvKey = (TextView) itemView.findViewById(R.id.key);
            tvValue = (TextView) itemView.findViewById(R.id.value);
            tvEncryptedValue = (TextView) itemView.findViewById(R.id.valueEncrypted);

            itemView.findViewById(R.id.deleteButton).setOnClickListener(v -> {
                if (listener != null && binded != null) {
                    listener.onDeleteClicked(binded);
                }
            });
        }

        protected void bindObject(PrefsEntry entry) {
            binded = entry;

            tvKey.setText(entry.getKey());
            tvValue.setText(entry.getValue());
            tvEncryptedValue.setText(entry.getEncryptedValue());
        }

    }

    public interface Listener {

        void onDeleteClicked(PrefsEntry entry);

    }

}
