package ggj2k15.bughole.bugholegraphicsengine.gles20;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.graphics.Bitmap;
import java.io.InputStream;
import ggj2k15.bughole.bugholegraphicsengine.BugHoleGameController;
import ggj2k15.bughole.bugholegraphicsengine.camerainformation.impl.CameraInformationTouch;
import ggj2k15.bughole.bugholegraphicsengine.geometry.GeometryBase;
import ggj2k15.bughole.bugholegraphicsengine.geometry.GeometryQuad;
import ggj2k15.bughole.bugholegraphicsengine.geometry.GeometrySphere;
import ggj2k15.bughole.bugholegraphicsengine.R;
import ggj2k15.bughole.bugholegraphicsengine.interfaces.IClientInformation;
import ggj2k15.bughole.bugholegraphicsengine.interfaces.IGeometryInformation;

public class GLES20Content {

    //rendering context
    private Context m_Context;

    private BugHoleGameController m_gameController;

    private int m_NumberOfGeometries = 3;

    private GeometryBase[] m_Geometries = new GeometryBase[m_NumberOfGeometries];

    //Google Pixel C    Resolution = 2560 x 1800 - 1:1.414
    //Google Nexus 9    Resolution = 2048 x 1536 - 4:3
    //Google Nexus 10   Resolution = 2560 x 1600 - 16:10
    //Samsung Galaxy S6 Resolution = 2560 x 1440 - 16:9
    //Google Nexus 5    Resoltuion = 1920 x 1080 - 16:9

    //Frontbuffer
    private int m_FrontBuffer_Width = 2560;
    private int m_FrontBuffer_Height = 1504;

    //FBO for the main scene
    private int m_FBO_MainScene_Width = 2560;
    private int m_FBO_MainScene_Height = 1504;
    private int m_FBO_MainScene_ID;
    private int m_FBO_MainScene_ColorAttachment_TextureID;
    private int m_FBO_MainScene_DepthRenderBuffer_ID;
    private float m_FBO_Mainscene_NativeResolutionFactor = 0.25f;
    private float m_FBO_Mainscene_HUD_NativeResolutionFactor = 0.5f;
    private int m_FBO_MainScene_HUD_Width = 2560;
    private int m_FBO_MainScene_HUD_Height = 1504;
    private GeometryBase m_GeometryQuad_FBO_MainScene;
    private long m_StartTimeInMillis;
    private Bitmap m_HUDFontBitmap;
    private Canvas m_HUDFontCanvas;
    private Typeface m_HUDTypeface;

    //FBO for the lowres, blurred version of the main scene
    private int m_FBO_BlurredMainScene_Width = 2560;
    private int m_FBO_BlurredMainScene_Height = 1504;
    private int m_FBO_BlurredMainScene_ID;
    private int m_FBO_BlurredMainScene_ColorAttachment_TextureID;
    private int m_FBO_BlurredMainScene_DepthRenderBuffer_ID;
    private float m_FBO_BlurredMainscene_NativeResolutionFactor = 0.125f;
    private GeometryBase m_GeometryQuad_FBO_BlurredMainScene;

    //DEBUG SETTINGS
    private static final boolean DEBUG_NOFBO_DIRECTRENDERING = false;
    private static final boolean DEBUG_NOTEXTUREOPTIMIZATION = true;

    public GLES20Content(Context a_Context, BugHoleGameController controller) {
        super();
        Log.d("GLES20Content.GLES20Content()", "Constructor called!");
        this.m_Context = a_Context;
        m_gameController = controller;
    }

