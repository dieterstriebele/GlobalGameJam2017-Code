precision highp float;

varying vec2 vTextureCoordinate;
//varying vec4 vNormal;
//uniform sampler2D sTextureSampler;

uniform sampler2D sGenericTextureSampler0; //mainscene
uniform sampler2D sGenericTextureSampler1; //hudcorners
uniform sampler2D sGenericTextureSampler2; //hudcenter
uniform sampler2D sGenericTextureSampler3;

uniform vec2 uScreenResolution;
uniform float uTime;

float rand(vec2 position) {
   return fract(sin(dot(position.xy ,vec2(12.9898,78.233))) * 43758.5453+(uTime*0.5));
}

vec3 blur(vec2 coords) {
   vec2 powers = pow(abs(vec2(coords.x - 0.5,coords.y - 0.5)),vec2(2));
   float noise = rand(coords.xy)*0.004*smoothstep(0.05,0.45,powers.x+powers.y)*5.5;
   vec2 xy1 = coords+noise;
   vec2 xy2 = coords-noise;
   return (
            (
             texture2D(sGenericTextureSampler0, xy1)+
             texture2D(sGenericTextureSampler0, xy2)+
             texture2D(sGenericTextureSampler0, vec2(xy1.x, xy2.y))+
             texture2D(sGenericTextureSampler0, vec2(xy2.x, xy1.y))
            )/4.
          ).rgb;
}

void main(){
    //vec4 mainscene = texture2D(sGenericTextureSampler0, vTextureCoordinate);
    vec4 hud = texture2D(sGenericTextureSampler1, vTextureCoordinate);
    vec4 text = texture2D(sGenericTextureSampler2, vTextureCoordinate);

    //text.rgb = text.rgb * vec3(0.8,1.0,0.9);

    hud = hud + text;

    //gl_FragColor = mainscene + hud;

    //vec2 screenResolution = vec2(2560.0,1800.0); //Google Pixel C
    vec2 screenResolution = uScreenResolution;
    vec2 hudResolution = vec2(1024.0, 1024.0);
    vec2 hudHalfResolution = hudResolution/2.0;

    vec2 fragmentCoords = gl_FragCoord.xy / hudResolution;

   vec2 position = gl_FragCoord.xy / screenResolution.xy;
   //cubic lens distortion
   vec2 rescale_term = 0.9*(position.xy-0.5);

   vec3 color = vec3(
      blur(1.0375*rescale_term+0.5).r,
      blur(1.025*rescale_term+0.5).g,
      blur(1.0125*rescale_term+0.5).b
   );

   //vec3 color = texture2D(sGenericTextureSampler0, vTextureCoordinate).rgb;
   //tvlines effect
   hud *= 0.8+0.2*sin(2.0*uTime+fragmentCoords.y*1000.0);
   //tvflicker effect
   hud *= 0.9+0.1*sin(uTime);

   gl_FragColor = vec4(color,1.0) + hud;
   //gl_FragColor = mainscene + hud;

}