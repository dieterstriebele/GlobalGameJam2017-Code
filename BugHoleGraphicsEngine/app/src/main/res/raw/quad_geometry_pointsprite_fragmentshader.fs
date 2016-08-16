precision highp float;

varying vec2 vTextureCoordinate;
//varying vec4 vNormal;
//uniform sampler2D sTextureSampler;

uniform sampler2D sGenericTextureSampler0;
uniform sampler2D sGenericTextureSampler1;
uniform sampler2D sGenericTextureSampler2;
uniform sampler2D sGenericTextureSampler3;

uniform vec2 uScreenResolution;
uniform float uTime;

void main(){
    vec4 pointsprite = texture2D(sGenericTextureSampler0, vTextureCoordinate);
    gl_FragColor = pointsprite;
}