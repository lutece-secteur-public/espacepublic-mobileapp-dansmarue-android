package com.accenture.dansmarue.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.SparseIntArray;
import android.view.View;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.utils.Constants;

import butterknife.BindView;
import butterknife.OnClick;

public class ChoosePriorityActivity extends BaseActivity {

    /**
     * Mappings to retrieve the text and the id of the different priorities based on theire view Ids
     */
    public static SparseIntArray PRIORITIES_LIBELLE = new SparseIntArray(4);
    public static SparseIntArray PRIORITIES_IDS = new SparseIntArray(4);

    static {
        PRIORITIES_LIBELLE.put(R.id.priority_low, R.string.text_priority_low);
        PRIORITIES_LIBELLE.put(R.id.priority_low_text_default, R.string.text_priority_low);
        PRIORITIES_LIBELLE.put(R.id.priority_medium, R.string.text_priority_medium);
        PRIORITIES_LIBELLE.put(R.id.priority_high, R.string.text_priority_high);

        PRIORITIES_IDS.put(R.id.priority_low_text_default, 3);
        PRIORITIES_IDS.put(R.id.priority_low, 3);
        PRIORITIES_IDS.put(R.id.priority_medium, 2);
        PRIORITIES_IDS.put(R.id.priority_high, 1);
    }

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    protected void onViewReady(final Bundle savedInstanceState, final Intent intent) {
        super.onViewReady(savedInstanceState, intent);

        //Set up toolbar title and back button
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.priority);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24px);
    }

    /**
     * Called when one of the priority gets choosen.
     *
     * @param v clicked textView
     */
    @OnClick({R.id.priority_low, R.id.priority_medium, R.id.priority_high, R.id.priority_low_text_default})
    public void choosePriority(final View v) {
        final Intent intent = new Intent(ChoosePriorityActivity.this, AddAnomalyActivity.class);
        //We only send back the viewId back to the AddAnomalyActivity which will retrieve the actual WS id and the text to display thanks to PRIORITIES_ID and PRIORITIES_LIBELLE
        if (R.id.priority_low_text_default == v.getId()) {
            intent.putExtra(Constants.EXTRA_PRIORITY_ID, R.id.priority_low);
        } else {
            intent.putExtra(Constants.EXTRA_PRIORITY_ID, v.getId());
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected int getContentView() {
        return R.layout.choose_priority_activity_layout;
    }
}
