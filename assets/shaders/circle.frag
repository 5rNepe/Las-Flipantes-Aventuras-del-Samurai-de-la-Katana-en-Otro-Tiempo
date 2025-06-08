#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
uniform vec2 u_center;
uniform float u_radius;
uniform vec4 u_color;

void main() {
    float dist = distance(v_texCoords * vec2(640.0, 480.0), u_center); // Ajusta 640x480 al tamaño de tu pantalla
    float alpha = step(dist, u_radius);
    gl_FragColor = u_color * vec4(1.0, 1.0, 1.0, alpha);
}