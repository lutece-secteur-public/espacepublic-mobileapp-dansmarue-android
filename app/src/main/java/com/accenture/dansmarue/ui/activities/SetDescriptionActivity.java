package com.accenture.dansmarue.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.utils.Constants;

import butterknife.BindView;
import butterknife.OnClick;


public class SetDescriptionActivity extends BaseActivity {

    @BindView(R.id.edit_text_description)
    protected EditText description;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);

        //Init champ si déjà saisi auparavant
        if (intent.getStringExtra(Constants.EXTRA_DESCRIPTION) != null) {
            description.append(intent.getStringExtra(Constants.EXTRA_DESCRIPTION));
        }

    }

    @Override
    protected int getContentView() {
        return R.layout.set_description_activity_layout;
    }


    @OnClick(R.id.img_validate_desc)
    public void validateDescription() {
        Intent intent = new Intent(SetDescriptionActivity.this, AddAnomalyActivity.class);

        intent.putExtra(Constants.EXTRA_DESCRIPTION, description.getText().toString());
        setResult(RESULT_OK, intent);

        finish();
    }

}
