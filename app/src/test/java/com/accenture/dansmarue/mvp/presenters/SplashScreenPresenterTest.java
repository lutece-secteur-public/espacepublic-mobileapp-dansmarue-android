package com.accenture.dansmarue.mvp.presenters;

import android.app.Application;
import android.content.Context;
import android.os.Looper;

import com.accenture.dansmarue.mvp.views.SplashScreenView;
import com.accenture.dansmarue.services.ApiServiceEquipement;
import com.accenture.dansmarue.services.SiraApiService;
import com.accenture.dansmarue.services.SiraApiServiceMock;
import com.accenture.dansmarue.services.models.CategoryRequest;
import com.accenture.dansmarue.services.models.CategoryResponse;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.PrefManager;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PK on 23/03/2017.
 * Unit test implementation of {@link SplashScreenPresenter}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AndroidSchedulers.class,Looper.class})
public class SplashScreenPresenterTest extends TestCase{

    private SplashScreenPresenter splashScreenPresenter;

    @Mock
    private SplashScreenView view;
    @Mock
    private PrefManager prefManager;
    @Mock
    private Application application;
    @Mock
    private Context context;
    @Mock
    private FileOutputStream fos;
    @Mock
    private SiraApiService service;

    @Mock
    private ApiServiceEquipement apiServiceEquipement;

    @Mock
    private SiraApiServiceMock siraApiServiceMock;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        PowerMockito.stub(PowerMockito.method(AndroidSchedulers.class, "mainThread"))
                .toReturn(Schedulers.single());
        PowerMockito.stub(PowerMockito.method(Looper.class, "getMainLooper"))
                .toReturn(null);
        splashScreenPresenter = new SplashScreenPresenter(application,view,prefManager,service,siraApiServiceMock,apiServiceEquipement);
        Mockito.when(service.getCategories((CategoryRequest)Mockito.anyObject())).thenReturn(Single.just(new CategoryResponse()));
        Mockito.when(application.getApplicationContext()).thenReturn(context);
        Mockito.when(context.openFileOutput(Constants.FILE_CATEGORIES_JSON, Context.MODE_PRIVATE)).thenReturn(fos);
    }


    /**
     * Test SplashScreenPresenter#onCreare when the app is launched for the first time.
     * Check that the presenter calls SplashScreenView#onFirstTimeLaunch
     */
    public void testOnDataReadyFirstTimeLaunched(){
        //Given
        Mockito.when(prefManager.isFirstTimeLaunch()).thenReturn(Boolean.TRUE);

        //When
        splashScreenPresenter.onDataReady();

        //Then
        Mockito.verify(view).onFirstTimeLaunch();
        Mockito.verify(view,Mockito.never()).onLaunch();
    }


    /**
     * Test SplashScreenPresenter#OnDataReady when the app is launched for the first time.
     * Check that the presenter calls SplashScreenView#onLaunch
     */
    public void testOnDataReadyNotFirstTimeLaunched(){
        //Given
        Mockito.when(prefManager.isFirstTimeLaunch()).thenReturn(Boolean.FALSE);

        //When
        splashScreenPresenter.onDataReady();

        //Then
        Mockito.verify(view).onLaunch();
        Mockito.verify(view,Mockito.never()).onFirstTimeLaunch();
    }

    /**
     * Test SplashScreenPresenter#checkAndLoadCategories.
     * Verify that the presenters subscribes to the getCategories Service and set up the request with the correct parameters
     */
    public void testCheckAndLoadCategories(){
        //Given
        final String version="2";
        Mockito.when(prefManager.getCategoriesVersion()).thenReturn(version);
        ArgumentCaptor<CategoryRequest> arg = ArgumentCaptor.forClass(CategoryRequest.class);

        //When
        splashScreenPresenter.checkAndLoadCategories();

        //Then
        //Check that the service got called
        Mockito.verify(service).getCategories(arg.capture());
        //--> with a correct request (version from prefManager)
        assertEquals("Incorrect parameter \"curVersion\" ",version,arg.getValue().getCurVersion());
    }


    /**
     * Test SplashScreenPresenter#onSuccess (callback of getCategories WS when the version (referential version) didn't change)
     * Verify that the presenter notify the view without doing anything else
     */
    public void testOnSuccessLatestVersion() throws FileNotFoundException {
        //Given
        final String actualVersion = "2";
        final String serviceVersion = "2";
        Mockito.when(prefManager.getCategoriesVersion()).thenReturn(actualVersion);

        final CategoryResponse response = generateNullResponse(serviceVersion);

        //When
        splashScreenPresenter.onSuccess(response);

        //Then
        //we didnt update the version nor recreate the JSON file
        Mockito.verify(prefManager, Mockito.never()).setCategoriesVersion(serviceVersion);
        Mockito.verify(application, Mockito.never()).getApplicationContext();
        Mockito.verify(context, Mockito.never()).deleteFile(Constants.FILE_CATEGORIES_JSON);
        Mockito.verify(context, Mockito.never()).openFileOutput(Constants.FILE_CATEGORIES_JSON,Context.MODE_PRIVATE);
        //we always notify the view
        Mockito.verify(view).dataReady();
    }

    /**
     * Test SplashScreenPresenter#onSuccess (callback of getCategories WS when the version (referential version) changed)
     * Verify that the presenter notify the view after creating a new json file and updating the curVersion
     */
    public void testOnSuccessNewVersion() throws FileNotFoundException {
        //Given
        final String actualVersion = "2";
        final String serviceVersion = "3";
        Mockito.when(prefManager.getCategoriesVersion()).thenReturn(actualVersion);

        final CategoryResponse response = generateNullResponse(serviceVersion);

        //When
        splashScreenPresenter.onSuccess(response);

        //Then
        //we didnt update the version nor recreate the JSON file
        Mockito.verify(prefManager).setCategoriesVersion(serviceVersion);
        Mockito.verify(application,Mockito.atLeastOnce()).getApplicationContext();
        Mockito.verify(context).deleteFile(Constants.FILE_CATEGORIES_JSON);
        Mockito.verify(context).openFileOutput(Constants.FILE_CATEGORIES_JSON,Context.MODE_PRIVATE);
        //we always notify the view
        Mockito.verify(view).dataReady();
    }


    /**
     * Generate a simple WS response
     * @param version
     * version to set on the response (curVersion)
     * @return
     * CategoryEquipementResponse
     */
    private CategoryResponse generateNullResponse(String version) {
        final CategoryResponse result = new CategoryResponse();
        result.getAnswer().setVersion(version);
        result.getAnswer().setStatus(Constants.STATUT_WS_OK);
        return result;
    }
}

