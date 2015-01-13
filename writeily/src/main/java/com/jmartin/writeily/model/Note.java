package com.jmartin.writeily.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;

/**
 * Created by jeff on 2014-04-11.
 */
public class Note implements Serializable {

    private String title;
    private String content;

    private String metadataFilename;
    private String txtFilename;
    private String rawFilename;

    private String imageUri;

    private boolean isArchived = false;
    private boolean isStarred = false;

    public Note() {
        title = "";
        content = "";
        metadataFilename = "";
        rawFilename = "";
        txtFilename = "";
    }

    public String getRawFilename() {
        return rawFilename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean isTrash) {
        this.isArchived = isTrash;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean isStarred) {
        this.isStarred = isStarred;
    }

    public String getMetadataFilename() {
        return metadataFilename;
    }

    public String getTxtFilename() {
        return txtFilename;
    }

    public void setTxtFilename(String txtFilename) {
        this.txtFilename = txtFilename;
    }

    public String getSummary() {
        return (getContent().replaceAll("\\s", " ").trim());
    }

    /**
     *
     * @param content
     * @param imageUri
     * @return
     */
    public boolean update(String content, String title, String imageUri) {
        boolean result = false;

        this.content = content;
        this.title = title.isEmpty() ? this.title : title;

        // Make sure the title is of appropriate length
        if (this.title.length() > Constants.MAX_TITLE_LENGTH) {
            this.title = this.title.substring(0, Constants.MAX_TITLE_LENGTH);
        }

        // Get the new filename (may or may not be unchanged)
        String tempFilename = this.title.replaceAll("[^\\w\\s]+", "");

        // Flag result if the title needs to change (means metadataFilename needs to change too)
        if (!rawFilename.equals(tempFilename)) {
            result = true;
        }

        // Set the metadataFilename for this note
        this.rawFilename = tempFilename;
        this.metadataFilename = this.rawFilename + Constants.WRITEILY_EXT;
        this.txtFilename = this.rawFilename + Constants.TXT_EXT;

        // Set the image URI
        this.setImageUri(imageUri);

        return result;
    }

    public String save(Context context, String loadedFilename, boolean requiresOverwrite) {
        if (requiresOverwrite && loadedFilename != null) {
            // We need to delete the old files if we need a complete overwrite
            File oldFileMetadata = new File(context.getFilesDir() + File.separator + loadedFilename + Constants.WRITEILY_EXT);
            File oldFile = new File(context.getFilesDir() + File.separator + loadedFilename + Constants.TXT_EXT);

            oldFileMetadata.delete();
            oldFile.delete();
        }

        // Don't save anything if the filename is empty
        if (getRawFilename().length() == 0) {
            return "";
        }

        // Save two files: metadata and the txt file
        Gson gson = new Gson();
        String metadataContent = gson.toJson(this, Note.class);

        writeToInternalStorage(context, getMetadataFilename(), metadataContent);
        writeToInternalStorage(context, getTxtFilename(), getContent());

        return getRawFilename();
    }

    /**
     * Write the file to internal storage.
     *
     * @param context
     * @param filename
     */
    private void writeToInternalStorage(Context context, String filename, String content) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);

            writer.write(content);
            writer.flush();

            writer.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
