package com.accenture.dansmarue.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accenture.dansmarue.BuildConfig;
import com.accenture.dansmarue.R;
import com.accenture.dansmarue.di.components.DaggerPresenterComponent;
import com.accenture.dansmarue.di.modules.PresenterModule;
import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.presenters.AddAnomalyPresenter;
import com.accenture.dansmarue.mvp.views.AddAnomalyView;
import com.accenture.dansmarue.ui.fragments.MapAnomalyFragment;
import com.accenture.dansmarue.utils.BitmapScaler;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.MiscTools;
import com.accenture.dansmarue.utils.NetworkUtils;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.accenture.dansmarue.ui.activities.ChoosePriorityActivity.PRIORITIES_IDS;
import static com.accenture.dansmarue.ui.activities.ChoosePriorityActivity.PRIORITIES_LIBELLE;


public class AddAnomalyActivity extends BaseActivity implements AddAnomalyView {

    private static final String TAG = AddAnomalyActivity.class.getCanonicalName();
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private static final int DEFAULT_PRIORITY_ID = R.id.priority_low;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1976;
    private static final int CHOOSE_TYPE_REQUEST_CODE = 1977;
    private static final int SET_DESCRIPTION_REQUEST_CODE = 1978;
    private static final int CHOOSE_PRIORITY_REQUEST_CODE = 1979;
    private static final int TAKE_PICTURE_REQUEST_CODE = 1980;
    private static final int CHOOSE_FROM_GALLERY_REQUEST_CODE = 1981;
    private static final int LOGIN_REQUEST_CODE = 1982;

    private static LatLng myLastPosition = null;

    private Dialog greetingsDialog, greetingsDialogSendIncident, greetingsDialogSendError, greetingsDialogSendOkThanks;
    private Dialog emailDialog;

    @Inject
    protected AddAnomalyPresenter presenter;

    //Type
    @BindView(R.id.image_choose_type)
    protected ImageView chooseType;
    @BindView(R.id.text_choose_type)
    protected TextView type;
    @BindView(R.id.text_choose_type_subtitle)
    protected TextView chooseTypeSubtitle;

    //Pictures
    @BindView(R.id.image_choose_picture)
    protected ImageView choosePicture;
    @BindView(R.id.myImageChoice)
    protected ImageView image1;
    @BindView(R.id.myImageChoice2)
    protected ImageView image2;
    @BindView(R.id.add_anomaly_photo_layout)
    protected RelativeLayout addAnomalyPhotoLayout;

    @BindView(R.id.add_anomaly_photo)
    protected ImageButton addPictureButton;

    @BindView(R.id.myImageChoiceLayout)
    protected RelativeLayout myImageChoiceLayout;
    @BindView(R.id.myImageChoiceClose)
    protected ImageView myImageChoiceClose;

    @BindView(R.id.myImageChoice2Layout)
    protected RelativeLayout myImageChoice2Layout;
    @BindView(R.id.myImageChoice2Close)
    protected ImageView myImageChoice2Close;


    //Description
    @BindView(R.id.text_description_subtitle)
    protected AppCompatTextView textDescription;
    @BindView(R.id.description_choose_type)
    protected ImageView chooseDescription;

    //Priority
    @BindView(R.id.text_default)
    protected TextView defaultPriorityLabel;
    @BindView(R.id.text_priority_subtitle)
    protected TextView choosePrioritySubtitle;

    //Submit
    @BindView(R.id.button_publish)
    protected Button publishButton;

    @BindView(R.id.bottom_sheet_my_adress_add_anomaly)
    protected TextView bottom_sheet_my_adress_add_anomaly;

    private boolean byUser;
    private boolean isValidAddress;

    String mCurrentPhotoPath = "";

    boolean firstPicLayout;

    String savechooseTypeSubtitle;
    String saveCategoryType;

    String txtDescription;
    String txtDescriptionLimit;

    int priorityId;

    ImageButton pencilAddress;

    @Override
    protected void resolveDaggerDependency() {
        DaggerPresenterComponent.builder()
                .applicationComponent(getApplicationComponent())
                .presenterModule(new PresenterModule(this))
                .build()
                .inject(this);
    }


