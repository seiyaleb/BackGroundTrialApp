package com.backgroundapp;

import android.content.Intent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button bt_start = view.findViewById(R.id.bt_start);
        Button bt_stop = view.findViewById(R.id.bt_stop);

        //startボタン選択時
        bt_start.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                //サービス起動
                //Intent intent = new Intent(getActivity(),MainService.class);
                //getActivity().startService(intent);

                //15分おきに定期実行を開始
                //Wi-Fiに繋がっている、かつ電池残量低下モードになっていない場合のみ
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .setRequiresBatteryNotLow(true)
                        .build();
                PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                        NotificationWorker.class, Duration.ofMinutes(15)
                )
                        .setConstraints(constraints)
                        .addTag("notification")
                        .build();
                WorkManager.getInstance(getActivity()).enqueue(workRequest);

                //ボタンの選択変更
                bt_start.setEnabled(false);
                bt_stop.setEnabled(true);
            }
        });

        //stopボタン選択時
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //サービス停止
                //Intent intent = new Intent(getActivity(),MainService.class);
                //getActivity().stopService(intent);

                //定期実行をキャンセル
                WorkManager.getInstance(getActivity()).cancelAllWorkByTag("notification");

                //ボタンの選択変更
                bt_start.setEnabled(true);
                bt_stop.setEnabled(false);
            }
        });

        //通知からの起動の場合
        Intent intent = getActivity().getIntent();
        boolean fromNotification = intent.getBooleanExtra("fromNotification",false);
        if(fromNotification) {

            //ボタンの選択変更
            bt_start.setEnabled(false);
            bt_stop.setEnabled(true);
        }
    }
}