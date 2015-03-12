package ca.utoronto.flapcheck;

/**
 * Created by ahmadul.hassan on 2015-02-19.
 */
public class Constants {
    //***************** DATABASE ******************************
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "FLAPCHECK";

    //***************** Media Directory ***********************

    //***************** Measurement Types *********************
    public static final String MEASUREMENT_PHOTO = "Photograph";
    public static final String MEASUREMENT_TEMP = "Temperature";
    public static final String MEASUREMENT_COLOUR = "Colour";
    public static final String MEASUREMENT_CAP_REFILL = "Capilary Refill";
    public static final String MEASUREMENT_PULSE = "Pulse";

    //***************** Activity Request Codes ****************
    public static final int ADD_PATIENT_REQUEST = 1;
    public static final int RECORD_VIDEO_REQUEST = 2;
    public static final int GET_TEMPERATURE_REQUEST = 201;

    //***************** NODE measurement default/invalid values **
    public static final float TEMP_INVALID_MEAS  = 0.0f;
    public static final int COLOUR_INVALID_MEAS  = R.color.fc_dark_gray;

    //***************** BUNDlE key names  *********************
    //proposed format:
    // 1. prefix with activity or fragment name
    // 2. followed by KEY_
    public static final String PATIENT_ENTRY_KEY_ADDED_PATIENT_ID = "Added_Patient_ID";
}
