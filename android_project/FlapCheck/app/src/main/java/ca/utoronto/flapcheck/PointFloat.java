package ca.utoronto.flapcheck;

/**
 * Created by kmurray on 26/03/15.
 */
public class PointFloat {
    public float x;
    public float y;

    PointFloat(float new_x, float new_y) {
        x = new_x;
        y = new_y;
    }

    void normalize(float padLeft, float padRight, float width, float height) {
        x -= padLeft;
        y -= padRight;
        x /= width;
        y /= height;
    }

    void denormalize(float padLeft, float padRight, float width, float height) {
        x *= width;
        y *= height;
        x += padLeft;
        y += padRight;
    }

}
