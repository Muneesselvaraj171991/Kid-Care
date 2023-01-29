package com.child.parent.kidcare.views.applist;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.child.parent.kidcare.BaseActivity;
import com.child.parent.kidcare.R;
import com.child.parent.kidcare.utils.ItemDecoration;
import com.child.parent.kidcare.utils.TimePreference;
import com.google.android.material.snackbar.Snackbar;

public class SelectAppActivity extends BaseActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectapp_list);
        if(TimePreference.getAlarmStatus()) {
            findViewById(R.id.txtwarning).setVisibility(View.VISIBLE);
        }
        recyclerView =  findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new ItemDecoration());
        // Passing the column number 1 to show online one column in each row.
        recyclerViewLayoutManager = new GridLayoutManager(SelectAppActivity.this, 2);

        ProgressBar progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        AppListViewModel viewModel = ViewModelProviders.of(this).get(AppListViewModel.class);
        if(savedInstanceState == null) {
            viewModel.getAppList();
        }

        viewModel.getList().observe(this, packages -> {
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(new AppsAdapter(packages, new AppsAdapter.ClickListener(){
                @Override
                public void onLongPress(int position) {
                    viewModel.updateDB(position);
                }
            }));




        });
    }
}
