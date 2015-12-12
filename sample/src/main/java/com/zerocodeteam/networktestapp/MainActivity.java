package com.zerocodeteam.networktestapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zerocodeteam.network.ZctNetwork;
import com.zerocodeteam.network.response.ResponseListener;

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                API.pingServer(new ResponseListener<Object>() {
                    @Override
                    public void onResponseSuccess(Object cookie, Object createdObject, Map<String, String> responseHeaders) {

                        /*Snackbar.make(view, createdObject.toString(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();*/
                        mTextView.append("\nS: " + createdObject.toString());
                    }

                    @Override
                    public void onErrorResponse(ZctNetwork.ErrorType type, Object cookie, VolleyError error, Map<String, String> responseHeaders) {
                        /*Snackbar.make(view, error.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();*/
                        String errorMsg = "UNKNOWN";
                        switch (type) {
                            case TIMEOUT:
                                errorMsg = "TIMEOUT";
                                break;
                            case AUTH_FAILURE:
                                errorMsg = "AUTH_FAILURE";
                                break;
                            case SERVER_ERROR:
                                errorMsg = "SERVER_ERROR";
                                break;
                            case NETWORK_ERROR:
                                errorMsg = "NETWORK_ERROR";
                                break;
                            case PARSE_ERROR:
                                errorMsg = "PARSE_ERROR";
                                break;
                        }
                        mTextView.append("\nE: [TYPE: " + errorMsg + " code:" + (error.networkResponse != null ? error.networkResponse.statusCode : "0") + ":" + error.getNetworkTimeMs() + "ms] " + error.getMessage());
                    }
                });
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
