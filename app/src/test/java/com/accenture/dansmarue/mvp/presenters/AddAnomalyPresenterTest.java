package com.accenture.dansmarue.mvp.presenters;

import android.app.Application;
import android.content.Context;

import com.accenture.dansmarue.mvp.models.Incident;
import com.accenture.dansmarue.mvp.views.AddAnomalyView;
import com.accenture.dansmarue.services.SiraApiService;
import com.accenture.dansmarue.services.models.SaveIncidentRequest;
import com.accenture.dansmarue.utils.Constants;
import com.accenture.dansmarue.utils.PrefManager;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.io.FileOutputStream;

import static org.mockito.Matchers.eq;

/**
 * Created by PK on 03/05/2017.
 */

public class AddAnomalyPresenterTest extends TestCase {

    private AddAnomalyPresenter presenter;

    @Mock
    private AddAnomalyView view;
    @Mock
    private PrefManager prefManager;
    @Mock
    private SiraApiService service;
    @Mock
    private Application application;
    @Mock
    private Context context;
    @Mock
    private FileOutputStream fos;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        presenter = new AddAnomalyPresenter(view,service,prefManager,application);

        Mockito.when(application.getApplicationContext()).thenReturn(context);
        Mockito.when(context.openFileOutput(Mockito.endsWith(Constants.FILE_DRAFT_SUFFIXE), eq(Context.MODE_PRIVATE))).thenReturn(fos);

    }

    public void testOnQuitByUserEmptyRequest(){
        //Given
        // set up the request to be empty (all fields empty)
        final SaveIncidentRequest request = generateRequest(new Incident());
        Whitebox.setInternalState(presenter, "request",request );

        //When
        presenter.onQuit(true);

        //Then
        Mockito.verify(view,Mockito.never()).proposeDraft();
        Mockito.verify(view).navigateBack();
    }

    public void testOnQuitByUserFilledRequest(){
        //Given
        // set up the request to be empty (all fields empty)
        final Incident incident = new Incident();
        incident.setDescriptive("test description");
        final SaveIncidentRequest request = generateRequest(incident);
        Whitebox.setInternalState(presenter, "request",request );

        //When
        presenter.onQuit(true);

        //Then
        Mockito.verify(view).proposeDraft();
        Mockito.verify(view,Mockito.never()).navigateBack();
    }

    public void testOnQuitBySystemFilledRequest(){
        //Given
        // set up the request to be empty (all fields empty)
        final Incident incident = new Incident();
        incident.setDescriptive("test description");
        final SaveIncidentRequest request = generateRequest(incident);
        Whitebox.setInternalState(presenter, "request",request );

        //When
        presenter.onQuit(false);

        //Then
        Mockito.verify(view,Mockito.never()).proposeDraft();
        Mockito.verify(view,Mockito.never()).navigateBack();
        //TODO check that draft is saved
    }

    public void testOnQuitBySystemEmptyRequest(){
        //Given
        // set up the request to be empty (all fields empty)
        final SaveIncidentRequest request = generateRequest(new Incident());
        Whitebox.setInternalState(presenter, "request",request );

        //When
        presenter.onQuit(false);

        //Then
        Mockito.verify(view,Mockito.never()).proposeDraft();
        Mockito.verify(view,Mockito.never()).navigateBack();
        //TODO check that draft is not saved
    }

    private SaveIncidentRequest generateRequest(Incident incident) {
        final SaveIncidentRequest result = new SaveIncidentRequest();
        result.setIncident(incident);
        return result;
    }
}
