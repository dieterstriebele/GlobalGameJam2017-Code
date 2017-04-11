package ggj2k15.bughole.bugholegraphicsengine.gles20;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GLES20Helper {

    public static int generateTextureID() {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        Log.d("GLES20Helper.generateTextureID()", "Generated TextureID=" + textureHandle[0]);
        return textureHandle[0];
    }

    public static void deleteTextureID(int inTextureID) {
        Log.d("GLES20Helper.deleteTextureID()", "Deleting TextureID=" + inTextureID);
        final int[] textureHandle = new int[1];
        textureHandle[0] = inTextureID;
        GLES20.glDeleteTextures(1, textureHandle, 0);
    }

    public static int generateFrameBufferID() {
        final int[] framebufferHandle = new int[1];
        GLES20.glGenFramebuffers(1, framebufferHandle, 0);
        Log.d("GLES20Helper.generateFrameBufferID()", "Generated FramebufferID=" + framebufferHandle[0]);
        return framebufferHandle[0];
    }

    public static int generateFrameBufferColorAttachmentTexture(int inWidth, int inHeight) {
        int tFBO_ColorAttachment_TextureID = GLES20Helper.generateTextureID();
        // generate color texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tFBO_ColorAttachment_TextureID);
        // parameters
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, inWidth, inHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        return tFBO_ColorAttachment_TextureID;
    }

    public static int generateFrameBufferDepthAttachmentRenderBuffer(int inWidth, int inHeight) {
        int tFBO_DepthRenderBuffer_ID = GLES20Helper.generateRenderBufferID();
        // create render buffer and bind 16-bit depth buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, tFBO_DepthRenderBuffer_ID);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, inWidth, inHeight);
        return tFBO_DepthRenderBuffer_ID;
    }

    public static int generateRenderBufferID() {
        final int[] renderbufferHandle = new int[1];
        GLES20.glGenRenderbuffers(1, renderbufferHandle, 0);
        Log.d("GLES20Helper.generateRenderBufferID()", "Generated RenderbufferID=" + renderbufferHandle[0]);
        return renderbufferHandle[0];
    }

    public static int generateGenericBufferID() {
        final int[] bufferHandle = new int[1];
        GLES20.glGenBuffers(1, bufferHandle, 0);
        Log.d("GLES20Helper.generateGenericBufferID()", "Generated GenericBufferID=" + bufferHandle[0]);
        return bufferHandle[0];
    }

    public static String readResourceAsPlaintext(int inResourceID, Context inContext) {
        Log.d("GLES20Helper.readResourceAsPlaintext()", "Reading ResourceID=" + inResourceID);
        StringBuffer textfile = new StringBuffer();
        try {
            InputStream inputStream = inContext.getResources().openRawResource(inResourceID);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String read = in.readLine();
            while (read != null) {
                textfile.append(read + "\n");
                read = in.readLine();
            }
            textfile.deleteCharAt(textfile.length() - 1);
        } catch (Exception e) {
            Log.e("GLES20Helper.readResourceAsPlaintext()", "Could not read resource as plaintext: " + e.getMessage());
        }
        Log.d("GLES20Helper.readResourceAsPlaintext()", "Successfully read ResourceID=" + inResourceID + " Length=" + textfile.length());
        return textfile.toString();
    }

    public static int copyBitmapAndCreateTextureID(Bitmap inBitmap, Context inContext) {
        Log.d("GLES20Helper.copyBitmapAndCreateTextureID()", "Create the texture, setup its properties and upload it to OpenGL ES inBitmap=" + inBitmap);
        int tTextureId = GLES20Helper.generateTextureID();

        //bind the texture and setup its properties
        Log.d("GLES20Helper.copyBitmapAndCreateTextureID()", "Binding the texture and setup its properties ...");
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tTextureId);
        //GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        //upload the texture to OpenGL ES
        Log.d("GLES20Helper.copyBitmapAndCreateTextureID()", "Upload the texture to the GPU ...");
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, inBitmap, 0);
        //GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        //mark the bitmap for deletion
        //Log.d("GLES20Helper.copyBitmapAndCreateTextureID()", "Mark the bitmap resource on CPU memory for deletion ...");
        //inBitmap.recycle();

        Log.d("GLES20Helper.copyBitmapAndCreateTextureID()", "Returning textureID="+tTextureId+" with bound and uploaded inBitmap=" + inBitmap);
        return tTextureId;
    }

    public static int loadBitmapResourceAndCreateTextureID(int inResourceID, Context inContext) {
        Log.d("GLES20Helper.loadBitmapResourceAndCreateTextureID()", "Create the texture, setup its properties and upload it to OpenGL ES ResourceID=" + inResourceID);
        int tTextureId = GLES20Helper.generateTextureID();

        //bind the texture and setup its properties
        Log.d("GLES20Helper.loadBitmapResourceAndCreateTextureID()", "Binding the texture and setup its properties ...");
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tTextureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        //load the resource as a bitmap
        Log.d("GLES20Helper.loadBitmapResourceAndCreateTextureID()", "Loading the resource as a bitmap in CPU memory...");
        InputStream inputStream = inContext.getResources().openRawResource(inResourceID);
        Bitmap bitmapTexture = BitmapFactory.decodeStream(inputStream);

        //upload the texture to OpenGL ES
        Log.d("GLES20Helper.loadBitmapResourceAndCreateTextureID()", "Upload the texture to the GPU ...");
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapTexture, 0);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        //mark the bitmap for deletion
        Log.d("GLES20Helper.loadBitmapResourceAndCreateTextureID()", "Mark the bitmap resource on CPU memory for deletion ...");
        bitmapTexture.recycle();

        Log.d("GLES20Helper.loadBitmapResourceAndCreateTextureID()", "Returning textureID="+tTextureId+" with bound and uploaded ResourceID=" + inResourceID);
        return tTextureId;
    }

    public static void CheckFramebufferObjectStatus() {
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e("GLES20Helper.CheckFramebufferObjectStatus()", "Framebuffer status invalid! !=GLES20.GL_FRAMEBUFFER_COMPLETE");
        }
    }

    public static void CheckShaderCompileStatus(int inShaderID) {
        int[] tCompiled = new int[1];
        GLES20.glGetShaderiv(inShaderID, GLES20.GL_COMPILE_STATUS, tCompiled, 0);
        if (tCompiled[0] == 0) {
            Log.e("GLES20Helper.CheckShaderCompileStatus()", "Could not compile shader! ShaderInfoLog='" + GLES20.glGetShaderInfoLog(inShaderID)+"'");
        }
        CheckGlError();
    }

    public static int createShader(int inVertexShaderID, int inFragmentShaderID, Context inContext) {
        Log.d("GLES20Helper.createShader()", "Creating shader VertexShaderID=" + inVertexShaderID + " FragmentShaderID=" + inFragmentShaderID);
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        String tVertexShaderSource = GLES20Helper.readResourceAsPlaintext(inVertexShaderID, inContext);
        GLES20.glShaderSource(vertexShader, tVertexShaderSource);
        GLES20.glCompileShader(vertexShader);
        CheckShaderCompileStatus(vertexShader);
        Log.d("GLES20Helper.createShader()", "Compiled vertex shader!");

        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        String tFragmentShaderSource = GLES20Helper.readResourceAsPlaintext(inFragmentShaderID, inContext);
        GLES20.glShaderSource(fragmentShader, tFragmentShaderSource);
        GLES20.glCompileShader(fragmentShader);
        CheckShaderCompileStatus(fragmentShader);
        Log.d("GLES20Helper.createShader()", "Compiled fragment shader!");

        int tShaderProgramID = GLES20.glCreateProgram();
        GLES20.glAttachShader(tShaderProgramID, vertexShader);
        GLES20.glAttachShader(tShaderProgramID, fragmentShader);
        GLES20.glLinkProgram(tShaderProgramID);
        CheckGlError();

        Log.d("GLES20Helper.createShader()", "Linked fragment+vertex shader! ShaderInfoLog:'" + GLES20.glGetShaderInfoLog(tShaderProgramID)+"'");
        return tShaderProgramID;
    }

    public static void CheckGlError() {
        int tErrorCode;
        while ((tErrorCode = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("GLES20Helper.CheckGlError", "" + tErrorCode);
            //throw new RuntimeException("GLES20Helper.CheckGlError" + tErrorCode);
        }
    }

}
