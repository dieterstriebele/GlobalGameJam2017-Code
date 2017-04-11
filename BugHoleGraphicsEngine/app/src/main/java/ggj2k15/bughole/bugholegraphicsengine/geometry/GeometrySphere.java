package ggj2k15.bughole.bugholegraphicsengine.geometry;

import android.util.Log;
import java.io.InputStream;

public class GeometrySphere extends GeometryBase {

    public GeometrySphere(InputStream rawResourceInputStream) {
        super();
        Log.d("GeometrySphere.GeometrySphere()", "Constructor called!");

        LoadVerticeAttributesFromInputStream(rawResourceInputStream);

        Initialise();
    }



}
