package com.jmartin.writeily;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jmartin.writeily.adapter.NotesAdapter;
import com.jmartin.writeily.model.Constants;
import com.jmartin.writeily.model.Note;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by jeff on 2014-04-11.
 */
public class StarredFragment extends Fragment {

    private Context context;

    private View layoutView;
    private ListView starredListView;
    private TextView hintTextView;

    private NotesAdapter starredAdapter;
    private ArrayList<Note> starred;

    public StarredFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.notes_fragment, container, false);
        hintTextView = (TextView) layoutView.findViewById(R.id.empty_hint);

        if (starred == null) {
            starred = new ArrayList<Note>();
        }

        checkIfDataEmpty();

        context = getActivity().getApplicationContext();
        starredListView = (ListView) layoutView.findViewById(R.id.notes_listview);
        starredAdapter = new NotesAdapter(context, starred);

        starredListView.setOnItemClickListener(new NotesItemClickListener());
        starredListView.setMultiChoiceModeListener(new ActionModeCallback());
        starredListView.setAdapter(starredAdapter);

        return layoutView;
    }

    @Override
    public void onResume() {
        refreshStarred();
        super.onResume();
    }

    private void checkIfDataEmpty() {
        if (starred.isEmpty()) {
            hintTextView.setVisibility(View.VISIBLE);
            hintTextView.setText(getString(R.string.empty_starred_hint));
        } else {
            hintTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void refreshStarred() {
        starred = new ArrayList<Note>();

        try {
            // Load from internal storage
            File dir = context.getFilesDir();

            for (String f : dir.list()) {
                if (f.endsWith(Constants.WRITEILY_EXT)) {
                    Note note = parseFile(f);

                    if (note != null && note.isStarred() && !note.isArchived())
                        starred.add(0, note);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (starredAdapter != null) {
            starredAdapter = new NotesAdapter(context, starred);
            starredListView.setAdapter(starredAdapter);
        }

        checkIfDataEmpty();
    }

    private Note parseFile(String file) {
        Note result = null;

        try {
            InputStream is = context.openFileInput(file);

            if (is != null) {
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String content = bufferedReader.readLine() + "\n";

                while (bufferedReader.ready()) {
                    content += bufferedReader.readLine() + "\n";
                }

                Gson gson = new Gson();
                result = gson.fromJson(content, Note.class);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void archiveSelectedNotes() {
        SparseBooleanArray checkedIndices = starredListView.getCheckedItemPositions();
        for (int i = 0; i < checkedIndices.size(); i++) {
            // Delete the file from internal storage
            if (checkedIndices.valueAt(i)) {
                Note note = starredAdapter.getItem(checkedIndices.keyAt(i));
                note.setArchived(true);

                note.save(context, note.getMetadataFilename(), true);
            }
        }
        refreshStarred();
    }

    private void unstarSelectedNotes() {
        SparseBooleanArray checkedIndices = starredListView.getCheckedItemPositions();
        for (int i = 0; i < checkedIndices.size(); i++) {
            // Delete the file from internal storage
            if (checkedIndices.valueAt(i)) {
                Note note = starredAdapter.getItem(checkedIndices.keyAt(i));
                note.setStarred(false);

                note.save(context, note.getMetadataFilename(), true);
            }
        }
        refreshStarred();
    }

    /** Search **/
    public void search(CharSequence query) {
        if (query.length() > 0) {
            starredAdapter.getFilter().filter(query);
        }
    }

    public void clearSearchFilter() {
        starredAdapter.getFilter().filter("");

        // Workaround to an (apparently) bug in Android's ArrayAdapter... not pretty, I know
        starredAdapter = new NotesAdapter(context, starred);
        starredListView.setAdapter(starredAdapter);
        starredAdapter.notifyDataSetChanged();
    }

    public void clearItemSelection() {
        starredAdapter.notifyDataSetChanged();
    }

    private class ActionModeCallback implements ListView.MultiChoiceModeListener {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.starred_context_menu, menu);
            mode.setTitle("Select notes");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.context_menu_delete:
                    archiveSelectedNotes();
                    mode.finish();
                    return true;
                case R.id.context_menu_unstar:
                    unstarSelectedNotes();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }


        @Override
        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
            final int numSelected = starredListView.getCheckedItemCount();

            switch (numSelected) {
                case 0:
                    actionMode.setSubtitle(null);
                    break;
                case 1:
                    actionMode.setSubtitle("One item selected");
                    break;
                default:
                    actionMode.setSubtitle(numSelected + " items selected");
                    break;
            }
        }
    };

    private class NotesItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Note note = starredAdapter.getItem(i);

            Intent intent = new Intent(context, NoteActivity.class);
            intent.putExtra(Constants.NOTE_KEY, note);

            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        }
    }
}
