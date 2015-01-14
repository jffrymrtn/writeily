package com.jmartin.writeily.adapter;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jmartin.writeily.R;
import com.jmartin.writeily.model.Note;

import java.util.ArrayList;

/**
 * Created by jeff on 2014-04-11.
 */
public class NotesAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<Note> data;
    private ArrayList<Note> filteredData;

    public NotesAdapter(Context context, ArrayList<Note> content) {
        this.context = context;
        this.data = content;
        this.filteredData = data;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Note getItem(int i) {
        return filteredData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        String theme = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_theme_key), "");

        View row = inflater.inflate(R.layout.note_item, viewGroup, false);
        TextView noteTitle = (TextView) row.findViewById(R.id.note_title);
        TextView noteSummary = (TextView) row.findViewById(R.id.note_summary);

        noteTitle.setText(getItem(i).getTitle());
        noteSummary.setText(getItem(i).getSummary());

        if (!theme.equals("")) {
            if (theme.equals(context.getString(R.string.theme_dark))) {
                noteTitle.setTextColor(context.getResources().getColor(android.R.color.white));
            } else {
                noteTitle.setTextColor(context.getResources().getColor(R.color.dark_grey));
            }
        }

        return row;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults searchResults = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    searchResults.values = data;
                    searchResults.count = data.size();
                } else {
                    ArrayList<Note> searchResultsData = new ArrayList<Note>();

                    for (Note item : data) {
                        if (item.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            searchResultsData.add(item);
                        }
                    }

                    searchResults.values = searchResultsData;
                    searchResults.count = searchResultsData.size();
                }
                return searchResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (ArrayList<Note>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}
