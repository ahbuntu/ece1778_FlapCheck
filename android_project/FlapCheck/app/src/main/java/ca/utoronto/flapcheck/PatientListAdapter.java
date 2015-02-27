package ca.utoronto.flapcheck;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by ahmadul.hassan on 2015-02-21.
 */
public class PatientListAdapter extends ArrayAdapter<Patient> {
    private static final String TAG = "PatientEntryAdapter";
    Context mContext;
    int layoutResourceId;
    List<Patient> patients = null;

    /**
     * constructor for the adapter
     * @param context - context for which it will be displayed
     * @param layoutResId - the layout to which the adapter will be bound
     * @param list - the list of patients
     */
    public PatientListAdapter(Context context, int layoutResId, List<Patient> list) {
        super(context, layoutResId, list);
        mContext = context;
        layoutResourceId = layoutResId;
        patients = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        TextView textViewName  = (TextView) convertView.findViewById(R.id.text_PE_arch_name);
        TextView textViewMRN = (TextView) convertView.findViewById(R.id.text_PE_arch_mrn);
        TextView textViewOpDateTime  = (TextView) convertView.findViewById(R.id.text_PE_arch_opDateTime);

        // object item based on the position
//        Log.d(TAG, "position of the view to inflate: " + position);
        if (patients == null) {
            textViewName.setText("");
            textViewMRN.setText("");
            textViewOpDateTime.setText("");
        } else {
            Patient item = patients.get(position);
            textViewName.setText(item.getPatientName());
            textViewMRN.setText(item.getPatientMRN());

            String formatStyle = "MMM dd, yyyy";
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(formatStyle);
            Calendar cal = new GregorianCalendar();
            dateTimeFormat.setTimeZone(cal.getTimeZone());
//            edit_opDate.setHint(dateTimeFormat.format(cal.getTime()));
            textViewOpDateTime.setText(dateTimeFormat.format(cal.getTime()));
        }
        return convertView;
    }
}
