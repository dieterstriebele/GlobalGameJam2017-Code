precision highp float;

varying vec2 vTextureCoordinate;
varying vec3 vNormal;
varying vec3 vPosition;

uniform sampler2D sTextureSampler;
uniform sampler2D sNormalSampler;
uniform sampler2D sSpecularIntensitySampler;
uniform sampler2D sAmbientOcclusionSampler;
uniform sampler2D sSpecularColorSampler;

void main(){
    //The position of the light in eye space.
    vec3 cLightPos = vec3(0.0,0.0,0.0);

    // Will be used for attenuation.
    float distance = length(cLightPos - vPosition);

    // Get a lighting direction vector from the light to the vertex.
    vec3 lightVector = normalize(cLightPos - vPosition);

    // Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
    // pointing in the same direction then it will get max illumination.
    float diffuse = max(dot(vNormal, lightVector), 0.1);

    // Add attenuation.
    diffuse = diffuse * (1.0 / (1.0 + (0.05 * distance * distance)));

    //calculate specular lighting
    float specularExp = 256.0;
    float NdotL = max(0.0, dot(vNormal, lightVector));
    vec3 H = normalize(lightVector + vec3(0.0, 0.0, 1.0));
    float NdotH = max(0.0, dot(vNormal, H));
    vec4 specular = vec4(0.0);
    if (NdotL > 0.0) {
        specular = vec4(pow(NdotH, specularExp));
    }

    // Multiply the color by the diffuse illumination level to get final output color.
    vec4 texture = texture2D(sTextureSampler, vTextureCoordinate);
    vec4 ambient = texture * 0.0125;
    gl_FragColor = ambient + (texture * diffuse) + specular;
}