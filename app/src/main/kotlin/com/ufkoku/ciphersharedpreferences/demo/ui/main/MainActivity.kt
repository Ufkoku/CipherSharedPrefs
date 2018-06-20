package com.ufkoku.ciphersharedpreferences.demo.ui.main

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.text.InputFilter
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast

import com.ufkoku.ciphersharedpreferences.R
import com.ufkoku.ciphersharedpreferences.demo.entity.PrefsEntry
import com.ufkoku.ciphersharedpreferences.demo.ui.main.di.DaggerMainActivityComponent
import com.ufkoku.ciphersharedpreferences.demo.ui.main.di.MainAcivityModule
import com.ufkoku.ciphersharedpreferences.demo.ui.main.di.MainActivityComponent
import com.ufkoku.mvp.BaseMvpActivity

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseMvpActivity<IMainActivity, MainPresenter, MainViewState>(), IMainActivity, Adapter.Listener {

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    private lateinit var adapter: Adapter

    private lateinit var mainActivityComponent: MainActivityComponent

    override fun retainPresenter(): Boolean {
        return true
    }

    override fun retainViewState(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mainActivityComponent = DaggerMainActivityComponent.builder()
                .mainAcivityModule(MainAcivityModule())
                .build()

        super.onCreate(savedInstanceState)
    }

    override fun createView() {
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = Adapter(layoutInflater)
        adapter.setListener(this)
        recyclerView!!.adapter = adapter

        findViewById<View>(R.id.fabAdd).setOnClickListener { fab -> onAddClicked() }
    }

    override fun getMvpView(): IMainActivity {
        return this
    }

    override fun createViewState(): MainViewState {
        return mainActivityComponent.mainViewState
    }

    override fun createPresenter(): MainPresenter {
        return mainActivityComponent.mainPresenter
    }

    override fun onInitialized(presenter: MainPresenter, viewState: MainViewState) {
        if (!presenter.isInitialized) {
            if (viewState.key != null) {
                presenter.initializeWithKey(viewState.key!!)
                presenter.getValues()
            } else {
                onShowEnterKeyDialog()
            }
        } else {
            if (!viewState.arePrefsLoaded && !presenter.isTaskRunning(MainPresenter.TASK_GET_RUNNING)) {
                presenter.getValues()
            }
        }
    }

    override fun onEntriesFetched(entry: List<PrefsEntry>) {
        val state = viewState
        if (state != null) {
            state.entries.addAll(entry)
            state.arePrefsLoaded = true
        }
        recyclerView!!.post { adapter.addEntries(entry) }
    }

    override fun onEntryCreated(entry: PrefsEntry) {
        val state = viewState
        state?.entries?.add(entry)
        recyclerView!!.post { adapter.addEntry(entry) }
    }

    override fun populateEntries(entries: List<PrefsEntry>) {
        recyclerView!!.post { adapter.addEntries(entries) }
    }

    override fun onDeleteClicked(entry: PrefsEntry) {
        recyclerView!!.post { adapter.removeEntry(entry) }

        val state = viewState
        state?.entries?.remove(entry)

        val presenter = presenter
        presenter?.removeValue(entry)
    }

    override fun onTaskStatusChanged(taskId: Int, status: Int) {
        //do nothing
    }

    private fun onAddClicked() {
        val presenter = presenter
        if (presenter != null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.add_value_pair)

            val view = layoutInflater.inflate(R.layout.dialog_add_entry_layout, null, false)
            val etKey = view.findViewById<EditText>(R.id.key)
            val etValue = view.findViewById<EditText>(R.id.value)
            builder.setView(view)

            builder.setPositiveButton(R.string.add, null)

            val dialog = builder.create()
            dialog.show()

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener { v ->
                val key = etKey.text.toString()
                if (!key.isEmpty()) {
                    if (presenter.isKeyUnique(key)) {
                        presenter.addValue(key, etValue.text.toString())
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, R.string.key_exists, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, R.string.empty_key, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun onShowEnterKeyDialog() {
        val presenter = presenter
        val viewState = viewState

        if (presenter != null && viewState != null) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.enter_key)
            builder.setMessage(R.string.key_requirments_message)

            val editText = EditText(this)
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(16))
            builder.setView(editText)

            builder.setPositiveButton(R.string.apply, null)

            val dialog = builder.create()
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener { v ->
                if (editText.text.length == 16) {
                    val key = editText.text.toString()
                    viewState.key = key
                    presenter.initializeWithKey(key)
                    presenter.getValues()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, R.string.bad_key, Toast.LENGTH_LONG).show()
                }
            }

            val layoutParams = editText.layoutParams as FrameLayout.LayoutParams
            val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt()
            layoutParams.setMargins(margin, margin, margin, margin)
            editText.requestLayout()
        }

    }

}
