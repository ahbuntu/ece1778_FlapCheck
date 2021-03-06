package ca.utoronto.flapcheck;

/**
 * Created by ahmadul.hassan on 2015-02-19.
 */
public class MeasurementReading {

    private long measurementID = -1; //this will only be populated after being inserted into the db
    private long meas_patientID;
    private long meas_timestamp;
    private int meas_position;
    private float meas_temperature;
    private String meas_colour_rgb;
    private String meas_colour_lab;
    private String meas_colour_hex;

    /**
     * create an empty reading object with none of the attributes defined
     */
    public MeasurementReading() {
    }

    /**
     * constructor for the measurement reading when details are known
     * @param patientID
     * @param timestamp
     * @param temperature
     */
    public MeasurementReading(long patientID, long timestamp, int position, float temperature,
                              String colour_rgb, String colour_lab, String colour_hex) {
        meas_patientID = patientID;
        meas_timestamp = timestamp;
        meas_temperature = temperature;
        meas_position = position;
        meas_colour_rgb = colour_rgb;
        meas_colour_lab = colour_lab;
        meas_colour_hex = colour_hex;
    }

    /**
     * ONLY use this constructor when instantiating from a database record
     * under this condition, id of the measurement record is known
     * @param id
     * @param patientID
     * @param timestamp
     * @param temperature
     * @param colour_rgb
     * @param colour_lab
     * @param colour_hex
     */
    public MeasurementReading(long id, long patientID, long timestamp, int position, float temperature,
                              String colour_rgb, String colour_lab, String colour_hex) {
        measurementID = id;
        meas_patientID = patientID;
        meas_timestamp = timestamp;
        meas_position = position;
        meas_temperature = temperature;
        meas_colour_rgb = colour_rgb;
        meas_colour_lab = colour_lab;
        meas_colour_hex = colour_hex;
    }

    //************* Setters & Getters **************


    public void setMeasurementID(long measurementID) {
        this.measurementID = measurementID;
    }

    public long getMeasurementID() {
        return measurementID;
    }

    public void setMeas_patientID(long meas_patientID) {
        this.meas_patientID = meas_patientID;
    }

    public long getMeas_patientID() {
        return meas_patientID;
    }

    public void setMeas_timestamp(long meas_timestamp) {
        this.meas_timestamp = meas_timestamp;
    }

    public long getMeas_timestamp() {
        return meas_timestamp;
    }

    public int getMeas_position() {
        return meas_position;
    }

    public void setMeas_position(int meas_position) {
        this.meas_position = meas_position;
    }

    public void setMeas_temperature(float meas_temperature) {
        this.meas_temperature = meas_temperature;
    }

    public float getMeas_temperature() {
        return meas_temperature;
    }

    public void setMeas_colour_rgb(String meas_colour_rgb) {
        this.meas_colour_rgb = meas_colour_rgb;
    }

    public String getMeas_colour_rgb() {
        return meas_colour_rgb;
    }

    public void setMeas_colour_lab(String meas_colour_lab) {
        this.meas_colour_lab = meas_colour_lab;
    }

    public String getMeas_colour_lab() {
        return meas_colour_lab;
    }

    public void setMeas_colour_hex(String meas_colour_hex) {
        this.meas_colour_hex = meas_colour_hex;
    }

    public String getMeas_colour_hex() {
        return meas_colour_hex;
    }
}
