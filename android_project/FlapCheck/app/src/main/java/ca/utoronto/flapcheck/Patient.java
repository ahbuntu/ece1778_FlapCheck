package ca.utoronto.flapcheck;

/**
 * Created by ahmadul.hassan on 2015-02-19.
 */
public class Patient {

    private long patientId = -1; //this will only be populated after being inserted into the db
    private String patientName;
    private String patientMRN;
    private Long patientOpDateTime; //displaying this in human friendly format should be handled at UI level

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
    }

    /**
     * ONLY use this constructor when instantiating from a database record
     * under this condition, id of the patient is known
     *
     * @param name patient name
     * @param mrn patient MRN number
     * @param opDateTime datetime of operation
     */
    public Patient (Long id, String name, String mrn, Long opDateTime) {
        patientId = id ;
        patientName = name;
        patientMRN = mrn;
        patientOpDateTime = opDateTime;
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
}
