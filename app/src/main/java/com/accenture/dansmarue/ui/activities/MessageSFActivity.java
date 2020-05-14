package com.accenture.dansmarue.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.mvp.models.MessageServiceFait;
import com.accenture.dansmarue.ui.adapters.MessageSFAdapter;
import com.accenture.dansmarue.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MessageSFActivity  extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.messages)
    protected ListView listView;

    private MessageSFAdapter adapter;

    @Override
    protected void onViewReady(final Bundle savedInstanceState, final Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        listView.setOnItemClickListener(this);

        HashMap<Integer, MessageServiceFait> messages = ( HashMap<Integer, MessageServiceFait> )intent.getSerializableExtra(Constants.EXTRA_LIST_MESSAGE_SERVICE_FAIT);

        if (adapter == null) {
            adapter = new MessageSFAdapter(this, R.layout.message_sf, messages);
            listView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.setMessages(messages);
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        if (adapter.getMessages().get(position) != null) {
            Intent intent = new Intent(this, AnomalyDetailsActivity.class);
            intent.putExtra(Constants.EXTRA_MESSAGE_SERVICE_FAIT_SELECT, adapter.getMessages().get(position));
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    @Override
    protected int getContentView() {
        return R.layout.message_sf_activity_layout;
    }

    @OnClick(R.id.arrow_back_type)
    public void backType() {
        finish();
    }
}
