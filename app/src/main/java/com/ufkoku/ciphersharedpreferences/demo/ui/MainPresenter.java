package com.ufkoku.ciphersharedpreferences.demo.ui;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.ufkoku.cipher_sharedprefs.CipherSharedPreferences;
import com.ufkoku.ciphersharedpreferences.demo.DemoApp;
import com.ufkoku.ciphersharedpreferences.demo.entity.PrefsEntry;
import com.ufkoku.mvp.presenter.rx.BaseAsyncRxPresenter;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

@SuppressLint("ApplySharedPref")
public class MainPresenter extends BaseAsyncRxPresenter<IMainActivity> {

    public static final int TASK_GET_RUNNING = 0;
    public static final int TASK_ADD_RUNNING = 1;

    @Inject
    protected SharedPreferences sharedPreferences;

    @Inject
    protected CipherSharedPreferences cipherSharedPreferences;

    public MainPresenter() {
        if (DemoApp.getSharedPrefsComponent() != null) {
            DemoApp.getSharedPrefsComponent().inject(this);
        }
    }

    @NotNull
    @Override
    protected ExecutorService createExecutor() {
        return new ScheduledThreadPoolExecutor(1);
    }

    public boolean isInitialized() {
        return DemoApp.getSharedPrefsComponent() != null;
    }

    public void initializeWithKey(String key) {
        try {
            DemoApp.initializeWithKey(key.getBytes("latin-1"));
            DemoApp.getSharedPrefsComponent().inject(this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void getValues() {
        notifyTaskAdded(TASK_GET_RUNNING);
        Observable.defer(() -> Observable.from(cipherSharedPreferences.getAll().entrySet()))

                .map(entry -> new PrefsEntry(entry.getKey(), entry.getValue(), sharedPreferences.getString(entry.getKey(), null)))
                .doOnNext(prefsEntry -> waitForViewIfNeeded())

                .subscribeOn(getScheduler())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Subscriber<PrefsEntry>() {
                    @Override
                    public void onCompleted() {
                        notifyTaskFinished(TASK_GET_RUNNING);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(PrefsEntry prefsEntry) {
                        IMainActivity activity = getView();
                        if (activity != null) {
                            activity.onEntryLoaded(prefsEntry);
                        }
                    }
                });
    }

    public void addValue(String key, String value) {
        notifyTaskAdded(TASK_ADD_RUNNING);
        Observable.create((Observable.OnSubscribe<PrefsEntry>) subscriber -> {
            cipherSharedPreferences.edit().putString(key, value).commit();
            subscriber.onNext(new PrefsEntry(
                    key, value, sharedPreferences.getString(key, null)
            ));
            subscriber.onCompleted();
        })

                .doOnNext(prefsEntry -> waitForViewIfNeeded())

                .subscribeOn(getScheduler())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Subscriber<PrefsEntry>() {
                    @Override
                    public void onCompleted() {
                        notifyTaskAdded(TASK_GET_RUNNING);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(PrefsEntry prefsEntry) {
                        IMainActivity activity = getView();
                        if (activity != null) {
                            activity.onEntryLoaded(prefsEntry);
                        }
                    }
                });
    }

    public void removeValue(PrefsEntry entry) {
        Observable.create((Observable.OnSubscribe<PrefsEntry>) subscriber -> {
            cipherSharedPreferences.edit().remove(entry.getKey()).commit();
            subscriber.onNext(entry);
            subscriber.onCompleted();
        })

                .subscribeOn(getScheduler())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe();
    }

    public boolean isKeyUnique(String key) {
        return !cipherSharedPreferences.contains(key);
    }

}
