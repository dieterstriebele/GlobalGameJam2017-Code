package ggj2k15.bughole.bugholegraphicsengine.geometry;

import android.util.Log;
import java.io.InputStream;

public class Geometry_Sphere extends Geometry_Base {

    public Geometry_Sphere(InputStream rawResourceInputStream) {
        super();
        Log.d("Geometry_Sphere.Geometry_Sphere()", "Constructor called!");

        LoadVerticeAttributesFromInputStream(rawResourceInputStream);

        Initialise();
    }



}
