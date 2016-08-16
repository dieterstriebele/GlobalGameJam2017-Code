package ggj2k15.bughole.bugholegraphicsengine;

import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

public class Geometry_Base {

    public Geometry_Base() {
        //Zzzz X-)
    }

    protected int m_VertexAttributeVBO;   //Vertex Attributes VBO ID
    protected int m_VertexCount;          //Number of vertices in the Cube

    protected void Initialise() {
        int floatSize = Float.SIZE >> 3;
        Log.d("Geometry_Base.Initialise()", "Copy the vertices into the buffers ... m_VertexCount="+m_VertexCount);
        FloatBuffer vertexDataBuffer = FloatBuffer.wrap(this.m_VerticesAttributes);
        Log.d("Geometry_Base.Initialise()", "Generate the Vertex Buffers and get valid buffer IDs from OpenGL ...");
        m_VertexAttributeVBO = GLES20Helper.generateGenericBufferID();
        Log.d("Geometry_Base.Initialise()", "Load the VBO data into the driver ...");
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.m_VertexAttributeVBO);
        GLES20.glBufferData(
                GLES20.GL_ARRAY_BUFFER,
                (this.m_VertexCount * 3 * floatSize) + //vertex positions
                (this.m_VertexCount * 2 * floatSize) + //texture coordinates
                (this.m_VertexCount * 3 * floatSize),  //normals
                vertexDataBuffer,
                GLES20.GL_STATIC_DRAW
        );
        Log.d("Geometry_Base.Initialise()", "Unbinding the VBO ...");
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
    }

    protected void LoadVerticeAttributesFromInputStream(InputStream inInputStream) {
        Log.d("Geometry_Base.LoadVerticeAttributesFromInputStream()", "Loading vertex attributes (positions, texture coordinates, normals) from input stream!");
        try {
            //http://developer.android.com/reference/java/io/DataInputStream.html
            DataInputStream obfInputStream = new DataInputStream(new BufferedInputStream(inInputStream));
            m_VertexCount = (int) obfInputStream.readFloat();
            m_VerticesAttributes = new float[(m_VertexCount * 3) + (m_VertexCount * 2) + (m_VertexCount * 3)];
            for (int i=0; i<m_VerticesAttributes.length; i++) {
                m_VerticesAttributes[i] = obfInputStream.readFloat();
            }
        } catch (IOException e) {
            Log.e("Geometry_Sphere.Geometry_Sphere()", "Constructor failed!", e);
        }
    }

    protected int m_ShaderProgramID;
    protected int m_VerticesPositionAttributeId;
    protected int m_VerticesTextureAttributeId;
    protected int m_VerticesNormalAttributeId;
    protected int m_ModelViewProjectionUniformId;
    protected int m_ModelViewUniformId;
    protected int m_ObjectPositionUniformId;
    protected int m_ObjectRotationUniformId;
    protected int m_ObjectScalingUniformId;
    protected int m_DiffuseTextureUniformId;
    protected int m_NormalTextureUniformId;
    protected int m_SpecularIntensityTextureUniformId;
    protected int m_SpecularColorTextureUniformId;
    protected int m_AmbientOcclusionTextureUniformId;

    //OPT
    protected int m_CombinedDiffuseAndAmbientOcclusionTextureUniformId;
    protected int m_CombinedSpecularColorAndSpecularIntensityTextureUniformId;
    protected int m_CombinedNormalTextureUniformId;

    //GENERIC/QUAD
    protected int m_GenericTextureSampler0UniformId;
    protected int m_GenericTextureSampler1UniformId;
    protected int m_GenericTextureSampler2UniformId;
    protected int m_GenericTextureSampler3UniformId;
    protected int m_ScreenResolutionUniformId;
    protected int m_TimeUniformId;

    public int Get_VerticesPositionAttribute_ID() { return m_VerticesPositionAttributeId; }
    public int Get_VerticesTextureAttribute_ID() { return m_VerticesTextureAttributeId; }
    public int Get_VerticesNormalAttribute_ID() { return m_VerticesNormalAttributeId; }
    public int Get_ModelViewProjection_ID() { return m_ModelViewProjectionUniformId; }
    public int Get_ModelView_ID() { return m_ModelViewUniformId; }
    public int Get_ObjectPosition_ID() { return m_ObjectPositionUniformId; }
    public int Get_ObjectRotation_ID() { return m_ObjectRotationUniformId; }
    public int Get_ObjectScaling_ID() { return m_ObjectScalingUniformId; }
    public int Get_ShaderProgram_ID() { return m_ShaderProgramID; }
    public int Get_ScreenResolution_ID() { return m_ScreenResolutionUniformId; }
    public int Get_Time_ID() { return m_TimeUniformId; }

    public void UseShaderAndBindVBO() {
        int floatSize = Float.SIZE >> 3;
        GLES20.glUseProgram(this.m_ShaderProgramID);
        //bind the VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, Get_VertexAttributeVBO_ID());
        //set the pointers to the vertex position attributes
        //the magic number 3 means 3-float-components for each vertex position
        GLES20.glVertexAttribPointer(this.m_VerticesPositionAttributeId, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(this.m_VerticesPositionAttributeId);
        // set the pointers to the vertex texture coordinates attributes
        // the magic number 2 means 2-float-components for each vertex texture coordinate
        // the magic number 3 means 3-float-components for each vertex position
        GLES20.glVertexAttribPointer(this.m_VerticesTextureAttributeId, 2, GLES20.GL_FLOAT, false, 0 , Get_VertexCount() * 3 * floatSize);
        GLES20.glEnableVertexAttribArray(this.m_VerticesTextureAttributeId);
        // set the pointers to the vertex normal attributes
        // the magic number 3 means 3-float-components for each vertex texture coordinate
        GLES20.glVertexAttribPointer(this.m_VerticesNormalAttributeId, 3, GLES20.GL_FLOAT, false, 0 , (Get_VertexCount() * 3 * floatSize) + (Get_VertexCount() * 2 * floatSize));
        GLES20.glEnableVertexAttribArray(this.m_VerticesNormalAttributeId);
    }

    public void Set_ShaderID(int inShaderID) {
        m_ShaderProgramID = inShaderID;
        //get Vertices Position attribute index from GLES
        this.m_VerticesPositionAttributeId = GLES20.glGetAttribLocation(this.m_ShaderProgramID, "aPosition");
        if (this.m_VerticesPositionAttributeId == -1) {
           //throw new RuntimeException("aPosition attribute location invalid");
        }
        //get Vertices Texture Coordinate attribute index from GLES
        this.m_VerticesTextureAttributeId = GLES20.glGetAttribLocation(this.m_ShaderProgramID, "aTextureCoordinate");
        if (this.m_VerticesTextureAttributeId == -1) {
            //throw new RuntimeException("aTextureCoordinate attribute location invalid");
        }
        //get Vertices Normal attribute index from GLES
        this.m_VerticesNormalAttributeId = GLES20.glGetAttribLocation(this.m_ShaderProgramID, "aNormal");
        if (this.m_VerticesNormalAttributeId == -1) {
            //throw new RuntimeException("aNormal attribute location invalid");
        }
        //get Model View Projection matrix index from GLES
        this.m_ModelViewProjectionUniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "uModelViewProjection");
        if (this.m_ModelViewProjectionUniformId == -1) {
            //throw new RuntimeException("uModelViewProjection location invalid");
        }
        //get Model View Projection matrix index from GLES
        this.m_ModelViewUniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "uModelView");
        if (this.m_ModelViewUniformId == -1) {
            //throw new RuntimeException("uModelView location invalid");
        }
        //get object translation matrix index from GLES
        this.m_ObjectPositionUniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "uObjectPosition");
        if (this.m_ObjectPositionUniformId == -1) {
            //throw new RuntimeException("uObjectPosition location invalid");
        }
        //get object rotation matrix index from GLES
        this.m_ObjectRotationUniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "uObjectRotation");
        if (this.m_ObjectRotationUniformId == -1) {
            //throw new RuntimeException("uObjectRotation location invalid");
        }
        //get object scaling matrix index from GLES
        this.m_ObjectScalingUniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "uObjectScaling");
        if (this.m_ObjectScalingUniformId == -1) {
            //throw new RuntimeException("uObjectScaling location invalid");
        }
        //get diffuse texture index from GLES
        this.m_DiffuseTextureUniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "sDiffuseSampler");
        if (this.m_DiffuseTextureUniformId == -1) {
            //throw new RuntimeException("sDiffuseSampler location invalid");
        }
        //get normal texture index from GLES
        this.m_NormalTextureUniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "sNormalSampler");
        if (this.m_NormalTextureUniformId == -1) {
            //throw new RuntimeException("sNormalSampler location invalid");
        }
        //get specular intensity texture index from GLES
        this.m_SpecularIntensityTextureUniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "sSpecularIntensitySampler");
        if (this.m_SpecularIntensityTextureUniformId == -1) {
            //throw new RuntimeException("sSpecularIntensitySampler location invalid");
        }
        //get specular color texture index from GLES
        this.m_SpecularColorTextureUniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "sSpecularColorSampler");
        if (this.m_SpecularColorTextureUniformId == -1) {
            //throw new RuntimeException("sSpecularColorSampler location invalid");
        }
        //get ambientocclusion texture index from GLES
        this.m_AmbientOcclusionTextureUniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "sAmbientOcclusionSampler");
        if (this.m_AmbientOcclusionTextureUniformId == -1) {
            //throw new RuntimeException("sAmbientOcclusionSampler location invalid");
        }

        //OPT
        //get combined diffuse and ambient occlusion texture index from GLES
        this.m_CombinedDiffuseAndAmbientOcclusionTextureUniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "sCombinedDiffuseAndAmbientOcclusionSampler");
        if (this.m_CombinedDiffuseAndAmbientOcclusionTextureUniformId == -1) {
            //throw new RuntimeException("m_CombinedDiffuseAndAmbientOcclusionTextureUniformId location invalid");
        }
        //OPT
        //get combined specularcolor and specularintensity texture index from GLES
        this.m_CombinedSpecularColorAndSpecularIntensityTextureUniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "sCombinedSpecularColorAndSpecularIntensitySampler");
        if (this.m_CombinedSpecularColorAndSpecularIntensityTextureUniformId == -1) {
            //throw new RuntimeException("m_CombinedSpecularColorAndSpecularIntensityTextureUniformId location invalid");
        }
        //OPT
        //get combined normal texture index from GLES
        this.m_CombinedNormalTextureUniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "sCombinedNormalSampler");
        if (this.m_CombinedNormalTextureUniformId == -1) {
            //throw new RuntimeException("m_CombinedNormalTextureUniformId location invalid");
        }

        //GENRIC/QUAD
        //get generic texture sampler texture index from GLES
        this.m_GenericTextureSampler0UniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "sGenericTextureSampler0");
        if (this.m_GenericTextureSampler0UniformId == -1) {
            //throw new RuntimeException("m_GenericTextureSampler00UniformId location invalid");
        }
        //get generic texture sampler texture index from GLES
        this.m_GenericTextureSampler1UniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "sGenericTextureSampler1");
        if (this.m_GenericTextureSampler1UniformId == -1) {
            //throw new RuntimeException("m_GenericTextureSampler1UniformId location invalid");
        }
        //get generic texture sampler texture index from GLES
        this.m_GenericTextureSampler2UniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "sGenericTextureSampler2");
        if (this.m_GenericTextureSampler2UniformId == -1) {
            //throw new RuntimeException("m_GenericTextureSampler2UniformId location invalid");
        }
        //get generic texture sampler texture index from GLES
        this.m_GenericTextureSampler3UniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "sGenericTextureSampler3");
        if (this.m_GenericTextureSampler3UniformId == -1) {
            //throw new RuntimeException("m_GenericTextureSampler3UniformId location invalid");
        }
        //get screen resolution uniform id from GLES
        this.m_ScreenResolutionUniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "uScreenResolution");
        if (this.m_ScreenResolutionUniformId == -1) {
            //throw new RuntimeException("m_ScreenResolutionUniformId location invalid");
        }
        //get screen resolution uniform id from GLES
        this.m_TimeUniformId = GLES20.glGetUniformLocation(this.m_ShaderProgramID, "uTime");
        if (this.m_TimeUniformId == -1) {
            //throw new RuntimeException("m_TimeUniformId location invalid");
        }
    }

    protected int m_DiffuseTextureID;
    public void Set_DiffuseTextureID(int inDiffuseTextureID) { m_DiffuseTextureID = inDiffuseTextureID; }

    protected int m_NormalTextureID;
    public void Set_NormalTextureID(int inNormalTextureID) { m_NormalTextureID = inNormalTextureID; }

    protected int m_SpecularIntensityTextureID;
    public void Set_SpecularIntensityTextureID(int inSpecularIntensityTextureID) { m_SpecularIntensityTextureID = inSpecularIntensityTextureID; }

    protected int m_SpecularColorTextureID;
    public void Set_SpecularColorTextureID(int inSpecularColorTextureID) { m_SpecularColorTextureID = inSpecularColorTextureID; }

    protected int m_AmbientOcclusionTextureID;
    public void Set_AmbientOcclusionTextureID(int inAmbientOcclusionTextureID) { m_AmbientOcclusionTextureID = inAmbientOcclusionTextureID; }

    //OPT
    protected int m_CombinedDiffuseAndAmbientOcclusionTextureID;
    public void Set_CombinedDiffuseAndAmbientOcclusionTextureID(int inCombinedDiffuseAndAmbientOcclusionTextureID) { m_CombinedDiffuseAndAmbientOcclusionTextureID = inCombinedDiffuseAndAmbientOcclusionTextureID; }

    //OPT
    protected int m_CombinedSpecularColorAndSpecularIntensityTextureID;
    public void Set_CombinedSpecularColorAndSpecularIntensityTextureID(int inCombinedSpeculatColorAndSpecularIntensityTextureID) { m_CombinedSpecularColorAndSpecularIntensityTextureID = inCombinedSpeculatColorAndSpecularIntensityTextureID; }

    //OPT
    protected int m_CombinedNormalTextureID;
    public void Set_CombinedNormalTextureID(int inCombinedNormalTextureID) { m_CombinedNormalTextureID = inCombinedNormalTextureID; }

    //GENERIC/QUAD
    protected int m_GenericTexture0TextureID;
    public void Set_GenericTexture0TextureID(int inGenericTexture0TextureID) { m_GenericTexture0TextureID = inGenericTexture0TextureID; }

    //GENERIC/QUAD
    protected int m_GenericTexture1TextureID;
    public void Set_GenericTexture1TextureID(int inGenericTexture1TextureID) { m_GenericTexture1TextureID = inGenericTexture1TextureID; }

    //GENERIC/QUAD
    protected int m_GenericTexture2TextureID;
    public void Set_GenericTexture2TextureID(int inGenericTexture2TextureID) { m_GenericTexture2TextureID = inGenericTexture2TextureID; }

    //GENERIC/QUAD
    protected int m_GenericTexture3TextureID;
    public void Set_GenericTexture3TextureID(int inGenericTexture3TextureID) { m_GenericTexture3TextureID = inGenericTexture3TextureID; }

    //GENERIC/QUAD
    protected float[] m_ScreenResolution;
    public void Set_ScreenResolution(float[] inScreenResolution) { m_ScreenResolution = inScreenResolution; }
    public float[] Get_ScreenResolution() { return m_ScreenResolution; }
    protected float m_Time;
    public void Set_Time(float inTime) { m_Time = inTime; }
    public float Get_Time() { return m_Time; }

    public void BindTexturesAndDrawGeometry() {
        //set the Texture unit 0 active and bind the texture to it
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //bind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.m_DiffuseTextureID);
        //set the Texture unit 1 active and bind the texture to it
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        //bind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.m_NormalTextureID);
        //set the Texture unit 2 active and bind the texture to it
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        //bind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.m_SpecularIntensityTextureID);
        //set the Texture unit 3 active and bind the texture to it
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        //bind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.m_AmbientOcclusionTextureID);
        //set the Texture unit 4 active and bind the texture to it
        GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
        //bind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.m_SpecularColorTextureID);

        GLES20.glUniform1i(m_DiffuseTextureUniformId, 0);
        GLES20.glUniform1i(m_NormalTextureUniformId, 1);
        GLES20.glUniform1i(m_SpecularIntensityTextureUniformId, 2);
        GLES20.glUniform1i(m_AmbientOcclusionTextureUniformId, 3);
        GLES20.glUniform1i(m_SpecularColorTextureUniformId, 4);

        //draw the cube using the indices. There are total 36 vertices to draw
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, this.m_VertexCount);
    }

    //OPT
    public void BindCombinedTexturesAndDrawGeometry() {
        //set the Texture unit 0 active and bind the texture to it
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //bind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.m_CombinedDiffuseAndAmbientOcclusionTextureID);
        //set the Texture unit 1 active and bind the texture to it
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        //bind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.m_CombinedNormalTextureID);
        //set the Texture unit 2 active and bind the texture to it
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        //bind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.m_CombinedSpecularColorAndSpecularIntensityTextureID);
        GLES20.glUniform1i(this.m_CombinedDiffuseAndAmbientOcclusionTextureUniformId, 0);
        GLES20.glUniform1i(this.m_CombinedNormalTextureUniformId, 1);
        GLES20.glUniform1i(this.m_CombinedSpecularColorAndSpecularIntensityTextureUniformId, 2);
        //draw the cube using the indices. There are total 36 vertices to draw
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, this.m_VertexCount);
    }

    //GENERIC/QUAD
    public void BindGenericTexturesAndDrawGeometry() {
        //set the Texture unit 0 active and bind the texture to it
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //bind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.m_GenericTexture0TextureID);
        //set the Texture unit 1 active and bind the texture to it
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        //bind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.m_GenericTexture1TextureID);
        //set the Texture unit 2 active and bind the texture to it
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        //bind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.m_GenericTexture2TextureID);
        //set the Texture unit 3 active and bind the texture to it
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        //bind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.m_GenericTexture3TextureID);
        GLES20.glUniform1i(this.m_GenericTextureSampler0UniformId, 0);
        GLES20.glUniform1i(this.m_GenericTextureSampler1UniformId, 1);
        GLES20.glUniform1i(this.m_GenericTextureSampler2UniformId, 2);
        GLES20.glUniform1i(this.m_GenericTextureSampler3UniformId, 3);
        //draw the cube using the indices. There are total 36 vertices to draw
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, this.m_VertexCount);
    }

    public void UnUseShaderAndUnBindVBO() {
        GLES20.glUseProgram(0);
        //doesn't seem to work the way it's supposed to be X-)
        //GLES20.glDisableVertexAttribArray(this.m_VerticesPositionAttributeId);
        //GLES20.glDisableVertexAttribArray(this.m_VerticesTextureAttributeId);
        //GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        //GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public int Get_VertexAttributeVBO_ID() { return m_VertexAttributeVBO; }
    public int Get_VertexCount() { return m_VertexCount; }

    protected void Set_VerticesAttributes(float inVerticesAttributes[]) {
        m_VerticesAttributes = inVerticesAttributes;
    }

    private float m_VerticesAttributes[];

}
