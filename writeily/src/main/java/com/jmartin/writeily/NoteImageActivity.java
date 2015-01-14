package com.jmartin.writeily;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jmartin.writeily.model.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by jeff on 15-01-14.
 */
public class NoteImageActivity extends ActionBarActivity {

    private ImageView imageView;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_noteimage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageView = (ImageView) findViewById(R.id.note_image);
        deleteButton = (Button) findViewById(R.id.note_image_delete);

        Uri imageUri = Uri.parse(getIntent().getStringExtra(Constants.IMAGE_URI_EXTRA));
        Picasso.with(this).load(imageUri).into(imageView);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.DELETE_IMAGE_KEY, true);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
