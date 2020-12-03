package com.dalakoti07.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SecondFragment extends Fragment {
    private TextView txtName;
    public SecondFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of this fragment.
     * @return A new instance of fragment SecondFragment.
     */
    public static SecondFragment newInstance() {
        return new SecondFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtName = view.findViewById(R.id.textViewName);
        //  listen RxJava event here
        RxBus.getInstance().listen().subscribe(getInputObserver());
    }
    // Get input observer instance
    private Observer<String> getInputObserver() {
        return new Observer<String>() {
            @Override public void onSubscribe(Disposable d) {
            }
            @Override public void onNext(String s) {
                txtName.setText(s);
            }
            @Override public void onError(Throwable e) {
            }
            @Override public void onComplete() {
            }
        };
    }
}