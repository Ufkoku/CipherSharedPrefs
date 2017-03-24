package com.ufkoku.ciphersharedpreferences.demo.ui;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ufkoku.ciphersharedpreferences.R;
import com.ufkoku.ciphersharedpreferences.demo.entity.PrefsEntry;
import com.ufkoku.mvp.retainable.BaseRetainableActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends BaseRetainableActivity<IMainActivity, MainPresenter, MainViewState> implements IMainActivity, Adapter.Listener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private RecyclerView recyclerView;
    private Adapter adapter;

    @Override
    public void createView() {
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new Adapter(getLayoutInflater());
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabAdd).setOnClickListener(fab -> {
            MainPresenter presenter = getPresenter();
            if (presenter != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.add_value_pair);

                View view = getLayoutInflater().inflate(R.layout.dialog_add_entry_layout, null, false);
                EditText etKey = (EditText) view.findViewById(R.id.key);
                EditText etValue = (EditText) view.findViewById(R.id.value);
                builder.setView(view);

                builder.setPositiveButton(R.string.add, null);

                AlertDialog dialog = builder.create();
                dialog.show();

                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String key = etKey.getText().toString();
                    if (!key.isEmpty()) {
                        if (presenter.isKeyUnique(key)) {
                            presenter.addValue(key, etValue.getText().toString());
                            dialog.dismiss();
                        } else {
                            Toast.makeText(this, R.string.key_exists, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, R.string.empty_key, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @NotNull
    @Override
    public IMainActivity getMvpView() {
        return this;
    }

    @NotNull
    @Override
    public MainViewState createNewViewState() {
        return new MainViewState();
    }

    @NotNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    public void onInitialized(MainPresenter presenter, MainViewState viewState) {
        if (!presenter.isInitialized()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.enter_key);
            builder.setMessage(R.string.key_requirments_message);

            EditText editText = new EditText(this);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
            builder.setView(editText);

            builder.setPositiveButton(R.string.apply, null);

            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                if (editText.getText().length() == 16) {
                    String key = editText.getText().toString();
                    presenter.initializeWithKey(key);
                    presenter.getValues();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, R.string.bad_key, Toast.LENGTH_LONG).show();
                }
            });

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) editText.getLayoutParams();
            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());
            layoutParams.setMargins(margin, margin, margin, margin);
            editText.requestLayout();
        } else {
            if (!viewState.isPrefsLoaded() && !presenter.isTaskRunning(MainPresenter.TASK_GET_RUNNING)) {
                presenter.getValues();
            }
        }
    }

    @Override
    public void onEntryLoaded(PrefsEntry entry) {
        MainViewState state = getViewState();
        if (state != null) {
            state.getEntries().add(entry);
        }
        recyclerView.post(() -> adapter.addEntry(entry));
    }

    @Override
    public void populateEntries(List<PrefsEntry> entries) {
        recyclerView.post(() -> adapter.addEntries(entries));
    }

    @Override
    public void onDeleteClicked(PrefsEntry entry) {
        recyclerView.post(() -> adapter.removeEntry(entry));
        MainViewState state = getViewState();
        if (state != null){
            state.getEntries().remove(entry);
        }
        MainPresenter presenter = getPresenter();
        if (presenter != null){
            presenter.removeValue(entry);
        }
    }
}
