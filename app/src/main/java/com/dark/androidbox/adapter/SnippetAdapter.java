package com.dark.androidbox.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import java.util.List;

public class SnippetAdapter extends ArrayAdapter<String> {

    private List<String> snippets;

    public SnippetAdapter(Context context, int resource, List<String> snippets) {
        super(context, resource, snippets);
        this.snippets = snippets;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                results.values = snippets;
                results.count = snippets.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }
}
