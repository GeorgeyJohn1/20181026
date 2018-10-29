package nyc.gj.com.a20180402_gj_nycschools;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import nyc.gj.com.a20180402_gj_nycschools.adapter.SchoolListAdapter;
import nyc.gj.com.a20180402_gj_nycschools.entities.SATDetails;
import nyc.gj.com.a20180402_gj_nycschools.entities.School;
import nyc.gj.com.a20180402_gj_nycschools.http.CheckConnectivity;
import nyc.gj.com.a20180402_gj_nycschools.http.HttpManager;
import nyc.gj.com.a20180402_gj_nycschools.utilities.JSONHelper;
import nyc.gj.com.a20180402_gj_nycschools.utilities.Keys;

public class DetailsActivity extends AppCompatActivity {

    private UI ui;
    private ProgressDialog pd;
    private School[] schoolObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent schoolDetailsIntent = getIntent();
        schoolObject = (School[]) JSONHelper.Deserialize(schoolDetailsIntent.getStringExtra(Keys._school_details), School[].class);
        ui = new UI();
        initialize();
        ui.tvWebSite.setText(schoolObject[0].getWebsite());
        ui.tvEmail.setText(schoolObject[0].getSchool_email());
        ui.tvPhoneNumber.setText(schoolObject[0].getPhone_number());
        if (CheckConnectivity.check(DetailsActivity.this)) {
            new SchoolResultAsyncTask().execute();
        }

        /*
        * To open Map to get this School location
        **/
        ui.ivSchoolDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:"+schoolObject[0].getLatitude()+","+schoolObject[0].getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
    }

    private void initialize() {
        ui.tvWebSite = findViewById(R.id.tv_school_website);
        ui.tvEmail = findViewById(R.id.tv_school_email);
        ui.tvPhoneNumber = findViewById(R.id.tv_school_phnum);
        ui.tvSATMath = findViewById(R.id.tv_sat_math);
        ui.tvSATReading = findViewById(R.id.tv_sat_reading);
        ui.tvSATTakers = findViewById(R.id.tv_sat_takers);
        ui.tvSATWriting = findViewById(R.id.tv_sat_writing);
        ui.tvSchoolName = findViewById(R.id.tv_school_name);
        ui.ivSchoolDirection=findViewById(R.id.iv_school_direction);
    }

    @SuppressLint("StaticFieldLeak")
    private class SchoolResultAsyncTask extends AsyncTask<Void, Void, String> {

        String response, URL;
        SATDetails[] responseSATDetails;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(DetailsActivity.this);
            pd.setMessage("Processing...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            URL = "734v-jeq5.json?DBN=" + schoolObject[0].getDbn();
            try {
                response = HttpManager.get(DetailsActivity.this, URL);
                if (response != null) {
                    if (!response.equals("[]\n")) {
                        responseSATDetails = (SATDetails[]) JSONHelper.Deserialize(response, SATDetails[].class);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(DetailsActivity.this, "Please try again. Error Occurred in server", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try {
                pd.dismiss();
                if (response != null) {
                    if (!response.equals("[]\n")) {
                        ui.tvSATWriting.setText(responseSATDetails[0].getSat_writing_avg_score());
                        ui.tvSATReading.setText(responseSATDetails[0].getSat_critical_reading_avg_score());
                        ui.tvSATTakers.setText(responseSATDetails[0].getNum_of_sat_test_takers());
                        ui.tvSATMath.setText(responseSATDetails[0].getSat_math_avg_score());
                        ui.tvSchoolName.setText(responseSATDetails[0].getSchool_name());

                    }
                    else {
                        Toast.makeText(DetailsActivity.this, "No Score to Display", Toast.LENGTH_LONG).show();

                    }
                }

            } catch (Exception e) {
                Toast.makeText(DetailsActivity.this, "Please try again. Error Occurred in server", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }


    class UI {
        TextView tvWebSite, tvEmail, tvPhoneNumber, tvSATTakers, tvSATMath, tvSATReading, tvSATWriting, tvSchoolName;
        ImageView ivSchoolDirection;
    }
}