    @Override
    protected int getContentView() {
        return R.layout.add_anomaly_activity_layout;
    }

    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);

        //check if we come from a draft
        if (TextUtils.isEmpty(intent.getStringExtra(Constants.EXTRA_DRAFT))) {
            // Retrieve my last position
            if (getIntent().getExtras() != null) {
                myLastPosition = getIntent().getExtras().getParcelable(Constants.EXTRA_CURRENT_LOCATION);
            }
            if (myLastPosition != null) {

                if (NetworkUtils.isConnected(getApplicationContext())) {

                    findAdresseWithLatLng(myLastPosition, getIntent().getStringExtra(Constants.EXTRA_SEARCH_BAR_ADDRESS));

                } else {
                    setUpCurrentAdress("", myLastPosition);
                }
            }

            presenter.setPriority(PRIORITIES_IDS.get(R.id.priority_low));

        } else {
            final String jsonDraft = intent.getStringExtra(Constants.EXTRA_DRAFT);
            final Incident draft = new GsonBuilder().create().fromJson(jsonDraft, Incident.class);
            presenter.openDraft(draft.getId());
            if (!TextUtils.isEmpty(draft.getDescriptive())) {
                onDescriptionResult(RESULT_OK, new Intent().putExtra(Constants.EXTRA_DESCRIPTION, draft.getDescriptive()));
            }
            onPriorityResult(RESULT_OK, new Intent().putExtra(Constants.EXTRA_PRIORITY_ID, PRIORITIES_IDS.keyAt(PRIORITIES_IDS.indexOfValue(draft.getPriorityId()))));

            if (draft.getLat() != null && draft.getLng() != null) {
                setUpCurrentAdress(draft.getAddress(), new LatLng(Double.valueOf(draft.getLat()), Double.valueOf(draft.getLng())));
            }

            if (!TextUtils.isEmpty(draft.getCategoryId())) {
                onTypeResult(RESULT_OK, new Intent().putExtra(Constants.EXTRA_CATEGORY_ID, draft.getCategoryId()).putExtra(Constants.EXTRA_CATEGORY_NAME, draft.getAlias()));
            }
            if (draft.getPicture1() != null) {
                showPicture(draft.getPicture1());
            }

            if (draft.getPicture2() != null) {
                showPicture(draft.getPicture2());
            }

        }


        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        if (null != toolbar) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.anomaly_screen_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        }
        // TODO refactor Clic on pen to search address
        pencilAddress = (ImageButton) findViewById(R.id.pen_add_anomaly);
        pencilAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPlace(v);

            }
        });
        enableCreateIncident();
    }

    /**
     * TODO
     *
     * @param adress
     * @param location
     */
    private void setUpCurrentAdress(String adress, LatLng location) {
        // setup fragment and hide or not bottom sheet
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MapAnomalyFragment mapAnomalyFragment = MapAnomalyFragment.newInstance(location);
        fragmentTransaction.replace(R.id.framelayout_add_anomaly, mapAnomalyFragment);
        fragmentTransaction.commitNowAllowingStateLoss();

        if (!adress.equals("")) {
            bottom_sheet_my_adress_add_anomaly.setText(MiscTools.whichPostalCode(adress));
            isValidAddress = true;
        } else {
            bottom_sheet_my_adress_add_anomaly.setText(location.latitude + ", " + location.longitude);
            isValidAddress = false;
        }

        // Format address
        Log.i(TAG, "Formatage adresse selon pattern BO : " + MiscTools.reformatArrondissement(adress));
        presenter.setAdress(MiscTools.reformatArrondissement(adress), location);

    }

    @OnClick(R.id.add_anomaly_photo)
    public void takePhotoOrViewGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            selectImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                }
                return;
            }
        }
    }

    //TODO remettre au propre et PAS en dur
    public void selectImage() {
        final CharSequence[] items = {"Prendre une photo", "Choisir dans la galerie", "Annuler"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajouter");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("Prendre une photo".equals(items[which])) {
                    cameraIntent();
                } else if ("Choisir dans la galerie".equals(items[which])) {
                    galleryIntent();

                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

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

    private void cameraIntent() {

//        if (android.os.Build.VERSION.SDK_INT > 20) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Crashlytics.logException(e);
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
//        } else {
//            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAKE_PICTURE_REQUEST_CODE);
//        }
    }

    private void galleryIntent() {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Sélectionnez une photo"), CHOOSE_FROM_GALLERY_REQUEST_CODE);
    }

    @OnClick(R.id.layout_choose_type)
    public void chooseType() {
        startActivityForResult(new Intent(AddAnomalyActivity.this, CategoryActivity.class), CHOOSE_TYPE_REQUEST_CODE);
    }

    @OnClick({R.id.layout_choose_priority, R.id.priority_layout_parent})
    public void choosePriority() {
        startActivityForResult(new Intent(AddAnomalyActivity.this, ChoosePriorityActivity.class), CHOOSE_PRIORITY_REQUEST_CODE);
    }

    @OnClick({R.id.layout_description, R.id.description_layout_parent})
    public void setDescription() {
        final Intent intent = new Intent(AddAnomalyActivity.this, SetDescriptionActivity.class);
        if (presenter.getRequest().getIncident().getDescriptive() != null) {
            intent.putExtra(Constants.EXTRA_DESCRIPTION, presenter.getRequest().getIncident().getDescriptive());
        }
        startActivityForResult(intent, SET_DESCRIPTION_REQUEST_CODE);
    }

    // Setup search screen in add anomaly
    public void findPlace(View view) {
        try {
            AutocompleteFilter filter = new AutocompleteFilter.Builder()
                    .setCountry("Fr")
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();
            // .zzdB(getString(R.string.google_searchbar_wording)) in case you change wording

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(filter)
                            .setBoundsBias(new LatLngBounds(
                                    new LatLng(48.811310, 2.217569),
                                    new LatLng(48.905509, 2.413468)
                            ))
                            .build(this);

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Crashlytics.logException(e);
        }
    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);

                    Log.i(TAG, "onActivityResult: " + place.getAddress().toString());

                    if (!place.getAddress().toString().contains("Paris")) {
                        new AlertDialog.Builder(AddAnomalyActivity.this)
                                .setMessage(R.string.not_in_paris)
                                .setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        pencilAddress.performClick();
                                    }
                                }).show();

                    } else {
                        myLastPosition = place.getLatLng();
                        if (NetworkUtils.isConnected(getApplicationContext())) {
                            findAdresseWithLatLng(place.getLatLng(), place.getAddress().toString());
                        } else {
                            setUpCurrentAdress(place.getAddress().toString(), place.getLatLng());
                        }
                    }

                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i(TAG, status.getStatusMessage());
                }
                break;
            case CHOOSE_TYPE_REQUEST_CODE:
                onTypeResult(resultCode, data);
                break;
            case SET_DESCRIPTION_REQUEST_CODE:
                onDescriptionResult(resultCode, data);
                break;
            case CHOOSE_PRIORITY_REQUEST_CODE:
                onPriorityResult(resultCode, data);
                break;
            case TAKE_PICTURE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
