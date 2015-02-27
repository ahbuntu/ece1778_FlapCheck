package ca.utoronto.flapcheck;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatientEntryNewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientEntryNewFragment extends Fragment
                                        implements PatientEntryArchiveInterac.OnPatientAdded{

    /**
     * interface definitions
     */
    public interface PatientNewEntryListener {
        public void onMeasureButtonClicked();
        public void onAddPatientButtonClicked();
    }

    Button button_addPatient;
    Button button_takeMeasurement;
    EditText edit_name ;
    EditText edit_mrn ;
    EditText edit_opDate;
    EditText edit_opTime;

    private PatientNewEntryListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PatientEntryNewFragment.
     */

    public static PatientEntryNewFragment  newInstance(String param1, String param2) {
        PatientEntryNewFragment fragment = new PatientEntryNewFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PatientEntryNewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_patient_entry_new, container, false);
        initWidgets(rootView);
        hintCurrentDateTime();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PatientNewEntryListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement PatientNewEntryListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        button_addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addPatientToDB(v)) {
                    //can't think of anything to do here; leaving it in just incase
                }
            }
        });

        button_takeMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addPatientToDB(v)) {
                    //can't think of anything to do here; leaving it in just incase
                }

                /******************DEBUG ONLY************************
                PatientOpenDBHelper db = new PatientOpenDBHelper(getActivity());
                db.deleteAllPatients();
                Toast.makeText(getActivity(), "patients deleted", Toast.LENGTH_SHORT);
                 ****************************************************/
            }
        });

        edit_opDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    showDatePickerDialog(v);
            }
        });
        edit_opDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        edit_opTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    showTimePickerDialog(v);
            }
        });
        edit_opTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });
    }

    /**
     * initializes the widgets on the activity/fragment
     */
    private void initWidgets(View view) {
        button_addPatient = (Button)  view.findViewById(R.id.button_addPatient);
        button_takeMeasurement = (Button) view.findViewById(R.id.button_takeMeasurement);
        edit_name = (EditText) view.findViewById(R.id.edit_name);
        edit_mrn = (EditText) view.findViewById(R.id.edit_mrn);
        edit_opDate = (EditText) view.findViewById(R.id.edit_opDate);
        edit_opTime = (EditText) view.findViewById(R.id.edit_opTime);
    }

    /**
     * runs validation to ensure patient can be added to the database.
     * saves patient details to the database
     */
    private boolean addPatientToDB(View v) {
        if (!isReadyToAdd()) {
            Toast.makeText(getActivity(), "Please enter the requested information.", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        String savedDateTime = edit_opDate.getText() + " " + edit_opTime.getText();
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));//need to use this while saving the datetime
        try {
            cal.setTime(dateFormat.parse(savedDateTime));
            Patient patient = new Patient(edit_name.getText().toString(),
                    edit_mrn.getText().toString(), cal.getTimeInMillis());

            PatientOpenDBHelper db = new PatientOpenDBHelper(getActivity());
            AddPatient addP = new AddPatient(v, db, this);
            addP.execute(patient);
            resetWidgets();
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error while trying to add new patient.", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

        return true;
    }
    /**
     * sets the widgets to the state when the fragment is first loaded
     */
    private void resetWidgets() {
        EditText edit_name = (EditText) getActivity().findViewById(R.id.edit_name);
        edit_name.setText(""); edit_name.setHint(R.string.hint_name);
        EditText edit_mrn = (EditText) getActivity().findViewById(R.id.edit_mrn);
        edit_mrn.setText(""); edit_mrn.setHint(R.string.hint_mrn);
        EditText edit_opDate = (EditText) getActivity().findViewById(R.id.edit_opDate);
        edit_opDate.setText("");
        EditText edit_opTime = (EditText) getActivity().findViewById(R.id.edit_opTime);
        edit_opTime.setText("");
        hintCurrentDateTime();
    }

    /**
     * formats the date and time and displays it on the edit text for opDate & opTime
     */
    private void hintCurrentDateTime() {
        String formatStyle = "MMM dd, yyyy";
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(formatStyle);
        Calendar cal = new GregorianCalendar();
        dateTimeFormat.setTimeZone(cal.getTimeZone());
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));//need to use this while saving the datetime
        edit_opDate.setHint(dateTimeFormat.format(cal.getTime()));

        formatStyle = "hh:mm a";
        dateTimeFormat = new SimpleDateFormat(formatStyle);
        edit_opTime.setHint(dateTimeFormat.format(cal.getTime()));
    }

    /**
     * checks to ensure that all patient information has been provided by the user
     * applies other validation rules, if any
     *
     * @return whether the patient can be added to the database
     */
    private boolean isReadyToAdd() {
        boolean proceed = true;
        if (edit_name.getText().toString().trim().equals("")
                || edit_mrn.getText().toString().trim().equals("")
                || edit_opDate.getText().toString().trim().equals("")
                || edit_opTime.getText().toString().trim().equals("")) {
            proceed = false;
        }
        return proceed;
    }
    /**
     * displays the date picker dialog
     * @param v
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            EditText edit_opDate = (EditText) getActivity().findViewById(R.id.edit_opDate);

            Calendar pickedDate = new GregorianCalendar();
            pickedDate.set(Calendar.YEAR, year);
            pickedDate.set(Calendar.MONTH, month);
            pickedDate.set(Calendar.DAY_OF_MONTH, day);

            String formatStyle = "MMM dd, yyyy";
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(formatStyle);
            dateTimeFormat.setTimeZone(pickedDate.getTimeZone());
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));//need to use this while saving the datetime
            edit_opDate.setText(dateTimeFormat.format(pickedDate.getTime()));
        }
    }

    /**
     * displays the time picker dialog
     * @param v the view that triggered the action
     */
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            EditText edit_opTime = (EditText) getActivity().findViewById(R.id.edit_opTime);

            Calendar pickedTime = new GregorianCalendar();
            pickedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            pickedTime.set(Calendar.MINUTE, minute);

            String formatStyle = "h:mm a";
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(formatStyle);
            dateTimeFormat.setTimeZone(pickedTime.getTimeZone());
            edit_opTime.setText(dateTimeFormat.format(pickedTime.getTime()));
        }
    }

    public void onPatientAdded(View callingView, Long rowId) {
        int id = callingView.getId();
        if (rowId != -1) { //means successfully added to the database
            switch (id) {
                case (R.id.button_addPatient):
                    //the toast cannot be triggered from here since the db action is async
                    Toast.makeText(getActivity(), "Patient added.", Toast.LENGTH_SHORT)
                            .show();
                    mListener.onAddPatientButtonClicked();
                    break;
                case (R.id.button_takeMeasurement):
                    //the toast cannot be triggered from here since the db action is async
                    Toast.makeText(getActivity(), "Patient added.", Toast.LENGTH_SHORT)
                            .show();
                    mListener.onMeasureButtonClicked();
                    break;
                default:
                    break;
            }
        } else {
            Toast.makeText(getActivity(), "Error while trying to add new patient.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public static class AddPatient extends AsyncTask<Patient, Integer, Long> {

        private PatientOpenDBHelper patientsDB;
        PatientEntryArchiveInterac.OnPatientAdded mCallback = null;
        View callingView;
        public AddPatient(View v, PatientOpenDBHelper db, PatientEntryArchiveInterac.OnPatientAdded ref) {
            patientsDB = db;
            mCallback = ref;
            callingView = v;
        }

        @Override
        protected Long doInBackground(Patient... patients) {
            Long rowID;
            rowID = patientsDB.addPatient(patients[0]);
            return rowID;
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            mCallback.onPatientAdded(callingView, result);
        }
    }
}
