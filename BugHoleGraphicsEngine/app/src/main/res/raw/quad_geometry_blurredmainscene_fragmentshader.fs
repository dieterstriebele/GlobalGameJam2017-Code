precision mediump float;

varying vec2 vTextureCoordinate;
//varying vec4 vNormal;
uniform sampler2D sTextureSampler;

const float blurSize = 1.0/256.0;
const float intensity = 0.35;

void main(){

    vec4 sum = vec4(0);
    //http://www.gamerendering.com/2008/10/11/gaussian-blur-filter-shader/ for the
    //blur in x (horizontal)
    //take nine samples, with the distance blurSize between them
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x - 4.0*blurSize, vTextureCoordinate.y)) * 0.05;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x - 3.0*blurSize, vTextureCoordinate.y)) * 0.09;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x - 2.0*blurSize, vTextureCoordinate.y)) * 0.12;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x - blurSize, vTextureCoordinate.y)) * 0.15;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x, vTextureCoordinate.y)) * 0.16;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x + blurSize, vTextureCoordinate.y)) * 0.15;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x + 2.0*blurSize, vTextureCoordinate.y)) * 0.12;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x + 3.0*blurSize, vTextureCoordinate.y)) * 0.09;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x + 4.0*blurSize, vTextureCoordinate.y)) * 0.05;
    //blur in y (vertical)
    //take nine samples, with the distance blurSize between them
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x, vTextureCoordinate.y - 4.0*blurSize)) * 0.05;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x, vTextureCoordinate.y - 3.0*blurSize)) * 0.09;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x, vTextureCoordinate.y - 2.0*blurSize)) * 0.12;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x, vTextureCoordinate.y - blurSize)) * 0.15;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x, vTextureCoordinate.y)) * 0.16;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x, vTextureCoordinate.y + blurSize)) * 0.15;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x, vTextureCoordinate.y + 2.0*blurSize)) * 0.12;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x, vTextureCoordinate.y + 3.0*blurSize)) * 0.09;
    sum += texture2D(sTextureSampler, vec2(vTextureCoordinate.x, vTextureCoordinate.y + 4.0*blurSize)) * 0.05;

    //vec3 normalColor = vNormal.x+vNormal+y+vNormal.z;

    gl_FragColor = sum*intensity + texture2D(sTextureSampler, vTextureCoordinate) ;

    //show texture coordinates
    //gl_FragColor = vec4(vTextureCoordinate.x,vTextureCoordinate.y,0.0,1.0);
    //gl_FragColor = vec4(1.0,0.0,1.0,1.0);
    //gl_FragColor = texture2D(sTextureSampler, vTextureCoordinate);
    //gl_FragColor = texture2D(sTextureSampler, vTextureCoordinate, 1.0);
}