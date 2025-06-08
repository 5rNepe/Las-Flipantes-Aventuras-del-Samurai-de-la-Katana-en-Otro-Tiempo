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
    
    // Distorsión horizontal
    uv.x += sin(u_time * 5.0 + uv.y * 30.0) * 0.005 * u_intensity;
    
    // Efecto de desenfoque vertical
    float blur = 0.002 * u_intensity;
    vec4 color = texture2D(u_texture, uv);
    color += texture2D(u_texture, uv + vec2(0.0, blur));
    color += texture2D(u_texture, uv - vec2(0.0, blur));
    color /= 3.0;
    
    gl_FragColor = color * v_color;
}