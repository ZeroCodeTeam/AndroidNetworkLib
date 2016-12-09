package com.zerocodeteam.networktestapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.zerocodeteam.network.ZctNetwork;
import com.zerocodeteam.network.ZctResponse;

import java.util.Map;

/**
 * Created by ZeroCodeTeam on 05/12/15.
 */
public class MainActivity extends AppCompatActivity {

    private final String IMAGE_0_URL = "http://www.wallpapersonview.com/wallpapers/2/animals_small_turtle-0075.jpg";
    //    private final String IMAGE_1_URL = "http://www.akjsdhjkashdjkh.jpg";
    private final String IMAGE_1_URL = "http://vineland.pynchonwiki.com/wiki/images/c/cf/Godzilla.jpg";

    private TextView mTextView;
    private NetworkImageView mINetworkImageView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTextView = (TextView) findViewById(R.id.logMsg);
        mINetworkImageView = (NetworkImageView) findViewById(R.id.networkImage);
        mImageView = (ImageView) findViewById(R.id.networkImageWithLoading);
        if (ZctNetwork.with(this.getApplicationContext()).isDeviceOnline().equals(ZctNetwork.NetworkType.NO_NETWORK)) {
            Toast.makeText(getApplicationContext(), "DEVICE OFFLINE", Toast.LENGTH_LONG).show();
        } else {
            ZctNetwork.with(getApplicationContext()).loadNetworkImage(IMAGE_0_URL, mINetworkImageView);
            ZctNetwork.with(getApplicationContext()).loadImage(IMAGE_1_URL, mImageView);
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
            mTextView.append("\n Stat: " + ZctNetwork.with(getApplicationContext()).
                    getNetworkStats(ZctNetwork.NetworkStats.WIFI_TX) + " bytes");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
