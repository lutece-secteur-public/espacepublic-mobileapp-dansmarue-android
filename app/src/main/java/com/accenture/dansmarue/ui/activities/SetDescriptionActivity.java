package com.accenture.dansmarue.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.ui.views.AccessibleTextInputEditText;
import com.accenture.dansmarue.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;


public class SetDescriptionActivity extends BaseActivity {

    @BindView(R.id.edit_text_description)
    protected AccessibleTextInputEditText description;

    @BindView(R.id.edit_text_title)
    protected TextView title;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    private String currentExtra = "";
    private String callingActivity;

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);

        callingActivity = intent.getStringExtra(Constants.EXTRA_CALLING_ACTIVITY);

        //Init champ si déjà saisi auparavant
        if (intent.getStringExtra(Constants.EXTRA_DESCRIPTION) != null) {
            title.setText(R.string.description);
            description.append(intent.getStringExtra(Constants.EXTRA_DESCRIPTION));
            currentExtra = Constants.EXTRA_DESCRIPTION;
        }
        else if (intent.getStringExtra(Constants.EXTRA_COMMENTAIRE_AGENT) != null) {
            title.setText(R.string.commentagent);
            description.append(intent.getStringExtra(Constants.EXTRA_COMMENTAIRE_AGENT));
            currentExtra = Constants.EXTRA_COMMENTAIRE_AGENT;
        }

        else if (intent.getStringExtra(Constants.EXTRA_COMMENTAIRE_AGENT_REQUALIFICATION) != null) {
            title.setText(R.string.commentagent);
            description.append(intent.getStringExtra(Constants.EXTRA_COMMENTAIRE_AGENT_REQUALIFICATION));
            currentExtra = Constants.EXTRA_COMMENTAIRE_AGENT_REQUALIFICATION;
        }

        else if (intent.hasExtra(Constants.EXTRA_COMMENTAIRE_PRECISIONS_TERRAIN)) {
            title.setText(R.string.text_title_precterrain);
            if(intent.getStringExtra(Constants.EXTRA_COMMENTAIRE_PRECISIONS_TERRAIN) != null) {
                description.append(intent.getStringExtra(Constants.EXTRA_COMMENTAIRE_PRECISIONS_TERRAIN));
            }
            currentExtra = Constants.EXTRA_COMMENTAIRE_PRECISIONS_TERRAIN;
        }

    }

    @Override
    protected int getContentView() {
        return R.layout.set_description_activity_layout;
    }


    @OnClick(R.id.img_validate_desc)
    public void validateDescription() {
        Intent intent;
        if(Constants.ACTIVITY_DETAILS_ANOMALY.equals(callingActivity)) {
            intent = new Intent(SetDescriptionActivity.this, AnomalyDetailsActivity.class);
        } else {
            intent = new Intent(SetDescriptionActivity.this, AddAnomalyActivity.class);
        }

        intent.putExtra(currentExtra, description.getText().toString());
        setResult(RESULT_OK, intent);

        finish();
    }
}
