package ca.utoronto.flapcheck;

import android.os.Environment;

import java.io.File;

/**
 * Created by ahmadul.hassan on 2015-02-19.
 */
public class PointToMeasure {
    public static long INVALID_ID = -1;

    private long pointId = INVALID_ID; //this will only be populated after being inserted into the db
    private long patientId;
    private int pointIndex;
    private int pointX;
    private int pointY;

    /**
     * create an empty patient object with none of the attributes defined
     */
    public PointToMeasure() {

    }

    public PointToMeasure(long patID, int index, int x, int y) {
        patientId = patID;
        pointIndex = index;
        pointX = x;
        pointY = y;
    }

    /**
     * ONLY use this constructor when instantiating from a database record
     * under this condition, id of the patient is known
     * @param id
     * @param patID
     * @param index
     * @param x
     * @param y
     */
    public PointToMeasure(long id, long patID, int index, int x, int y) {
        pointId = id;
        patientId = patID;
        pointIndex = index;
        pointX = x;
        pointY = y;;
    }

    public long getPointId() {
        return pointId;
    }

    public void setPointId(long pointId) {
        this.pointId = pointId;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public int getPointIndex() {
        return pointIndex;
    }

    public void setPointIndex(int pointIndex) {
        this.pointIndex = pointIndex;
    }

    public int getPointX() {
        return pointX;
    }

    public void setPointX(int pointX) {
        this.pointX = pointX;
    }

    public int getPointY() {
        return pointY;
    }

    public void setPointY(int pointY) {
        this.pointY = pointY;
    }
}
