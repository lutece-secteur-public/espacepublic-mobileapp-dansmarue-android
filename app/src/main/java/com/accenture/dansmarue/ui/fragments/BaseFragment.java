package com.accenture.dansmarue.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.widget.Toast;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.app.DansMaRueApplication;

/**
 * Created by PK on 19/04/2017.
 */

public class BaseFragment extends Fragment {


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resolveDaggerDependency((DansMaRueApplication) getActivity().getApplication());
    }

    void resolveDaggerDependency(DansMaRueApplication application) {
    }

    public void displayConnectionError() {
        if (this.getContext() != null) {
            final Toast toastMessage = Toast.makeText(this.getContext(), R.string.info_no_network, Toast.LENGTH_LONG);
            toastMessage.setGravity(Gravity.TOP, 5, 5);
//            toastMessage.show();
        }
    }

}
