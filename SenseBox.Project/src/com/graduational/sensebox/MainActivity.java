package com.graduational.sensebox;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import com.jjoe64.graphview.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class MainActivity extends Activity {
   // Activity activity = this;
    JSON_resolver resolver = null;
    ArrayList<Date> date;
    ArrayList<String> temperature;
    Activity activity;
    GraphView graphView;
    GraphViewSeries graphSeries;
    private JSONObject jObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_layout);

        AsyncTask<String, Void, Void> db = null;
        try {
            db = new db_conn(this);
            db.execute("http://192.168.1.5/request.php").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        try {
            resolver = new JSON_resolver();
            resolver.setjObject(jObject);
            resolver.resolve();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //   textView.setText(data);
        if (resolver != null) {
            date = resolver.getDate();
            temperature = resolver.getTemp();
        }

        GraphView.GraphViewData[] data = new GraphView.GraphViewData[date.size()];
        for (int i = 0; i < date.size(); i++) {
            data[i] = new GraphView.GraphViewData(5.1, Double.parseDouble(temperature.get(i)));
        }


        for (int i = 0; i < data.length; i++) {
            long now = date.get(i).getTime();
            System.out.println(now);
            data[i] = new GraphView.GraphViewData(now , Double.parseDouble(temperature.get(i)));
           // System.out.println(now + (i * 60 * 60 * 24 * 1000));
        }

        graphSeries = new GraphViewSeries(data);
        graphView = new LineGraphView(this, "Demo");
        ((LineGraphView) graphView).setDrawBackground(true);

        graphView.addSeries(graphSeries);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d k:m:s ");
        graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Date d = new Date((long) value);
                    return dateFormat.format(d);
                }
                return null; // let graphview generate Y-axis label for us
            }
        });

        LinearLayout layout = (LinearLayout) findViewById(R.id.graph2);
        layout.addView(graphView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    public void getGraphActivity(View view) {
//        Intent intent = new Intent(this, Graphs.class);
//        startActivity(intent);
//    }


    class db_conn extends AsyncTask<String, Void, Void> {
        Activity activity2;
        ProgressDialog dialog;
        List<Message> titles;
        private Context context;
        public db_conn(Activity activity) {
            this.activity2 = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }

        /** progress dialog to show user that the backup is processing. */


        @Override
        protected Void doInBackground(String... params) {
            GetDataFromDB db = new GetDataFromDB();
            String url = params[0];
            System.out.println(url);
            jObject = db.makeHttpRequest(url, "GET", null);
         //   Graphs graph = new Graphs(resolver, activity);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            myHandler.sendEmptyMessage(0);
        }

        Handler myHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        // calling to this function from other pleaces
                        // The notice call method of doing things
                        break;
                    default:
                        break;
                }
            }
        };
    }

    }


