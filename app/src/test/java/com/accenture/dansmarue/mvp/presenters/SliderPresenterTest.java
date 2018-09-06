package com.accenture.dansmarue.mvp.presenters;

import com.accenture.dansmarue.mvp.views.SliderView;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Created by PK on 27/03/2017.
 * Unit test of the {@link SliderPresenter}
 */
public class SliderPresenterTest extends TestCase {

    private SliderPresenter sliderPresenter;
    @Mock
    private SliderView view;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        sliderPresenter = new SliderPresenter(view);
    }


    /**
     * Test SliderPresenter#onBeginButtonPressed.
     * Check that the presenter calls SliderView#launchRuntimeGeolocPermissionRequestActivity
     */
    public void testOnBeginButtonPressed(){
        //Given

        //When
        sliderPresenter.onBeginButtonPressed();

        //Then
        Mockito.verify(view).launchRuntimeGeolocPermissionRequestActivity();
    }
}
