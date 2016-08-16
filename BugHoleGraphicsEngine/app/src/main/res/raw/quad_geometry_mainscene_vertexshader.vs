uniform mat4 uModelViewProjection;
uniform mat4 uModelView;

attribute vec4 aPosition;
attribute vec2 aTextureCoordinate;
//attribute vec4 aNormal;

varying vec2 vTextureCoordinate;
//varying vec4 vNormal;

void main(){
    vTextureCoordinate = aTextureCoordinate;
    //vNormal = aNormal;
    gl_Position = uModelViewProjection * aPosition;
}