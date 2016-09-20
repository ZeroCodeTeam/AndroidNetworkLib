package com.zerocodeteam.networktestapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zerocodeteam.network.ZctNetwork;
import com.zerocodeteam.network.ZctResponse;

import java.util.Map;

/**
 * Created by ZeroCodeTeam on 05/12/15.
 */
public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTextView = (TextView) findViewById(R.id.logMsg);

        if (ZctNetwork.isDeviceOnline(this.getApplicationContext()).equals(ZctNetwork.NetworkType.NO_NETWORK)) {
            Toast.makeText(getApplicationContext(), "DEVICE OFFLINE", Toast.LENGTH_LONG).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                // Send string request
                API.echoCallString(new ZctResponse<String>() {

                    @Override
                    public void onSuccess(String responseObject, Map<String, String> responseHeaders, Object cookie) {
                        mTextView.append("\nS: " + responseObject);
                    }

                    @Override
                    public void onError(VolleyError error, ZctNetwork.ErrorType type, Map<String, String> responseHeaders, Object cookie) {
                        mTextView.append("\nError response:\ntype: " + type + "\ncode: " + (error.networkResponse != null ? error.networkResponse.statusCode : "0") + "\nduration: " + error.getNetworkTimeMs() + "ms\n" + error.getMessage());
                    }
                }, MainActivity.this);

                // Send gson request
                API.echoCallGson(new ZctResponse<Example>() {

                    @Override
                    public void onSuccess(Example responseObject, Map<String, String> responseHeaders, Object cookie) {
                        mTextView.append("\nS: " + responseObject);
                    }

                    @Override
                    public void onError(VolleyError error, ZctNetwork.ErrorType type, Map<String, String> responseHeaders, Object cookie) {
                        mTextView.append("\nError response:\ntype: " + type + "\ncode: " + (error.networkResponse != null ? error.networkResponse.statusCode : "0") + "\nduration: " + error.getNetworkTimeMs() + "ms\n" + error.getMessage());
                    }
                }, MainActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
