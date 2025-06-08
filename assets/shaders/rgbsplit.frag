#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_time;
uniform float u_intensity;

void main() {
    vec2 uv = v_texCoords;
    
    // Separación RGB
    float offset = 0.01 * u_intensity;
    float r = texture2D(u_texture, uv + vec2(offset, 0.0)).r;
    float g = texture2D(u_texture, uv).g;
    float b = texture2D(u_texture, uv - vec2(offset, 0.0)).b;
    
    // Parpadeo aleatorio
    float flicker = 0.9 + 0.1 * sin(u_time * 10.0);
    
    gl_FragColor = vec4(r * flicker, g * flicker, b * flicker, v_color.a);
}