//                    if (android.os.Build.VERSION.SDK_INT < 20) {
                    rotateResizeAndCompress();
                    showPicture(mCurrentPhotoPath);
//                    } else {
//                        onCaptureImageResult(data);
//                    }
                }
                break;
            case CHOOSE_FROM_GALLERY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    onSelectFromGalleryResult(data);
                }
                break;
            case LOGIN_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    pleaseBePatientDialog();
                    presenter.createIncident();
                }
                break;
            default:
                Log.d(TAG, "onActivityResult : " + requestCode);
                break;
        }
    }

    private void rotateResizeAndCompress() {
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

    private void onTypeResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            typeTreatment(data.getStringExtra(Constants.EXTRA_CATEGORY_NAME), data.getStringExtra(Constants.EXTRA_CATEGORY_ID));
        }
        enableCreateIncident();
    }

    private void typeTreatment(String typeSubtitle, String categoryType) {
        chooseTypeSubtitle.setText(typeSubtitle);
        presenter.setCategory(categoryType);
        chooseType.setImageResource(R.drawable.ic_check_circle_pink_24px);
    }

    /**
     * Callback after SET_DESCRIPTION_REQUEST_CODE
     *
     * @param resultCode result
     * @param data       intent containig the description
     */
    private void onDescriptionResult(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            txtDescription = data.getStringExtra(Constants.EXTRA_DESCRIPTION);
            txtDescriptionLimit = txtDescription;
            if (txtDescription.length() > 40) {
                txtDescriptionLimit = txtDescription.substring(0, 40) + "...";
            }
            textDescription.setText(txtDescriptionLimit);
            if (txtDescription.length() > 0) {
                chooseDescription.setImageResource(R.drawable.ic_check_circle_pink_24px);
            } else {
                chooseDescription.setImageResource(R.drawable.ic_check_circle_grey_24px);
            }
            presenter.setDescription(data.getStringExtra(Constants.EXTRA_DESCRIPTION));
        }
    }

    /**
     * Callback after CHOOSE_PRIORITY_REQUEST_CODE
     *
     * @param resultCode result
     * @param data       intent containig the choosen priority
     */
    private void onPriorityResult(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            priorityId = data.getIntExtra(Constants.EXTRA_PRIORITY_ID, DEFAULT_PRIORITY_ID);
            priorityTreatment(priorityId);
        }
    }

    private void priorityTreatment(int thePriority) {
        presenter.setPriority(PRIORITIES_IDS.get(thePriority));
        choosePrioritySubtitle.setText(getString(PRIORITIES_LIBELLE.get(thePriority)));
        if (thePriority == DEFAULT_PRIORITY_ID) {
            defaultPriorityLabel.setVisibility(View.VISIBLE);
        } else {
            defaultPriorityLabel.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Callback after TAKE_PICTURE_REQUEST_CODE
     *
     * @param data intent containig the picture taken
     */
    private void onCaptureImageResult(final Intent data) {
        final Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        addPictureToPlaceHolder(thumbnail);
    }

    /**
     * Callback after CHOOSE_FROM_GALLERY_REQUEST_CODE
     *
     * @param data intent containig the picture choosen
     */
    private void onSelectFromGalleryResult(final Intent data) {
        if (data != null) {
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                addPictureToPlaceHolder(thumbnail);
            } catch (IOException e) {
                Crashlytics.logException(e);
                Log.e(TAG, e.getMessage(), e);
            }
        }

    }

    /**
     * Place a thumbnail on the placeholder after compressing the Bitmap.
     *
     * @param thumbnail thumbnail
     */
    private void addPictureToPlaceHolder(Bitmap thumbnail) {
        presenter.savePictureInFile(thumbnail);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // We prefer saving all datas because photo intent may kill app before going back

        if (null != mCurrentPhotoPath)
            outState.putString(Constants.ADD_ANO_CURRENT_PHOTO_PATH, mCurrentPhotoPath);

        if (null != presenter.getPicture1State())
            outState.putString(Constants.ADD_ANO_PICTURE_1, presenter.getPicture1State());
        if (null != presenter.getPicture2State())
            outState.putString(Constants.ADD_ANO_PICTURE_2, presenter.getPicture2State());

        if (null != savechooseTypeSubtitle)
            outState.putString(Constants.ADD_ANO_TYPE_SUBTITLE, savechooseTypeSubtitle);
        if (null != saveCategoryType)
            outState.putString(Constants.ADD_ANO_TYPE_CATEGORY, saveCategoryType);

        if (null != textDescription)
            outState.putString(Constants.ADD_ANO_TXT_DESCRIPTION, txtDescription);
        if (null != txtDescriptionLimit)
            outState.putString(Constants.ADD_ANO_TXT_DESCRIPTION_SHORT, txtDescriptionLimit);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {

        if (null != state.getString(Constants.ADD_ANO_CURRENT_PHOTO_PATH))
            mCurrentPhotoPath = state.getString(Constants.ADD_ANO_CURRENT_PHOTO_PATH);
        if (null != state.getString(Constants.ADD_ANO_PICTURE_1))
            showPicture(state.getString(Constants.ADD_ANO_PICTURE_1));
        if (null != state.getString(Constants.ADD_ANO_PICTURE_2))
            showPicture(state.getString(Constants.ADD_ANO_PICTURE_2));

        if (null != state.getString(Constants.ADD_ANO_TYPE_SUBTITLE) && null != state.getString(Constants.ADD_ANO_TYPE_CATEGORY)) {
            typeTreatment(state.getString(Constants.ADD_ANO_TYPE_SUBTITLE), state.getString(Constants.ADD_ANO_TYPE_CATEGORY));
        }

        if (null != state.getString(Constants.ADD_ANO_TXT_DESCRIPTION) && null != state.getString(Constants.ADD_ANO_TXT_DESCRIPTION_SHORT)) {
            textDescription.setText(state.getString(Constants.ADD_ANO_TXT_DESCRIPTION_SHORT));
            presenter.setDescription(state.getString(Constants.ADD_ANO_TXT_DESCRIPTION));
            if (state.getString(Constants.ADD_ANO_TXT_DESCRIPTION).length() > 0) {
                chooseDescription.setImageResource(R.drawable.ic_check_circle_pink_24px);
            } else {
                chooseDescription.setImageResource(R.drawable.ic_check_circle_grey_24px);
            }
        }

        enableCreateIncident();

        super.onRestoreInstanceState(state);
    }


    public void showPicture(final String fileName) {
        presenter.addPictureToModel(fileName);

        firstPicLayout = myImageChoiceLayout.getVisibility() == View.GONE;

        //image1 placeholder is empty
        if (firstPicLayout) {
            //first picture set
            choosePicture.setImageResource(R.drawable.ic_check_circle_pink_24px);
            myImageChoiceLayout.setVisibility(View.VISIBLE);
            image1.setVisibility(View.VISIBLE);
            Glide.with(this).load(fileName).fitCenter().into(image1);
            myImageChoiceClose.setVisibility(View.VISIBLE);
            if (myImageChoice2Layout.getVisibility() == View.VISIBLE) {
                addAnomalyPhotoLayout.setVisibility(View.GONE);
                addPictureButton.setVisibility(View.GONE);
            }
        } else {
            //image2 placeholder is empty then
            myImageChoice2Layout.setVisibility(View.VISIBLE);
            image2.setVisibility(View.VISIBLE);
            Glide.with(this).load(fileName).fitCenter().into(image2);
            myImageChoice2Close.setVisibility(View.VISIBLE);
            //both pictures taken, we can remove the add picture button
            addAnomalyPhotoLayout.setVisibility(View.INVISIBLE);
            addPictureButton.setVisibility(View.INVISIBLE);
        }
        enableCreateIncident();
    }


    @OnClick(R.id.myImageChoiceClose)
    public void disablePic1() {
        presenter.removePicture1();
        myImageChoiceLayout.setVisibility(View.GONE);
        image1.setVisibility(View.GONE);
        myImageChoiceClose.setVisibility(View.GONE);
        addAnomalyPhotoLayout.setVisibility(View.VISIBLE);
        addPictureButton.setVisibility(View.VISIBLE);
        testPicturesAndGreyCheck();
        enableCreateIncident();
    }

    @OnClick(R.id.myImageChoice2Close)
    public void disablePic2() {
        presenter.removePicture2();
        myImageChoice2Layout.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        myImageChoice2Close.setVisibility(View.GONE);
        addAnomalyPhotoLayout.setVisibility(View.VISIBLE);
        addPictureButton.setVisibility(View.VISIBLE);
        testPicturesAndGreyCheck();
        enableCreateIncident();
    }

    public void testPicturesAndGreyCheck() {
        if (image1.getVisibility() == View.GONE && image2.getVisibility() == View.GONE) {
            choosePicture.setImageResource(R.drawable.ic_check_circle_grey_24px);
        }
    }


    /**
     * Check if the publish button should be enable by validating the form
     */
    private void enableCreateIncident() {
        if (checkMandatoryFields()) {
            publishButton.setClickable(true);
            publishButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
        } else {
            publishButton.setClickable(false);
            publishButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.grey_icon));
        }
    }


    /**
     * Find Postal Adresse with latitude longitude.
     *
     * @param latlng
     *         Latitude, Longitude
     *
     */
    private void findAdresseWithLatLng(LatLng latlng, String searchBarAddress) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String currentAddress = "";
        try {

            final List<Address> addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 2); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            int index = 0;

            if(addresses.get(index).getThoroughfare() == null) {
                //fisrt address as consider invalid
                index = 1;
            }

            final String address = addresses.get(index).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            Log.i(TAG, "adress " + address);
            final String city = addresses.get(index).getLocality();
            Log.i(TAG, "city " + city);
            final String country = addresses.get(index).getCountryName();
            Log.i(TAG, "country " + country);


            final String postalCode = addresses.get(index).getPostalCode();
            Log.i(TAG, "cp " + postalCode);

            currentAddress = address;

            if ( searchBarAddress != null && ! "".equals(searchBarAddress)  ) {
                currentAddress = currentAddress.replace(currentAddress.substring(0, currentAddress.indexOf(",")), searchBarAddress);
            }
            Log.i(TAG, "current " + currentAddress);

        } catch (IOException e) {
            Crashlytics.logException(e);
            Log.e(TAG, e.getMessage(), e);
        }

        setUpCurrentAdress(currentAddress, latlng);
    }

    /**
     * @return true if all required fields a<br>
     * false otherwise
     */
    private boolean checkMandatoryFields() {
        return presenter.isIncidentValid();
    }


    /**
     * Click action on the publish button
     */
    @OnClick(R.id.button_publish)
    public void onPublish() {

        if (!isValidAddress) {
            if (NetworkUtils.isConnected(getApplicationContext())) {
                findAdresseWithLatLng(myLastPosition, null);
                isValidAddress = true;
            }
        }
        presenter.onPublish();

    }


    /**
     * Open the greeting dialog.
     * The incident is created after the user closes this dialog.
     *
     * @param askEmail true to let the user set an email or connect to his VDP account <br>
     *                 false to just show the greetings
     */
    @Override
    public void showGreetingsDialog(boolean askEmail) {

        //user not connected, we ask him to input an email or to connect to his VDP account
        if (askEmail) {

            // custom dialog
            greetingsDialog = new Dialog(this);
            greetingsDialog.setContentView(R.layout.greetings_dialog_keep_contact);
            greetingsDialog.setCancelable(false);

            // set the custom dialog components - text, image and button
            ImageView closeSave = (ImageView) greetingsDialog.findViewById(R.id.btnCloseSave);
            // Close Button
            closeSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pleaseBePatientDialog();
                    presenter.createIncident();
                }
            });

            LinearLayout cnx2account = (LinearLayout) greetingsDialog.findViewById(R.id.cnx2account);
            TextView cnx2mail = (TextView) greetingsDialog.findViewById(R.id.cnx2mail);

            if (greetingsDialog.getWindow() != null) {
                greetingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Fix Dialog Size
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(greetingsDialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                greetingsDialog.getWindow().setAttributes(lp);
                greetingsDialog.show();
            }

            cnx2account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginParisianAccount();
                }
            });

            cnx2mail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recordEmail();
                }
            });

        } else {

            pleaseBePatientDialog();
            presenter.createIncident();
        }


    }

    private void pleaseBePatientDialog() {

        if (null != greetingsDialog) greetingsDialog.dismiss();

        greetingsDialogSendIncident = new Dialog(this);
        greetingsDialogSendIncident.setContentView(R.layout.greetings_dialog_send_incident);
        greetingsDialogSendIncident.setCancelable(false);

        if (greetingsDialogSendIncident.getWindow() != null) {
            greetingsDialogSendIncident.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Fix Dialog Size
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(greetingsDialogSendIncident.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            greetingsDialogSendIncident.getWindow().setAttributes(lp);
            greetingsDialogSendIncident.show();
        }

    }


    @Override
    public void showDialogErrorSaveDraft() {
        new AlertDialog.Builder(this).setMessage(R.string.save_anomaly_failure)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.saveDraft();
                        dialog.dismiss();
                        navigateBack();
                    }
                }).show();
    }


    @Override
    public void showIncidentAndPhotosOkThankYou(boolean smile) {

        greetingsDialogSendIncident.dismiss();

        if (smile) {
            Log.i(TAG, "showIncidentAndPhotosOkThankYou: OK");

            greetingsDialogSendOkThanks = new Dialog(this);
            greetingsDialogSendOkThanks.setContentView(R.layout.greetings_dialog_send_ok_thanks);
            greetingsDialogSendOkThanks.setCancelable(false);

            // set the custom dialog components - text, image and button
            ImageView close = (ImageView) greetingsDialogSendOkThanks.findViewById(R.id.btnCloseSuccess);
            // Close Button
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    greetingsDialogSendOkThanks.dismiss();
                    finish();
                }
            });

            if (greetingsDialogSendOkThanks.getWindow() != null) {
                greetingsDialogSendOkThanks.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Fix Dialog Size
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(greetingsDialogSendIncident.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                greetingsDialogSendOkThanks.getWindow().setAttributes(lp);
                greetingsDialogSendOkThanks.show();
            }


        } else {
            Log.i(TAG, "showIncidentAndPhotosOkThankYou: KO");

            greetingsDialogSendError = new Dialog(this);
            greetingsDialogSendError.setContentView(R.layout.greetings_dialog_send_error);
            greetingsDialogSendError.setCancelable(false);

            // set the custom dialog components - text, image and button
            ImageView close = (ImageView) greetingsDialogSendError.findViewById(R.id.btnCloseFailure);
            // Close Button
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    greetingsDialogSendError.dismiss();
                }
            });

            if (greetingsDialogSendError.getWindow() != null) {
                greetingsDialogSendError.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Fix Dialog Size
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(greetingsDialogSendError.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                greetingsDialogSendError.getWindow().setAttributes(lp);
                greetingsDialogSendError.show();
            }

        }
    }

    /**
     * Starts the LoginActivity to connect to a VDP account
     */
    private void loginParisianAccount() {
        startActivityForResult(new Intent(AddAnomalyActivity.this, LoginActivity.class), LOGIN_REQUEST_CODE);
    }

    /**
     * Display another dialog to input an email adress
     */
    private void recordEmail() {
        emailDialog = new Dialog(this);
        emailDialog.setContentView(R.layout.email_record_dialog);

        final TextInputEditText inputEmail = (TextInputEditText) emailDialog.findViewById(R.id.input_email);
        final String lastUsedEmail = presenter.getLastUsedEmail();
        if (!TextUtils.isEmpty(lastUsedEmail)) {
            inputEmail.append(lastUsedEmail);
        }

        if (emailDialog.getWindow() != null) {
            emailDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Fix Dialog Size
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(emailDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            emailDialog.show();
            emailDialog.getWindow().setAttributes(lp);
        }


        TextView saveEmailPopupCancelBtn = (TextView) emailDialog.findViewById(R.id.save_email_popup_cancel_btn);
        TextView saveEmailPopupConfirmBtn = (TextView) emailDialog.findViewById(R.id.save_email_popup_confirm_btn);


        saveEmailPopupCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailDialog.dismiss();
            }
        });

        saveEmailPopupConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String txtEmail = inputEmail.getText().toString();
                if (presenter.setEmail(txtEmail)) {
                    emailDialog.dismiss();
                    pleaseBePatientDialog();
                    presenter.createIncident();
                } else {
                    inputEmail.setError(getString(R.string.invalid_email));
                }

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        byUser = true;
        presenter.onQuit(true);
        return true;
    }

    // Avoid user to quit the app
    @Override
    public void onBackPressed() {
        byUser = true;
        presenter.onQuit(true);
    }

    /**
     * Callback after an incident is successfully created.
     * Upload the picture and go back to the main screen
     *
     * @param incidentId id of the incident newly created
     */
    @Override
    public void onIncidentCreated(Integer incidentId) {
        presenter.uploadPictures(incidentId);
//        startActivity(new Intent(AddAnomalyActivity.this, WelcomeMapActivity.class));
//        finish();
    }


    /**
     * Callback after a creation failure
     *
     * @param errorMessage error message received by the WS
     */
    @Override
    public void onIncidentNotCreated(String errorMessage) {
        Toast.makeText(this, "Une erreur technique est survenue pendant la création du signalement.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void proposeDraft() {
        new AlertDialog.Builder(this)
                .setTitle("Attention")
                .setMessage("Vous n'avez pas finalisé votre déclaration d'anomalie.\nSouhaitez vous enregistrer un brouillon ?")
                .setCancelable(true)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.saveDraft();
                        dialog.cancel();
                        navigateBack();
                    }
                }).setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.removeDraft();
                dialog.cancel();
                navigateBack();
            }
        }).show();
    }

    @Override
    public void navigateBack() {
        finish();
    }

    @Override
    protected void onStop() {
        if (!byUser) {
            presenter.onQuit(false);
        }

        super.onStop();
    }
}
