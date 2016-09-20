package util;

/**
 * Created by ahoppe on 28.02.2015.
 */
public class Vector3D {
    public static final Vector3D Zero = new Vector3D();
    public static final Vector3D One = new Vector3D(1f, 1f, 1f);

    public float mXPos = 0f;
    public float mYPos = 0f;
    public float mZPos = 0f;

    public Vector3D() {}

    public Vector3D(float x, float y, float z)
    {
        mXPos = x;
        mYPos = y;
        mZPos = z;
    }

    public Vector3D(Vector3D vec)
    {
        set(vec);
    }

    public void add(Vector3D vec) {
        mXPos += vec.mXPos;
        mYPos += vec.mYPos;
        mZPos += vec.mZPos;
    }

    public void add(float dx, float dy, float dz) {
        mXPos += dx;
        mYPos += dy;
        mZPos += dz;
    }

    /**
     * Compute the cross product of two vectors (this x vec)
     *
     * @param vec
     *            The second vector
     * @param result
     *            Where to store the cross product
     **/
    public void cross(Vector3D vec, Vector3D result) {
        result.mXPos = mYPos * vec.mZPos - vec.mYPos * mZPos;
        result.mYPos = mZPos * vec.mXPos - vec.mZPos * mXPos;
        result.mZPos = mXPos * vec.mYPos - vec.mXPos * mYPos;
    }

    public float dot(Vector3D vec) {
        return mXPos*vec.mXPos + mYPos*vec.mYPos + mZPos*vec.mZPos;
    }

    public float angleToVectorRad(Vector3D vec) {
        return (float)Math.acos(dot(vec));
    }

    public float[] asArray() {
        return new float[] { mXPos, mYPos, mZPos, 0 };
    }

    public void sub(Vector3D vec) {
        mXPos -= vec.mXPos;
        mYPos -= vec.mYPos;
        mZPos -= vec.mZPos;
    }

    public void div(float len) {
        mXPos /= len;
        mYPos /= len;
        mZPos /= len;
    }

    public float len() {
        return (float)Math.sqrt(mXPos*mXPos + mYPos*mYPos + mZPos*mZPos);

    }

    public void mul(float len) {
        mXPos *= len;
        mYPos *= len;
        mZPos *= len;
    }

    public void set(Vector3D vec) {
        mXPos = vec.mXPos;
        mYPos = vec.mYPos;
        mZPos = vec.mZPos;
    }

    public void set(float xPos, float yPos, float zPos) {
        mXPos = xPos;
        mYPos = yPos;
        mZPos = zPos;
    }

    public void mul(float[] matrix) {
        float xPos = mXPos;
        float yPos = mYPos;
        float zPos = mZPos;

        // Column - major - order
//        mXPos = matrix[0] * xPos + matrix[4] * yPos + matrix[8] * zPos;
//        mYPos = matrix[1] * xPos + matrix[5] * yPos + matrix[9] * zPos;
//        mZPos = matrix[2] * xPos + matrix[6] * yPos + matrix[10] * zPos;

        // Row - major - order
        mXPos = matrix[0] * xPos + matrix[4] * yPos + matrix[8] * zPos;
        mYPos = matrix[1] * xPos + matrix[5] * yPos + matrix[9] * zPos;
        mZPos = matrix[2] * xPos + matrix[6] * yPos + matrix[10] * zPos;
    }

    public float squareDistance(Vector3D vec) {
        float dx = mXPos - vec.mXPos;
        float dy = mYPos - vec.mYPos;
        float dz = mZPos - vec.mZPos;

        return dx*dx + dy*dy + dz*dz;
    }

    public void normalize() {
        float len = (float)Math.sqrt(mXPos*mXPos + mYPos*mYPos + mZPos*mZPos);

        mXPos /= len;
        mYPos /= len;
        mZPos /= len;
    }

    public void setLength(float length) {
        normalize();
        mul(length);
    }

    public String toString() {
        return "" + mXPos + ", " + mYPos + ", " + mZPos;
    }
}
