package com.accenture.dansmarue.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.accenture.dansmarue.BuildConfig;
import com.accenture.dansmarue.R;
import com.accenture.dansmarue.utils.BitmapScaler;
import com.accenture.dansmarue.utils.MiscTools;
import com.google.firebase.crashlytics.FirebaseCrashlytics;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Parent for AddAnomalyActivity BaseAnomalyActivity
 */
public abstract class BaseAnomalyActivity extends BaseActivity {

    protected static final int TAKE_PICTURE_REQUEST_CODE = 1980;
    protected static final int CHOOSE_FROM_GALLERY_REQUEST_CODE = 1981;

    protected String mCurrentPhotoPath = "";

    protected abstract int getContentView();

    /**
     * Display choice modal dialog to add photo to incident.
     */
    public void selectImage() {
        final CharSequence[] items = {  getResources().getString(R.string.take_picture), getResources().getString(R.string.choose_photo_in_gallery), getResources().getString(R.string.text_cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.text_add);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getResources().getString(R.string.take_picture).equals(items[which])) {
                    cameraIntent();
                } else if (getResources().getString(R.string.choose_photo_in_gallery).equals(items[which])) {
                    galleryIntent();

                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    /**
     * Start camera activity
     */
    private void cameraIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                FirebaseCrashlytics.getInstance().log(e.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID+".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST_CODE);

            }
        }
    }

    /**
     * Start activity Gallery on device.
     */
    private void galleryIntent() {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_photo)), CHOOSE_FROM_GALLERY_REQUEST_CODE);
    }


    /**
     * Create image file from photo.
     * @return image file representation of photo.
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Rotate compress resize photos incident
     * before send them to back office
     */
    protected void rotateResizeAndCompress() {
        // Rotate, Scrale and compress
        Bitmap resizedBitmap = BitmapScaler.scaleToFitTheGoodOne(MiscTools.rotateBitmapOrientation(mCurrentPhotoPath));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        File resizedFile = new File(mCurrentPhotoPath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(resizedFile);
            fos.write(bytes.toByteArray());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
