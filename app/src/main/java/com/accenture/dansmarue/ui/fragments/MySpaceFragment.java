package com.accenture.dansmarue.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.app.DansMaRueApplication;
import com.accenture.dansmarue.di.components.DaggerPresenterComponent;
import com.accenture.dansmarue.di.modules.PresenterModule;
import com.accenture.dansmarue.mvp.views.MySpaceView;
import com.accenture.dansmarue.ui.activities.AnomaliesActivity;
import com.accenture.dansmarue.ui.activities.InternalWebViewActivity;
import com.accenture.dansmarue.ui.activities.LoginActivity;
import com.accenture.dansmarue.ui.activities.MySpaceHelpActivity;
import com.accenture.dansmarue.ui.activities.MySpaceNewsActivity;
import com.accenture.dansmarue.ui.activities.PrefAboutActivity;
import com.accenture.dansmarue.ui.activities.PrefProfilActivity;
import com.accenture.dansmarue.utils.PrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MySpaceFragment extends BaseFragment implements MySpaceView {

    @BindView(R.id.item_layout_se_connecter)
    protected LinearLayout layoutSeConnecter;

    @BindView(R.id.item_mon_profil)
    protected TextView itemMonProfil;

    @BindView(R.id.btn_deco)
    protected Button buttonDisconnect;

    private PrefManager prefManager;


    public MySpaceFragment() {
        // Required empty public constructor
    }

    public static MySpaceFragment newInstance() {
        // Required empty public constructor
        return new MySpaceFragment();
    }

    @Override
    protected void resolveDaggerDependency(DansMaRueApplication application) {
        DaggerPresenterComponent.builder()
                .applicationComponent(application.getApplicationComponent())
                .presenterModule(new PresenterModule(this))
                .build()
                .inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //activity = (ProfileFragment.OnProfileFragmentInteractionListener) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //activity = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        prefManager = new PrefManager(getContext());
        if (prefManager.isConnected()) {
            itemMonProfil.setVisibility(View.VISIBLE);
            layoutSeConnecter.setVisibility(View.GONE);
            buttonDisconnect.setVisibility(View.VISIBLE);
        } else {
            itemMonProfil.setVisibility(View.GONE);
            layoutSeConnecter.setVisibility(View.VISIBLE);
            buttonDisconnect.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_profile2, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @OnClick(R.id.btn_mon_paris)
    public void onClickMonParisSignIn() {
        startActivity(new Intent(getActivity(), LoginActivity.class));

    }

    @OnClick(R.id.item_se_connecter)
    public void onClickSignIn() {
        onClickMonParisSignIn();
    }

    @OnClick(R.id.item_mon_profil)
    public void goToMonProfil() {
        startActivity(new Intent(getActivity(), PrefProfilActivity.class));
    }

    @OnClick(R.id.item_mes_anomalies)
    public void goToMesAnomalies() {
        if (prefManager.isConnected()) {
            startActivity(new Intent(getActivity(), AnomaliesActivity.class));
        } else {
            onClickMonParisSignIn();
        }
    }


    @OnClick(R.id.item_actualites)
    public void goToActualites() {
        startActivity(new Intent(getActivity(), MySpaceNewsActivity.class));
    }

    @OnClick(R.id.item_aide)
    public void goToAideConseils() {

        startActivity(new Intent(getActivity(), MySpaceHelpActivity.class));
    }

    @OnClick(R.id.item_pref)
    public void goToPrefActivity() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @OnClick(R.id.item_cgu)
    public void goToCgu() {
        String urlCGU = "https://teleservices.paris.fr/formsddct/jsp/site/Portal.jsp?page=forms&view=stepView&id_form=82&init=true";
        Intent intent = new Intent(getActivity(), InternalWebViewActivity.class);
        intent.putExtra(InternalWebViewActivity.WEBSITE_ADDRESS, urlCGU);
        startActivity(intent);
    }

    @OnClick(R.id.item_pcvp)
    public void goToPolitiqueConfidentialite() {
        String urlPcvp = "https://www.paris.fr/pages/mentions-legales-235#confidentialite-et-protection-des-donnees";
        Intent intent = new Intent(getActivity(), InternalWebViewActivity.class);
        intent.putExtra(InternalWebViewActivity.WEBSITE_ADDRESS, urlPcvp);
        startActivity(intent);
    }

    @OnClick(R.id.item_about)
    public void goToAbout() {
        startActivity(new Intent(getActivity(), PrefAboutActivity.class));
    }


    @OnClick(R.id.btn_deco)
    public void onClickSignOut() {
        onClickMonParisSignIn();
    }


    public interface OnProfileFragmentInteractionListener {
    }
}