    public void Initialize(int a_Width, int a_Height) {
        m_FrontBuffer_Width = a_Width;
        m_FrontBuffer_Height = a_Height;
        m_FBO_MainScene_Width = (int)(a_Width/(1.0f/ m_FBO_Mainscene_NativeResolutionFactor));
        m_FBO_MainScene_Height = (int)(a_Height/(1.0f/ m_FBO_Mainscene_NativeResolutionFactor));
        m_FBO_MainScene_HUD_Width = (int)(a_Width/(1.0f/ m_FBO_Mainscene_HUD_NativeResolutionFactor));
        m_FBO_MainScene_HUD_Height = (int)(a_Height/(1.0f/ m_FBO_Mainscene_HUD_NativeResolutionFactor));
        m_FBO_BlurredMainScene_Width = (int)(a_Width/(1.0f/ m_FBO_BlurredMainscene_NativeResolutionFactor));
        m_FBO_BlurredMainScene_Height = (int)(a_Height/(1.0f/ m_FBO_BlurredMainscene_NativeResolutionFactor));

        //OpenGL ES initialization
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        int tShaderProgramID_Geometry = -1;
        int tDiffuseTextureId = -1;
        int tNormalTextureId = -1;
        int tSpecularIntensityTextureId = -1;
        int tSpecularColorTextureId = -1;
        int tAmbientOcclusionTextureId = -1;

        int tCombinedDiffuseAndAmbientOcclusionTextureId = -1;
        int tCombinedSpecularColorAndSpecularIntensityTextureId = -1;
        int tCombinedNormalTextureId = -1;

        if (DEBUG_NOTEXTUREOPTIMIZATION) {
            //use unoptimzed textures - no channel compositing

            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE] = new GeometrySphere(m_Context.getResources().openRawResource(R.raw.brainmine));
            tShaderProgramID_Geometry = GLES20Helper.createShader(R.raw.normalmapping_geometry_vertexshader, R.raw.normalmapping_geometry_fragmentshader, m_Context);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE].Set_ShaderID(tShaderProgramID_Geometry); //implicitly binds on the standard attribute locations for position, texture coords and modelviewprojection matrix
            tDiffuseTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.brainminediffusemap, this.m_Context);
            tNormalTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.brainminenormalmap, this.m_Context);
            tSpecularIntensityTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.brainminespecularintesitymap, this.m_Context);
            tSpecularColorTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.brainminespecularcolormap, this.m_Context);
            tAmbientOcclusionTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.brainmineambientocclusionmap, this.m_Context);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE].Set_DiffuseTextureID(tDiffuseTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE].Set_NormalTextureID(tNormalTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE].Set_SpecularIntensityTextureID(tSpecularIntensityTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE].Set_SpecularColorTextureID(tSpecularColorTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE].Set_AmbientOcclusionTextureID(tAmbientOcclusionTextureId);

            /*
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_VIRUSMINE] = new GeometrySphere(m_Context.getResources().openRawResource(R.raw.virusmine));
            tShaderProgramID_Geometry = GLES20Helper.createShader(R.raw.normalmapping_geometry_vertexshader, R.raw.normalmapping_geometry_fragmentshader, m_Context);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_VIRUSMINE].Set_ShaderID(tShaderProgramID_Geometry); //implicitly binds on the standard attribute locations for position, texture coords and modelviewprojection matrix
            tDiffuseTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.virusminediffusemap, this.m_Context);
            tNormalTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.virusminenormalmap, this.m_Context);
            tSpecularIntensityTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.virusminespecularintesitymap, this.m_Context);
            tSpecularColorTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.virusminespecularcolormap, this.m_Context);
            tAmbientOcclusionTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.virusmineambientocclusionmap, this.m_Context);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_VIRUSMINE].Set_DiffuseTextureID(tDiffuseTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_VIRUSMINE].Set_NormalTextureID(tNormalTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_VIRUSMINE].Set_SpecularIntensityTextureID(tSpecularIntensityTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_VIRUSMINE].Set_SpecularColorTextureID(tSpecularColorTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_VIRUSMINE].Set_AmbientOcclusionTextureID(tAmbientOcclusionTextureId);

            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_MATRIXMINE] = new GeometrySphere(m_Context.getResources().openRawResource(R.raw.matrixmine));
            tShaderProgramID_Geometry = GLES20Helper.createShader(R.raw.normalmapping_geometry_vertexshader, R.raw.normalmapping_geometry_fragmentshader, m_Context);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_MATRIXMINE].Set_ShaderID(tShaderProgramID_Geometry); //implicitly binds on the standard attribute locations for position, texture coords and modelviewprojection matrix
            tDiffuseTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.matrixminediffusemap, this.m_Context);
            tNormalTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.matrixminenormalmap, this.m_Context);
            tSpecularIntensityTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.matrixminespecularintesitymap, this.m_Context);
            tSpecularColorTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.matrixminespecularcolormap, this.m_Context);
            tAmbientOcclusionTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.matrixmineambientocclusionmap, this.m_Context);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_MATRIXMINE].Set_DiffuseTextureID(tDiffuseTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_MATRIXMINE].Set_NormalTextureID(tNormalTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_MATRIXMINE].Set_SpecularIntensityTextureID(tSpecularIntensityTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_MATRIXMINE].Set_SpecularColorTextureID(tSpecularColorTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_MATRIXMINE].Set_AmbientOcclusionTextureID(tAmbientOcclusionTextureId);
            */

            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE] = new GeometrySphere(m_Context.getResources().openRawResource(R.raw.intestines));
            tShaderProgramID_Geometry = GLES20Helper.createShader(R.raw.normalmapping_geometry_vertexshader, R.raw.normalmapping_geometry_fragmentshader, m_Context);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE].Set_ShaderID(tShaderProgramID_Geometry); //implicitly binds on the standard attribute locations for position, texture coords and modelviewprojection matrix
            tDiffuseTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.intestinesdiffusemap, this.m_Context);
            tNormalTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.intestinesnormalmap, this.m_Context);
            tSpecularIntensityTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.intestinesspecularintensitymap, this.m_Context);
            tSpecularColorTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.intestinesspecularcolormap, this.m_Context);
            tAmbientOcclusionTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.intestinesambientocclusionmap, this.m_Context);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE].Set_DiffuseTextureID(tDiffuseTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE].Set_NormalTextureID(tNormalTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE].Set_SpecularIntensityTextureID(tSpecularIntensityTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE].Set_SpecularColorTextureID(tSpecularColorTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE].Set_AmbientOcclusionTextureID(tAmbientOcclusionTextureId);

            /*
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_METABALL] = new GeometrySphere(m_Context.getResources().openRawResource(R.raw.metaball_intestines_object));
            tShaderProgramID_Geometry = GLES20Helper.createShader(R.raw.normalmapping_geometry_vertexshader, R.raw.normalmapping_geometry_fragmentshader, m_Context);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_METABALL].Set_ShaderID(tShaderProgramID_Geometry); //implicitly binds on the standard attribute locations for position, texture coords and modelviewprojection matrix
            tDiffuseTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.metaball_intestines_albedo, this.m_Context);
            tNormalTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.metaball_intestines_normal, this.m_Context);
            tSpecularIntensityTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.metaball_intestines_gloss, this.m_Context);
            tSpecularColorTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.metaball_intestines_specular, this.m_Context);
            tAmbientOcclusionTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.metaball_intestines_ao, this.m_Context);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_METABALL].Set_DiffuseTextureID(tDiffuseTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_METABALL].Set_NormalTextureID(tNormalTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_METABALL].Set_SpecularIntensityTextureID(tSpecularIntensityTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_METABALL].Set_SpecularColorTextureID(tSpecularColorTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_METABALL].Set_AmbientOcclusionTextureID(tAmbientOcclusionTextureId);
            */

        } else {
            //use optimized textures - composed channels to reduce texture unit load
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE] = new GeometrySphere(m_Context.getResources().openRawResource(R.raw.brainmine));
            tShaderProgramID_Geometry = GLES20Helper.createShader(R.raw.opt_normalmapping_geometry_vertexshader, R.raw.opt_normalmapping_geometry_fragmentshader, m_Context);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE].Set_ShaderID(tShaderProgramID_Geometry); //implicitly binds on the standard attribute locations for position, texture coords and modelviewprojection matrix
            tCombinedDiffuseAndAmbientOcclusionTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.opt_brainminecombineddiffuseambientocclusion, this.m_Context);
            tCombinedSpecularColorAndSpecularIntensityTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.opt_brainminecombinedspecularcolorspecularintensitymap, this.m_Context);
            tCombinedNormalTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.brainminenormalmap, this.m_Context);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE].Set_CombinedDiffuseAndAmbientOcclusionTextureID(tCombinedDiffuseAndAmbientOcclusionTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE].Set_CombinedSpecularColorAndSpecularIntensityTextureID(tCombinedSpecularColorAndSpecularIntensityTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE].Set_CombinedNormalTextureID(tCombinedNormalTextureId);

            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE] = new GeometrySphere(m_Context.getResources().openRawResource(R.raw.intestines));
            tShaderProgramID_Geometry = GLES20Helper.createShader(R.raw.opt_normalmapping_geometry_vertexshader, R.raw.opt_normalmapping_geometry_fragmentshader, m_Context);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE].Set_ShaderID(tShaderProgramID_Geometry); //implicitly binds on the standard attribute locations for position, texture coords and modelviewprojection matrix
            tCombinedDiffuseAndAmbientOcclusionTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.opt_intestinescombineddiffuseambientocclusion, this.m_Context);
            tCombinedSpecularColorAndSpecularIntensityTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.opt_intestinescombinedspecularcolorspecularintensity, this.m_Context);
            tCombinedNormalTextureId = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.intestinesnormalmap, this.m_Context);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE].Set_CombinedDiffuseAndAmbientOcclusionTextureID(tCombinedDiffuseAndAmbientOcclusionTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE].Set_CombinedSpecularColorAndSpecularIntensityTextureID(tCombinedSpecularColorAndSpecularIntensityTextureId);
            m_Geometries[IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE].Set_CombinedNormalTextureID(tCombinedNormalTextureId);
        }

        //setup fullscreen quad FBO rendering for the scene renderings
        m_FBO_BlurredMainScene_ColorAttachment_TextureID = GLES20Helper.generateFrameBufferColorAttachmentTexture(this.m_FBO_BlurredMainScene_Width, this.m_FBO_BlurredMainScene_Height);
        m_FBO_BlurredMainScene_DepthRenderBuffer_ID = GLES20Helper.generateFrameBufferDepthAttachmentRenderBuffer(this.m_FBO_BlurredMainScene_Width, this.m_FBO_BlurredMainScene_Height);
        m_FBO_BlurredMainScene_ID = GLES20Helper.generateFrameBufferID();

        m_FBO_MainScene_ColorAttachment_TextureID = GLES20Helper.generateFrameBufferColorAttachmentTexture(this.m_FBO_MainScene_Width, this.m_FBO_MainScene_Height);
        m_FBO_MainScene_DepthRenderBuffer_ID = GLES20Helper.generateFrameBufferDepthAttachmentRenderBuffer(this.m_FBO_MainScene_Width, this.m_FBO_MainScene_Height);
        m_FBO_MainScene_ID = GLES20Helper.generateFrameBufferID();

        m_GeometryQuad_FBO_BlurredMainScene = new GeometryQuad();
        int tShaderProgramID_Quad_BlurredMainScene = GLES20Helper.createShader(R.raw.quad_geometry_blurredmainscene_vertexshader, R.raw.quad_geometry_blurredmainscene_fragmentshader, m_Context);
        m_GeometryQuad_FBO_BlurredMainScene.Set_ShaderID(tShaderProgramID_Quad_BlurredMainScene); //implicitly binds on the standard attribute locations for position, texture coords and modelviewprojection matrix
        m_GeometryQuad_FBO_BlurredMainScene.Set_DiffuseTextureID(m_FBO_MainScene_ColorAttachment_TextureID);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap tHUDBitmap = Bitmap.createBitmap(m_FBO_MainScene_HUD_Width, m_FBO_MainScene_HUD_Height, conf);
        Canvas tHUDCanvas = new Canvas(tHUDBitmap);

        InputStream inputStream = m_Context.getResources().openRawResource(R.raw.hudcornerleftdown);
        Bitmap tHUDBitmap_CornerLeftDown = BitmapFactory.decodeStream(inputStream);
        inputStream = m_Context.getResources().openRawResource(R.raw.hudcornerleftup);
        Bitmap tHUDBitmap_CornerLeftUp = BitmapFactory.decodeStream(inputStream);
        inputStream = m_Context.getResources().openRawResource(R.raw.hudcornerrightup);
        Bitmap tHUDBitmap_CornerRightUp = BitmapFactory.decodeStream(inputStream);
        inputStream = m_Context.getResources().openRawResource(R.raw.hudcornerrightdown);
        Bitmap tHUDBitmap_CornerRightDown = BitmapFactory.decodeStream(inputStream);
        inputStream = m_Context.getResources().openRawResource(R.raw.hudcenter);
        Bitmap tHUDBitmap_Center = BitmapFactory.decodeStream(inputStream);

        tHUDCanvas.drawBitmap(tHUDBitmap_CornerLeftDown, 0.0f, 0.0f, new Paint());
        tHUDCanvas.drawBitmap(tHUDBitmap_CornerLeftUp, 0.0f, m_FBO_MainScene_HUD_Height - 512.0f, new Paint());
        tHUDCanvas.drawBitmap(tHUDBitmap_CornerRightUp, m_FBO_MainScene_HUD_Width - 512.0f, m_FBO_MainScene_HUD_Height - 512.0f, new Paint());
        tHUDCanvas.drawBitmap(tHUDBitmap_CornerRightDown, m_FBO_MainScene_HUD_Width - 512.0f, 0.0f, new Paint());
        tHUDCanvas.drawBitmap(tHUDBitmap_Center, (m_FBO_MainScene_HUD_Width - 512.0f) / 2.0f, (m_FBO_MainScene_HUD_Height - 512.0f) / 2.0f, new Paint());
        int tHUDTextureID = GLES20Helper.copyBitmapAndCreateTextureID(tHUDBitmap, m_Context);

        m_GeometryQuad_FBO_MainScene = new GeometryQuad();
        int tShaderProgramID_Quad_MainScene = GLES20Helper.createShader(R.raw.quad_geometry_mainscene_vertexshader, R.raw.quad_geometry_mainscene_fragmentshader, m_Context);
        m_GeometryQuad_FBO_MainScene.Set_ShaderID(tShaderProgramID_Quad_MainScene); //implicitly binds on the standard attribute locations for position, texture coords and modelviewprojection matrix
        m_GeometryQuad_FBO_MainScene.Set_GenericTexture0TextureID(m_FBO_MainScene_ColorAttachment_TextureID);

        //int tHUDTextureID = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.hud, this.m_Context);
        m_GeometryQuad_FBO_MainScene.Set_GenericTexture1TextureID(tHUDTextureID);

        //int tHUDCenterTextureID = GLES20Helper.loadBitmapResourceAndCreateTextureID(R.raw.hudcenter, this.m_Context);
        //m_GeometryQuad_FBO_MainScene.Set_GenericTexture2TextureID(tHUDCenterTextureID);
        m_GeometryQuad_FBO_MainScene.Set_ScreenResolution(new float[]{m_FrontBuffer_Width, m_FrontBuffer_Height});

