package com.bytegeist.enigmatestapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bytegeist.enigmatestapp.data.TestDataFetcher;
import com.bytegeist.enigmatestapp.enigma.Enigma;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Enigma";
    private ProgressDialog mProgressDialog;
    private TextView mMessage;
    private EditText mCustomMessage;
    private Button mRunButton;
    private Button mResetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mMessage = (TextView) findViewById(R.id.message);
        mCustomMessage = (EditText) findViewById(R.id.custom_input);
        setSupportActionBar(toolbar);

        mRunButton = (Button) findViewById(R.id.run);
        mResetButton = (Button) findViewById(R.id.reset);

        mRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (mCustomMessage.getText().toString().length() != 0) {
                    encodeMessage(mCustomMessage.getText().toString());
                } else {
                    fetchDataThenEncode();
                }

            }
        });
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Enigma.getInstance().reset();
                Toast.makeText(MainActivity.this, "Enigma rotors reset", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDataThenEncode() {
        mProgressDialog = ProgressDialog.show(MainActivity.this, "Loading", "Fetching random data...", true);
        Call<ResponseBody> testData = TestDataFetcher.createClient().getTestData();
        testData.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (mProgressDialog != null) {
                    mProgressDialog.hide();
                }
                try {
                    encodeMessage(response.body().string());

                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (mProgressDialog != null) {
                    mProgressDialog.hide();
                }
                Log.e(TAG, t.getMessage(), t);
            }
        });
    }

    private void encodeMessage(final String message) {
        mProgressDialog = ProgressDialog.show(MainActivity.this, "Loading", "Encoding fetched data...", true);
        Enigma.getInstance().encrypt(message).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mProgressDialog.hide();
                        mMessage.setText("Message:\n" + message + "\n\nEncrypted message:\n" + s);
                    }
                });
    }

}
