package nyc.gj.com.a20180402_gj_nycschools;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import nyc.gj.com.a20180402_gj_nycschools.adapter.SchoolListAdapter;
import nyc.gj.com.a20180402_gj_nycschools.entities.School;
import nyc.gj.com.a20180402_gj_nycschools.http.CheckConnectivity;
import nyc.gj.com.a20180402_gj_nycschools.http.HttpManager;
import nyc.gj.com.a20180402_gj_nycschools.utilities.JSONHelper;
import nyc.gj.com.a20180402_gj_nycschools.utilities.Keys;

public class HomeActivity extends AppCompatActivity {

    private ProgressDialog pd;
    private UI ui;
    private HashSet<String> borough;
    private SchoolListAdapter schoolListAdapter;
    private ArrayList<School> schoolArrayList;
    private ArrayAdapter<String> schoolAdapter;
    private List<String> listBorough;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ui = new UI();
        initialize();
        //Calling the API for
        if (CheckConnectivity.check(HomeActivity.this)) {
            new SchoolListAsyncTask().execute();
        }

        ui.spnBorough.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                schoolListAdapter.filterBorough(ui.spnBorough.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ui.lvSchool.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent schoolDetailsIntent = new Intent(HomeActivity.this, DetailsActivity.class);
                schoolDetailsIntent.putExtra(Keys._school_details, schoolListAdapter.getItem(position).toString());
                startActivity(schoolDetailsIntent);
            }
        });


    }

    private void initialize() {
        ui.spnBorough = findViewById(R.id.spn_borough);
        ui.lvSchool = findViewById(R.id.lv_school);
    }


    @SuppressLint("StaticFieldLeak")
    private class SchoolListAsyncTask extends AsyncTask<Void, Void, String> {

        String response, URL;
        School[] responseSchool;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(HomeActivity.this);
            pd.setMessage("Processing...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            URL = "97mf-9njv.json";
            try {
                response = HttpManager.get(HomeActivity.this, URL);
                if (response != null) {
                    responseSchool = (School[]) JSONHelper.Deserialize(response, School[].class);
                }
            } catch (Exception e) {
                Toast.makeText(HomeActivity.this, "Please try again. Error Occurred in server", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                pd.dismiss();
                borough = new HashSet<>();
                if (responseSchool != null) {
                    schoolArrayList = new ArrayList<>();
                    for (int i = 0; i < responseSchool.length; i++) {
                        if (responseSchool[i].getBorough() != null) {
                            borough.add(responseSchool[i].getBorough());
                        }
                        schoolArrayList.add(responseSchool[i]);
                    }
                    /*
                    * Setting the Spinner and List View Value from the Response
                    **/

                    listBorough = new ArrayList<>(borough);
                    schoolAdapter = new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_spinner_dropdown_item, listBorough);
                    schoolListAdapter = new SchoolListAdapter(HomeActivity.this, R.layout.item_school, schoolArrayList);
                    ui.spnBorough.setAdapter(schoolAdapter);
                    ui.lvSchool.setAdapter(schoolListAdapter);
                    schoolListAdapter.filterBorough(ui.spnBorough.getSelectedItem().toString());
                }
            } catch (Exception e) {
                Toast.makeText(HomeActivity.this, "Please try again. Error Occurred in server", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    class UI {
        Spinner spnBorough;
        ListView lvSchool;
    }

}
