import com.soywiz.klock.*
import com.soywiz.korge.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*

// NOTE: This API is experimental and you have to copy it from:
// https://github.com/korlibs/korge-samples/tree/master/sample-raw-3d
suspend fun main() = Korge {
    image(resourcesVfs["korge.png"].readNativeImage())

    scene3D {
        camera.set(fov = 45.degrees, near = 1.0, far = 20.0)

        val rotAxis = Vector3D(1f, 1f, 1f)
        val cube = box(1, 1, 1) {
            position(-.5, 0, -5)
            modelMat.setToRotation(0.degrees, rotAxis)
        }
        val cube2 = box(2, 1, 1) {
            position(+.5, 0, -5)
            modelMat.setToRotation(0.degrees, rotAxis)
        }
        launchImmediately {
            while (true) {
                tween(time = 4.seconds) {
                    cube.modelMat.setToRotation((it * 360).degrees, rotAxis)
                    cube2.modelMat.setToRotation(-(it * 360).degrees, rotAxis)
                }
            }
        }
    }

    image(resourcesVfs["korge.png"].readNativeImage()).position(700, 0).alpha(1)
}