//!!!needs second texture uniform for the blurred mainscene
//      m_GeometryQuad_FBO_MainScene.Set_GenericTexture1TextureID(m_FBO_MainSceneBlurred_ColorAttachment_TextureID);

        Thread serverGeometryThread = new Thread((Runnable)m_gameController.GetGeometryInformation());
        serverGeometryThread.setPriority(Thread.MAX_PRIORITY);
        serverGeometryThread.start();

        Thread serverClientThread = new Thread((Runnable)m_gameController.GetClientInformation());
        serverClientThread.setPriority(Thread.MAX_PRIORITY);
        serverClientThread.start();

        m_HUDFontBitmap = Bitmap.createBitmap(m_FBO_MainScene_HUD_Width, m_FBO_MainScene_HUD_Height, Bitmap.Config.ARGB_8888);
        m_HUDFontCanvas = new Canvas(m_HUDFontBitmap);
        m_HUDTypeface = Typeface.createFromAsset(m_Context.getAssets(), "fonts/hudfont.ttf");

        m_StartTimeInMillis = System.currentTimeMillis();
    }

    public void render() {
        //this will be called every frame to synchronize the object state
        long startTime = SystemClock.elapsedRealtime();
        m_gameController.GetGeometryInformation().SynchronizeState();
        long endTime = SystemClock.elapsedRealtime();

        //render hud font texture ...
        Paint clearPaint = new Paint();
        clearPaint.setColor(Color.BLACK);
        m_HUDFontCanvas.drawRect(0, 0, m_FBO_MainScene_HUD_Width, m_FBO_MainScene_HUD_Height, clearPaint);
        Paint tPaint = new Paint();
        tPaint.setColor(Color.WHITE);
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setTypeface(m_HUDTypeface);
        //tPaint.setFakeBoldText(true);
        tPaint.setTextSize(32);
        m_HUDFontCanvas.scale(1f, -1.0f);
        m_HUDFontCanvas.drawText("SCORE:" + m_gameController.GetClientInformation().GetUserScore(), 50, -m_FBO_MainScene_HUD_Height + 95, tPaint);
        m_HUDFontCanvas.drawText("HITPOINTS:" + m_gameController.GetClientInformation().GetUserHitpoints(), 50, -70, tPaint);
        int tHUDTextureID = GLES20Helper.copyBitmapAndCreateTextureID(m_HUDFontBitmap, m_Context);
        m_GeometryQuad_FBO_MainScene.Set_GenericTexture2TextureID(tHUDTextureID);

        //prepare to render into FBO
        if (!DEBUG_NOFBO_DIRECTRENDERING) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, m_FBO_MainScene_ID);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, m_FBO_MainScene_ColorAttachment_TextureID, 0);
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, m_FBO_MainScene_DepthRenderBuffer_ID);
            GLES20Helper.CheckFramebufferObjectStatus();
        }

        //--- SCENE RENDERING BEGINNING ------------------------------------------------------------
        m_gameController.GetCameraInformation().Update();
        m_gameController.GetCameraInformation().GenerateCameraMatrix(
                //float eyeY, float eyeZ
                0, 0, 0,
                //float centerX, float centerY, float centerZ
                0f, 0f, 0.1f,
                //int inViewportWidth, int inViewportHeight
                m_FBO_MainScene_Width, m_FBO_MainScene_Height
        );

        //GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        GLES20.glClearColor(0.0f, 0.3f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        int tLastObjectModelIdentification = -1;
        boolean tSkipUnbinding = true;

        if (m_gameController.GetGeometryInformation().GetNumberOfObjects() > 0) {
            for (int i = 0; i < m_gameController.GetGeometryInformation().GetNumberOfObjects(); i++) {
                int tCurrentObjectModelIdentification = m_gameController.GetGeometryInformation().GetObjectModelIdentification(i);
                //test for model/shader change and eventually bind new - or skip and safe rebinding and increase performance
                if (tLastObjectModelIdentification != tCurrentObjectModelIdentification) {
                    if (!tSkipUnbinding) {
                        m_Geometries[tLastObjectModelIdentification].UnUseShaderAndUnBindVBO();
                    }
                    tSkipUnbinding = false;
                    m_Geometries[tCurrentObjectModelIdentification].UseShaderAndBindVBO();
                    tLastObjectModelIdentification = tCurrentObjectModelIdentification;
                }
                GLES20.glUniformMatrix4fv(m_Geometries[tCurrentObjectModelIdentification].Get_ModelViewProjection_ID(), 1, false, m_gameController.GetCameraInformation().GetModelViewProjectionMatrix(), 0);
                GLES20.glUniformMatrix4fv(m_Geometries[tCurrentObjectModelIdentification].Get_ModelView_ID(), 1, false, m_gameController.GetCameraInformation().GetModelViewMatrix(), 0);
                GLES20.glUniform3f(
                        m_Geometries[tCurrentObjectModelIdentification].Get_ObjectPosition_ID(),
                        m_gameController.GetGeometryInformation().GetObjectXPosition(i),
                        m_gameController.GetGeometryInformation().GetObjectYPosition(i),
                        m_gameController.GetGeometryInformation().GetObjectZPosition(i)
                );
                GLES20.glUniform3f(
                        m_Geometries[tCurrentObjectModelIdentification].Get_ObjectRotation_ID(),
                        m_gameController.GetGeometryInformation().GetObjectXRotation(i),
                        m_gameController.GetGeometryInformation().GetObjectYRotation(i),
                        m_gameController.GetGeometryInformation().GetObjectZRotation(i)
                );
                GLES20.glUniform3f(
                        m_Geometries[tCurrentObjectModelIdentification].Get_ObjectScaling_ID(),
                        m_gameController.GetGeometryInformation().GetObjectXScaling(i),
                        m_gameController.GetGeometryInformation().GetObjectYScaling(i),
                        m_gameController.GetGeometryInformation().GetObjectZScaling(i)
                );

                if (DEBUG_NOTEXTUREOPTIMIZATION) {
                    m_Geometries[tCurrentObjectModelIdentification].BindTexturesAndDrawGeometry();
                } else {
                    m_Geometries[tCurrentObjectModelIdentification].BindCombinedTexturesAndDrawGeometry();
                }

            }
            m_Geometries[tLastObjectModelIdentification].UnUseShaderAndUnBindVBO();
        }

        //--- SCENE RENDERING FINISHED -------------------------------------------------------------

        if (!DEBUG_NOFBO_DIRECTRENDERING) {
            //un-bind the frame buffer object and render to the screen frame buffer
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

            //--- FULLSCREEN QUAD FBO RENDERING (ORTHOGRAPHIC PROJECTION) ------------------------------

            GLES20.glClearColor(0.0f, 0.5f, 0.3f, 1.0f);
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            m_GeometryQuad_FBO_MainScene.UseShaderAndBindVBO();
            GLES20.glViewport(0, 0, (int)(m_FBO_MainScene_Width * (1.0f/ m_FBO_Mainscene_NativeResolutionFactor)), (int)(m_FBO_MainScene_Height * (1.0f/ m_FBO_Mainscene_NativeResolutionFactor)));

            float[] tProjectionMatrix = new float[16];
            float[] tModelViewProjectionMatrix = new float[16];
            float[] tModelViewMatrix = new float[16];

            //http://www.songho.ca/opengl/gl_projectionmatrix.html
            //Matrix.orthoM(tProjectionMatrix, 0, -1, 1, -1, 1, -1, 10);
            Matrix.orthoM(tProjectionMatrix, 0, -1, 1, -1, 1, -1, 10);
            //orthoM(float[] m, int mOffset, float left, float right, float bottom, float top, float near, float far)
            //Matrix.orthoM(tProjectionMatrix, 0, 0, this.m_FBO_MainScene_Width, 0, this.m_FBO_MainScene_Height, -1, 10);
            Matrix.setIdentityM(tModelViewMatrix, 0);
            //create model view projection matrix
            Matrix.multiplyMM(tModelViewProjectionMatrix, 0, tProjectionMatrix, 0, tModelViewMatrix, 0);
            //upload the ModelViewProjection matrix to the bound program
            GLES20.glUniformMatrix4fv(m_GeometryQuad_FBO_MainScene.Get_ModelViewProjection_ID(), 1, false, tModelViewProjectionMatrix, 0);
            GLES20.glUniform2fv(m_GeometryQuad_FBO_MainScene.Get_ScreenResolution_ID(), 1, m_GeometryQuad_FBO_MainScene.Get_ScreenResolution(), 0);

            m_GeometryQuad_FBO_MainScene.Set_Time((float)(System.currentTimeMillis()-m_StartTimeInMillis)/1000.0f);
            GLES20.glUniform1f(m_GeometryQuad_FBO_MainScene.Get_Time_ID(), m_GeometryQuad_FBO_MainScene.Get_Time());

            m_GeometryQuad_FBO_MainScene.BindGenericTexturesAndDrawGeometry();
            m_GeometryQuad_FBO_MainScene.UnUseShaderAndUnBindVBO();
        }

        GLES20Helper.deleteTextureID(tHUDTextureID);

        //Log.d("GLES20Content.render()", "SynchronizeState() took "+(endTime-startTime)+" ms");
        m_gameController.GetGeometryInformation().SwapState();
    }
}
