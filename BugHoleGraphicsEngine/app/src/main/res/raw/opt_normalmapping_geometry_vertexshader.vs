uniform mat4 uModelViewProjection;
uniform mat4 uModelView;
uniform vec3 uObjectPosition;
uniform vec3 uObjectRotation;
uniform vec3 uObjectScaling;

attribute vec4 aPosition;
attribute vec2 aTextureCoordinate;
attribute vec3 aNormal;

varying vec2 vTextureCoordinate;
varying vec3 vNormal;
varying vec3 vPosition;

mat4 translationMatrix(vec3 translation)
{
    return mat4(
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        translation.x, translation.y, translation.z, 1);
}

mat4 scalingMatrix(vec3 scaling)
{
    return mat4(
        scaling.x, 0, 0, 0,
        0, scaling.y, 0, 0,
        0, 0, scaling.z, 0,
        0, 0, 0, 1);
}

mat4 rotationMatrix(vec3 axis, float angle)
{
    axis = normalize(axis);
    float s = sin(angle);
    float c = cos(angle);
    float oc = 1.0 - c;
    return mat4(oc * axis.x * axis.x + c,          oc * axis.x * axis.y + axis.z * s, oc * axis.z * axis.x - axis.y * s, 0.0,
                oc * axis.x * axis.y - axis.z * s, oc * axis.y * axis.y + c,          oc * axis.y * axis.z + axis.x * s, 0.0,
                oc * axis.z * axis.x + axis.y * s, oc * axis.y * axis.z - axis.x * s, oc * axis.z * axis.z + c,          0.0,
                0.0, 0.0, 0.0, 1.0);
}

void main(){
    vTextureCoordinate = aTextureCoordinate;

    //vec4 tPosition = vec4(aPosition.x * uObjectScaling.x, aPosition.y * uObjectScaling.y, aPosition.z * uObjectScaling.z, aPosition.w);  // * scalingMatrix(uObjectScaling);
    vec4 tPosition = aPosition * scalingMatrix(uObjectScaling);

    mat4 matrix = translationMatrix(uObjectPosition);
    matrix = matrix * rotationMatrix(vec3(1.0, 0.0, 0.0), uObjectRotation.x);
    matrix = matrix * rotationMatrix(vec3(0.0, 1.0, 0.0), uObjectRotation.y);
    matrix = matrix * rotationMatrix(vec3(0.0, 0.0, 1.0), uObjectRotation.z);

    // Transform the vertex into eye space.
    vPosition = vec3((uModelView*matrix) * tPosition);

    // Transform the normal's orientation into eye space.
    vNormal = vec3((uModelView*matrix) * vec4(aNormal, 0.0));

    gl_Position = (uModelViewProjection * matrix) * tPosition;
}