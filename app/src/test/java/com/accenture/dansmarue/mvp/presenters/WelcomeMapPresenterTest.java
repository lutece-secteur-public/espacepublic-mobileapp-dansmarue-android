package com.accenture.dansmarue.mvp.presenters;

import android.os.Looper;

import com.accenture.dansmarue.mvp.views.WelcomeMapView;
import com.accenture.dansmarue.services.SiraApiService;
import com.accenture.dansmarue.services.models.FollowRequest;
import com.accenture.dansmarue.services.models.SiraSimpleResponse;
import com.accenture.dansmarue.utils.PrefManager;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by d4v1d on 12/05/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AndroidSchedulers.class,Looper.class})
public class WelcomeMapPresenterTest extends TestCase {

    private WelcomeMapPresenter presenter;

    @Mock
    private WelcomeMapView view;
    @Mock
    private PrefManager prefManager;
    @Mock
    private SiraApiService siraApiService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        PowerMockito.stub(PowerMockito.method(AndroidSchedulers.class, "mainThread"))
                .toReturn(Schedulers.single());
        PowerMockito.stub(PowerMockito.method(Looper.class, "getMainLooper"))
                .toReturn(null);
        Mockito.when(siraApiService.follow((FollowRequest) Mockito.anyObject())).thenReturn(Single.just(new SiraSimpleResponse()));
        presenter = new WelcomeMapPresenter(view,prefManager,siraApiService);
    }

    @Test
    public void testFollowAnomalyNotConnected(){
        //Given
        final String incidentId="123456";
        ArgumentCaptor<FollowRequest> arg = ArgumentCaptor.forClass(FollowRequest.class);
        Mockito.when(prefManager.isConnected()).thenReturn(Boolean.FALSE);

        //When
        presenter.followAnomaly(incidentId);

        //Then
        Mockito.verify(siraApiService,Mockito.never()).follow(arg.capture());
        Mockito.verify(view).inviteToLogin();

    }

    @Test
    public void testFollowAnomalyConnected(){
        //Given
        final String incidentId="123456";
        ArgumentCaptor<FollowRequest> arg = ArgumentCaptor.forClass(FollowRequest.class);
        Mockito.when(prefManager.isConnected()).thenReturn(Boolean.TRUE);

        //When
        presenter.followAnomaly(incidentId);

        //Then
        Mockito.verify(siraApiService).follow(arg.capture());
        Mockito.verify(view,Mockito.never()).inviteToLogin();
        assertEquals("Incorrect parameter \"incidentId\" ",incidentId,arg.getValue().getIncidentId());

    }
}
