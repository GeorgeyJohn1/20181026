package nyc.gj.com.a20180402_gj_nycschools.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nyc.gj.com.a20180402_gj_nycschools.entities.School;
import nyc.gj.com.a20180402_gj_nycschools.R;


public class SchoolListAdapter extends ArrayAdapter<School> {

    private Context mContext;
    private String borough;

    /*
    * This class is Adapter for the List View,
    * I AM NOT USING View Holder, it will cause empty space if a row need to hide
     */

    public SchoolListAdapter(@NonNull Context context, int resource, @NonNull List<School> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        School mSchool = getItem(position);

        if (mSchool == null) {
            return convertView;
        }
/*
* To set the view and value
**/
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_school, parent, false);
        TextView tvSchoolLocation = convertView.findViewById(R.id.tv_school_location);
        TextView tvSchoolName = convertView.findViewById(R.id.tv_school_name);

        try {
            if (mSchool.getBorough().equals(borough)) {
                tvSchoolLocation.setText(mSchool.getLocation().split("\\(")[0]);
                tvSchoolName.setText(mSchool.getSchool_name());
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_blank_layout, parent, false);

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }

    /*
    * To set the borough value from the Spinner
    */
    public void filterBorough(String boro){
        borough = boro;
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public School getItem(int position) {
        return super.getItem(position);
    }

}
