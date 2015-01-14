package com.jmartin.writeily;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.jmartin.writeily.model.Constants;
import com.jmartin.writeily.model.Note;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jeff on 2014-04-11.
 */
public class NoteActivity extends ActionBarActivity {

    private Note note;
    private Context context;
    private EditText noteTitle;
    private EditText content;
    private ImageView noteImage;

    private String loadedFilename;
    private String imageUri;


    public NoteActivity() {
    }

    public NoteActivity(Context context) {
        this.context = context;
        this.note = new Note();
    }

    public NoteActivity(Context context, Note note) {
        this.context = context;
        this.note = note;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_note);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        context = getApplicationContext();
        content = (EditText) findViewById(R.id.note_content);
        noteTitle = (EditText) findViewById(R.id.edit_note_title);
        noteImage = (ImageView) findViewById(R.id.note_image);

        noteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteActivity.this, NoteImageActivity.class);
                intent.putExtra(Constants.IMAGE_URI_EXTRA, imageUri);
                startActivityForResult(intent, Constants.VIEW_PHOTO_KEY);
            }
        });

        Intent receivingIntent = getIntent();
        String intentAction = receivingIntent.getAction();
        String type = receivingIntent.getType();

        if (Intent.ACTION_SEND.equals(intentAction) && type != null) {
            openFromSendAction(receivingIntent);
        } else if (Intent.ACTION_EDIT.equals(intentAction) && type != null) {
            openFromEditAction(receivingIntent);
        } else {
            note = (Note) getIntent().getSerializableExtra(Constants.NOTE_KEY);
        }

        if (note == null) {
            note = new Note();
        } else {
            content.setText(note.getContent());
            loadedFilename = note.getRawFilename();
            imageUri = note.getImageUri();
            noteTitle.setText(note.getTitle());
        }

        // Show/Hide noteImage
        if (imageUri == null) {
            noteImage.setVisibility(View.GONE);
        } else {
            noteImage.setVisibility(View.VISIBLE);
            Picasso.with(context).load(Uri.parse(imageUri)).fit().centerCrop().into(noteImage);
        }

        // Set up the font and background activity_preferences
        setupAppearancePreferences();

        super.onCreate(savedInstanceState);
    }

    private void openFromSendAction(Intent receivingIntent) {
        note = new Note();
        String content = "";
        Uri fileUri = receivingIntent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (fileUri != null) {
            try {
                InputStreamReader reader = new InputStreamReader(getContentResolver().openInputStream(fileUri));
                BufferedReader br = new BufferedReader(reader);

                while (br.ready()) {
                    content = br.readLine();
                }

                note.setContent(content);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openFromEditAction(Intent receivingIntent) {
        note = new Note();
        String content = "";
        Uri fileUri = receivingIntent.getData();

        if (fileUri != null) {
            try {
                InputStreamReader reader = new InputStreamReader(getContentResolver().openInputStream(fileUri));
                BufferedReader br = new BufferedReader(reader);

                while (br.ready()) {
                    content = br.readLine();
                }

                note.setContent(content);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_add_image:
                promptForImage();
                return true;
            case R.id.action_share:
                shareNote();
                return true;
            case R.id.action_preview:
                previewNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        saveNote(note.update(content.getText().toString(), noteTitle.getText().toString(), imageUri));
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.CHOOSE_PHOTO_KEY) {
                imageUri = data.getData().toString();
                noteImage.setVisibility(View.VISIBLE);
                Picasso.with(context).load(Uri.parse(imageUri)).fit().centerCrop().into(noteImage);
            } else if (requestCode == Constants.VIEW_PHOTO_KEY) {
                if (data.getBooleanExtra(Constants.DELETE_IMAGE_KEY, false)) {
                    noteImage.setImageBitmap(null);
                    imageUri = null;
                    note.update(content.getText().toString(), noteTitle.getText().toString(), imageUri);
                    noteImage.setVisibility(View.GONE);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupAppearancePreferences() {
        String fontType = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_font_choice_key), "");
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_theme_key), "");

        String fontSize = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_font_size_key), "");

        if (!fontSize.equals("")) {
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(fontSize));
        }

        if (!fontType.equals("")) {
            content.setTypeface(Typeface.create(fontType, Typeface.NORMAL));
        }

        if (!theme.equals("")) {
            if (theme.equals(getString(R.string.theme_dark))) {
                content.setBackgroundColor(getResources().getColor(R.color.dark_grey));
                content.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                content.setBackgroundColor(getResources().getColor(android.R.color.white));
                content.setTextColor(getResources().getColor(R.color.dark_grey));
            }
        }
    }

    private void previewNote() {
        saveNote(note.update(content.getText().toString(), noteTitle.getText().toString(), imageUri));

        Intent intent = new Intent(this, PreviewActivity.class);

        // .replace is a workaround for Markdown lists requiring two \n characters
        intent.putExtra(Constants.MD_PREVIEW_KEY, note.getContent().replace("\n-", "\n\n-"));

        startActivity(intent);
    }

    private void shareNote() {
        saveNote(note.update(content.getText().toString(), noteTitle.getText().toString(), imageUri));

        String shareContent = note.getContent();

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_string)));
    }

    private void promptForImage() {
        Intent imageIntent = new Intent();
        imageIntent.setAction(Intent.ACTION_PICK);
        imageIntent.setType("image/*");

        startActivityForResult(imageIntent, Constants.CHOOSE_PHOTO_KEY);
    }

    private void saveImage() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Constants.WRITEILY_IMG_DIR);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // TODO save image
    }

    private void saveNote(boolean requiresOverwrite) {
        loadedFilename = note.save(context, loadedFilename, requiresOverwrite);
    }
}
