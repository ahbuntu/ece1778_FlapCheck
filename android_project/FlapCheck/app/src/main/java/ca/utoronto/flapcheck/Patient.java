package ca.utoronto.flapcheck;

import android.os.Environment;

import java.io.File;

/**
 * Created by ahmadul.hassan on 2015-02-19.
 */
public class Patient {
    public static long INVALID_ID = -1;

    private long patientId = INVALID_ID; //this will only be populated after being inserted into the db
    private String patientName;
    private String patientMRN;
    private Long patientOpDateTime; //displaying this in human friendly format should be handled at UI level
    private String patientPhotoPath;
    private String patientVidPath;

    /**
     * create an empty patient object with none of the attributes defined
     */
    public Patient() {

    }

    /**
     * constructor for patient when details are known
     *
     * @param name patient name
     * @param mrn patient MRN number
     * @param opDateTime datetime of operation
     */
    public Patient (String name, String mrn, Long opDateTime) {
        patientName = name;
        patientMRN = mrn;
        patientOpDateTime = opDateTime;
        patientPhotoPath = createPhotoPath(mrn, opDateTime);
        patientVidPath = createVideoPath(mrn, opDateTime);
    }

    /**
     * ONLY use this constructor when instantiating from a database record
     * under this condition, id of the patient is known
     *
     * @param name patient name
     * @param mrn patient MRN number
     * @param opDateTime datetime of operation
     */
    public Patient (Long id, String name, String mrn, Long opDateTime, String imgPath, String vidPath) {
        patientId = id ;
        patientName = name;
        patientMRN = mrn;
        patientOpDateTime = opDateTime;
        patientPhotoPath = imgPath;
        patientVidPath = vidPath;
    }

    /**
     * follows convention /images/<mrn>/<opTime>
     * opTime stored as long to make it easier to sort
     * @param mrn
     * @param opTime
     * @return
     */
    private String createPhotoPath(String mrn, long opTime) {
        String uniquePath = "FLAPCHECK" + "/" + "images" + "/" + mrn +"/" + opTime;
        // Get the directory for the user's public pictures directory.
        File filePath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), uniquePath);
        filePath.mkdirs();

        return filePath.getAbsolutePath();
    }

    /**
     * follows convention /videos/<mrn>/<opTime>
     * opTime stored as long to make it easier to sort
     * @param mrn
     * @param opTime
     * @return
     */
    private String createVideoPath(String mrn, long opTime) {
        String uniquePath = "FLAPCHECK" + "/" + "videos" + "/" + mrn +"/" + opTime;
        // Get the directory for the user's public pictures directory.
        File filePath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), uniquePath);
        filePath.mkdirs();

        return filePath.getAbsolutePath();
    }

    //************* Setters & Getters **************

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientMRN(String patientMRN) {
        this.patientMRN = patientMRN;
    }

    public String getPatientMRN() {
        return patientMRN;
    }

    public void setPatientOpDateTime(Long patientOpDateTime) {
        this.patientOpDateTime = patientOpDateTime;
    }

    public Long getPatientOpDateTime() {
        return patientOpDateTime;
    }

    public String toString() {
        return String.format("%s (%s)", getPatientName(), getPatientMRN());
    }

    public void setPatientPhotoPath(String patientPhotoPath) {
        this.patientPhotoPath = patientPhotoPath;
    }

    public String getPatientPhotoPath() {
        return patientPhotoPath;
    }

    public void setPatientVidPath(String patientVidPath) {
        this.patientVidPath = patientVidPath;
    }

    public String getPatientVidPath() {
        return patientVidPath;
    }
}
