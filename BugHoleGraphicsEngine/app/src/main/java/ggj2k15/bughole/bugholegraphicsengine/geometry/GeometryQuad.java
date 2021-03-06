package ggj2k15.bughole.bugholegraphicsengine.geometry;

import android.util.Log;

public class GeometryQuad extends GeometryBase {

    public GeometryQuad() {
        super();
        Log.d("GeometryQuad.GeometryQuad()", "Constructor called!");

        m_VertexCount = 6;

        Set_VerticesAttributes(VerticesAttributes);
        Initialise();
    }

    public static float VerticesAttributes[] = new float[] {
            /** Vertices Positions */
            -1.0f, -1.0f,  1.0f,    /* 0  */
            1.0f, -1.0f,  1.0f,    /* 1  */
            -1.0f,  1.0f,  1.0f,    /* 2  */
            -1.0f,  1.0f,  1.0f,    /* 2  */
            1.0f, -1.0f,  1.0f,    /* 1  */
            1.0f,  1.0f,  1.0f,    /* 3  */

            /** Vertices Texture Coordinates */
            0.0f, 0.0f,             /* 0  */
            1.0f, 0.0f,             /* 1  */
            0.0f, 1.0f,             /* 2  */
            0.0f, 1.0f,             /* 2  */
            1.0f, 0.0f,             /* 1  */
            1.0f, 1.0f,             /* 3  */

            /** Normals are not really used - leave them emtpy here */
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
    };

}
