package com.example.abhishekmodi.rxandroidsample;

import android.location.Address;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    private Disposable disposable;
    private Button flatMap,concatMap,switchMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flatMap=(Button) findViewById(R.id.flatmap);
        concatMap=(Button) findViewById(R.id.concatMap);
        switchMap=(Button) findViewById(R.id.switchmap);
        flatMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flatMapExapple();
            }
        });

        concatMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                concatMapExample();
            }
        });

        switchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMap();
            }
        });

    }

    private void concatMapExample()
    {
        getUsersObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(new Function<User, Observable<User>>() {

                    @Override
                    public Observable<User> apply(User user) throws Exception {

                        // getting each user address by making another network call
                        return getAddressObservable(user);
                    }
                })
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("MainActivity", "onSubscribe");
                        disposable = d;
                    }

                    @Override
                    public void onNext(User user) {
                        Log.e("MainActivity", "onNext: " + user.getName() + ", " + user.getGender() + ", " + user.getAddress());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.e("MainActivity", "All users emitted!");
                    }
                });
    }


    private void flatMapExapple()
    {
        getUsersObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<User, Observable<User>>() {

                    @Override
                    public Observable<User> apply(User user) throws Exception {

                        // getting each user address by making another network call
                        return getAddressObservable(user);
                    }
                })
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("MainActivity", "onSubscribe");
                        disposable = d;
                    }

                    @Override
                    public void onNext(User user) {
                        Log.e("Main", "onNext: " + user.getName() + ", " + user.getGender() + ", " + user.getAddress());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.e("Main", "All users emitted!");
                    }
                });
    }
    /**
     * Assume this as a network call
     * returns Users with address filed added
     */
    private Observable<User> getAddressObservable(final User user) {

        final String[] addresses = new String[]{
                "1600 Amphitheatre Parkway, Mountain View, CA 94043",
                "2300 Traverwood Dr. Ann Arbor, MI 48105",
                "500 W 2nd St Suite 2900 Austin, TX 78701",
                "355 Main Street Cambridge, MA 02142"
        };

        return Observable
                .create(new ObservableOnSubscribe<User>() {
                    @Override
                    public void subscribe(ObservableEmitter<User> emitter) throws Exception {


                        if (!emitter.isDisposed()) {
                            user.setAddress(addresses[new Random().nextInt(2) + 0]);
                            // Generate network latency of random duration
                            int sleepTime = new Random().nextInt(1000) + 500;

                            Thread.sleep(sleepTime);
                            emitter.onNext(user);
                            emitter.onComplete();
                        }
                    }
                }).subscribeOn(Schedulers.io());
    }

    /**
     * Assume this is a network call to fetch users
     * returns Users with name and gender but missing address
     */
    private Observable<User> getUsersObservable() {
        String[] maleUsers = new String[]{"Mark", "John", "Trump", "Obama"};

        final List<User> users = new ArrayList<>();

        for (String name : maleUsers) {
            User user = new User();
            user.setName(name);
            user.setGender("male");

            users.add(user);
        }

        return Observable
                .create(new ObservableOnSubscribe<User>() {
                    @Override
                    public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                        for (User user : users) {
                            if (!emitter.isDisposed()) {
                                emitter.onNext(user);
                            }
                        }

                        if (!emitter.isDisposed()) {
                            emitter.onComplete();
                        }
                    }
                }).subscribeOn(Schedulers.io());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    //SwitchMap is best suited when you want
    // to discard the response and consider the latest one. Let’s say you are writing an Instant
    // Search app which sends search query to server each time user types something.
    // In this case multiple requests will be sent to server with multiple queries,
    // but we want to show the result of latest typed query only. For this case,
    // SwitchMap is best operator to use.
    private void switchMap()
    {
        Observable<Integer> integerObservable =
                Observable.fromArray(new Integer[]{1, 2, 3, 4, 5, 6});


        // it always emits 6 as it un-subscribes the before observer
        integerObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .switchMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer integer) throws Exception {
                        return Observable.just(integer).delay(1, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("MainActivity", "onSubscribe");
                        disposable = d;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d("MainActivity", "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("MainActivity", "All users emitted!");
                    }
                });
    }
}