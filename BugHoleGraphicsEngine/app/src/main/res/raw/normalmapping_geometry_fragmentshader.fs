precision highp float;

varying vec2 vTextureCoordinate;
varying vec3 vNormal;
varying vec3 vPosition;

uniform sampler2D sDiffuseSampler;
uniform sampler2D sNormalSampler;
uniform sampler2D sSpecularIntensitySampler;
uniform sampler2D sAmbientOcclusionSampler;
uniform sampler2D sSpecularColorSampler;

void main(){
    // Position of the light in eye space.
    const vec3 cLightPos = vec3(0.0,0.0,-1.0);

    // Color of the specular (only needed for colored metals)
    vec3 cSpecularColor = texture2D(sSpecularColorSampler, vTextureCoordinate).rgb;

    // Will be used for attenuation.
    float distance = abs(length(cLightPos - vPosition));

    // Increase/decrease normal perturbation
    const float maxVariance = 2.0;
    const float minVariance = maxVariance / 2.0;
    // Create a normal which is our standard normal + the normal map perturbation (which is going to be either positive or negative)
    vec3 normalAdjusted = vNormal + normalize(texture2D(sNormalSampler, vTextureCoordinate).rgb * maxVariance - minVariance);
    normalAdjusted = clamp(normalAdjusted, -1.0, 1.0);

    //const float vertexNormalWeight = 2.0;
    //const float mappedNormalWeight = 2.0;
    //vec3 normalAdjusted = (vNormal * vertexNormalWeight) + (normalize(texture2D(sNormalSampler, vTextureCoordinate).rgb) * mappedNormalWeight);

    // Get a lighting direction vector from the light to the vertex.
    vec3 lightVector = normalize(cLightPos - vPosition);

    // Calculate the dot product of the light vectoxr and vertex normal. If the normal and light vector are
    // pointing in the same direction then it will get max illumination.
    // !!! NORMALMAP IS CURRENTLY ONLY APPLIED TO SPECULAR COMPONENT, NOT DIFFUSE
	float diffuseIntensity = max(0.0, dot(normalize(normalAdjusted), normalize(lightVector)));

    //!!! PERFORMANCE OPTIMIZATION: MERGE AMBIENT OCCLUSION INTENSITY CHANNEL INTO NORMAL ALPHA
    float ambientIntensity = texture2D(sAmbientOcclusionSampler, vTextureCoordinate).r;
    float ambientBaseIntensity = 0.05;

    diffuseIntensity = (diffuseIntensity * ambientIntensity) + ambientBaseIntensity;
    diffuseIntensity = clamp(diffuseIntensity, 0.0, 1.0);

    // Add attenuation.
    diffuseIntensity = diffuseIntensity * (1.0 / (1.0 + (0.0125 * distance * distance)));
    diffuseIntensity = clamp(diffuseIntensity, 0.0, 1.0);


    // Diffuse texture map
    vec4 diffuseTexture = texture2D(sDiffuseSampler, vTextureCoordinate);

	// Add the diffuse contribution blended with the standard texture lookup and add in the ambient light on top
	vec3 color = diffuseIntensity * diffuseTexture.rgb;

	// Calc and apply specular contribution
	vec3 vReflection        = reflect(-normalize(lightVector),normalize(normalAdjusted));
	float specularIntensity = max(0.0, dot(normalize(normalAdjusted), vReflection));
    specularIntensity = clamp(specularIntensity, 0.0, 1.0);
    //specularIntensity = 1.0;

	//!!! PERFORMANCE OPTIMIZATION: MERGE SPECULAR INTENSITY CHANNEL INTO DIFFUSE ALPHA
	float specular = pow(specularIntensity, texture2D(sSpecularIntensitySampler, vTextureCoordinate).r * 128.0);
	color += vec3(specular * cSpecularColor) * diffuseIntensity;
    color = clamp(color, 0.0, 1.0);

    gl_FragColor = vec4(color, 1.0);
}