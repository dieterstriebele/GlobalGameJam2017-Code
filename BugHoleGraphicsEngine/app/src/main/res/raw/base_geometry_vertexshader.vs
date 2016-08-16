uniform mat4 uModelViewProjection;
uniform vec3 uObjectPosition;
uniform vec3 uObjectRotation;

attribute vec4 aPosition;
attribute vec2 aTextureCoordinate;
//attribute vec3 aNormal;

varying vec2 vTextureCoordinate;

mat4 translationMatrix(vec3 translation)
{
    return mat4(
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        translation.x, translation.y, translation.z, 1);
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

    mat4 matrix = translationMatrix(uObjectPosition);

    matrix = matrix * rotationMatrix(vec3(1.0, 0.0, 0.0), uObjectRotation.x);
    matrix = matrix * rotationMatrix(vec3(0.0, 1.0, 0.0), uObjectRotation.y);
    matrix = matrix * rotationMatrix(vec3(0.0, 0.0, 1.0), uObjectRotation.z);

    matrix = uModelViewProjection * matrix;

    gl_Position = matrix * aPosition;
}