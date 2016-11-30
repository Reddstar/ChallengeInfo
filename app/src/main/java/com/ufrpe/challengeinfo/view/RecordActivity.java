package com.ufrpe.challengeinfo.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ufrpe.challengeinfo.R;
import com.ufrpe.challengeinfo.controller.ResultFormat;
import com.ufrpe.challengeinfo.model.PostResult;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class RecordActivity extends AppCompatActivity {

    private ArrayList<String> months = new ArrayList();
    private ArrayList<String> years = new ArrayList<>();
    private Spinner spinnerYears;
    private Spinner spinnerMonths;
    private Button button;

    private Integer selectedMonth;
    private Integer selectedYear;
    private String builtURL;
    boolean yearChoosen = false;
    boolean monthChoosen = false;
    private ListView listView;
    private static ArrayList<ResultFormat> records;
    private ArrayList<String> results;

    @Override
    public void onBackPressed() {
        finish();
        Intent intentBackMain = new Intent(RecordActivity.this, MainScreenActivity.class);
        startActivity(intentBackMain);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        final String[] months = {"Month",
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"};
        final String[] years = {"Year", "2016", "2015", "2014", "2013", "2012", "2011", "2010"};

        this.months = new ArrayList(Arrays.asList(months));
        this.years = new ArrayList(Arrays.asList(years));
        spinnerMonths = (Spinner) findViewById(R.id.spinner_month);
        spinnerYears = (Spinner) findViewById(R.id.spinner_year);
        button = (Button) findViewById(R.id.btn_ok);
        spinnerYears.setAdapter(new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, years));
        spinnerMonths.setAdapter(new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, months));
        listView = (ListView) findViewById(R.id.list_record);
        records = new ArrayList();
        results = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Top3ViewActivity.setResult(results.get(position));
                finish();
                Intent intentGoTop3 = new Intent(RecordActivity.this, Top3ViewActivity.class);
                startActivity(intentGoTop3);
            }
        });

        spinnerYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    yearChoosen = true;
                    selectedYear = Integer.parseInt(years[position]);
                } else {
                    yearChoosen = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                yearChoosen = false;
            }
        });
        spinnerMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    monthChoosen = true;
                    selectedMonth = position;
                } else {
                    monthChoosen = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                monthChoosen = false;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monthChoosen == false || yearChoosen == false) {
                    Toast.makeText(RecordActivity.this, "Select filter", Toast.LENGTH_LONG).show();
                } else {
                    builtURL = "http://" + MainScreenActivity.serverIP + ":5000/record/";
                    builtURL = builtURL + checkSelectedMonth(selectedMonth.toString()) + "/";
                    builtURL = builtURL + selectedYear.toString();
                    String result = communicate();
                    /*String result = "(24, datetime.datetime(2016, 3, 3, 10, 2, 43), " +
                            "'999', 'Joao Li', '150', '14', '872', " +
                            "'Pedro Couto', '100', '90', '721', " +
                            "'Maria Helena', '50', '4')#" +
                            "(25, datetime.datetime(2016, 3, 4, 1, 20, 11), " +
                            "'1', 'teste', '1', '1', '2', " +
                            "'teste2', '2', '2', '3', " +
                            "'teste3', '3', '3')#" +
                            "(26, datetime.datetime(2016, 3, 1, 1, 1, 1)," +
                            " '1', 'oi', '1', '1', '-', '-', '-', '-', '-', '-', '-', '-')#";*/
                    String[] parts = result.split("#");
                    for (int x = 0; x < parts.length - 1; x++) {
                        ResultFormat resultFormat = new ResultFormat();
                        resultFormat.format(parts[x]);
                        results.add(parts[x]);
                        records.add(resultFormat);
                    }
                    populate();

                }
            }
        });

    }
    public void restart(){
        finish();
        Intent restart = new Intent(RecordActivity.this, RecordActivity.class);
        startActivity(restart);
    }
    public String checkSelectedMonth(String text) {
        if (text.length() == 1) {
            text = "0" + text;
        }
        return text;
    }
    public void populate(){
        ArrayAdapter<PostResult> adapter = new PostListAdapter();
        listView.setAdapter(adapter);

    }
    public String communicate() {
        final RecordActivity.RecordsGetter recordsGetter = new RecordActivity.RecordsGetter();
        recordsGetter.execute();
        try {
            recordsGetter.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return recordsGetter.result;
    }

    public class PostListAdapter extends ArrayAdapter {

        public PostListAdapter() {
            super(RecordActivity.this, R.layout.list_top_post_item, records);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_record_item, parent, false);
            }
            TextView txtTime = (TextView) convertView.findViewById(R.id.txt_record_list_item);
            ResultFormat result = records.get(position);
            txtTime.setText(result.getTimestamp());
            return convertView;
        }

    }
    public class RecordsGetter extends AsyncTask<String, Void, String> {

        public String result;

        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(builtURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            this.result = result.toString();
            return result.toString();
        }

    }
}
