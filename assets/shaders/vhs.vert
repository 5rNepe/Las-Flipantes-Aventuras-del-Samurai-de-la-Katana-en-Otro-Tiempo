attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform float u_time;
uniform float u_intensity;

varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    v_color = a_color;
    v_texCoords = a_texCoord0;
    
    // Pequeña distorsión vertical basada en el tiempo
    float distortion = sin(u_time * 10.0 + a_position.y * 50.0) * 0.002 * u_intensity;
    gl_Position = u_projTrans * vec4(a_position.x + distortion, a_position.y, a_position.z, a_position.w);
}