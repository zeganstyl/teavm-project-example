package com.example

import org.intellij.lang.annotations.Language
import org.ksdfv.thelema.APP
import org.ksdfv.thelema.g3d.ActiveCamera
import org.ksdfv.thelema.g3d.cam.OrbitCameraControl
import org.ksdfv.thelema.gl.GL
import org.ksdfv.thelema.gl.GL_COLOR_BUFFER_BIT
import org.ksdfv.thelema.gl.GL_DEPTH_BUFFER_BIT
import org.ksdfv.thelema.mesh.build.BoxMeshBuilder
import org.ksdfv.thelema.shader.Shader
import org.ksdfv.thelema.teavm.TeaVMApp

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        TeaVMApp {
            @Language("GLSL")
            val shader = Shader(
                vertCode = """
attribute vec3 aPosition;
varying vec3 vPosition;
uniform mat4 viewProj;

void main() {
    vPosition = aPosition;
    gl_Position = viewProj * vec4(aPosition, 1.0);
}""",
                fragCode = """
varying vec3 vPosition;

void main() {
    gl_FragColor = vec4(vPosition, 1.0);
}"""
            )

            val box = BoxMeshBuilder().build()

            val control = OrbitCameraControl()
            control.listenToMouse()

            GL.glClearColor(0f, 0f, 0f, 1f)

            GL.isDepthTestEnabled = true

            GL.render {
                GL.glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

                control.update(APP.deltaTime)
                ActiveCamera.update()

                shader.bind()
                shader["viewProj"] = ActiveCamera.viewProjectionMatrix
                box.render(shader)
            }
        }
    }
}