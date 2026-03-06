package iut.dagere.tache_pistache.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.drawable.toBitmap
import iut.dagere.tache_pistache.R
import kotlin.math.sin
import kotlin.random.Random

/**
 * Overlay de pistaches qui tombent, affiché par-dessus le contenu. Chaque pistache a un intérieur
 * vert différent.
 *
 * @param isVisible contrôle si l'animation est affichée
 * @param onFinished callback appelé quand l'animation est terminée
 */
@Composable
fun ConfettiOverlay(isVisible: Boolean, onFinished: () -> Unit = {}) {
    if (!isVisible) return

    // Charger les 8 variantes de pistache (couleurs pétantes)
    val context = LocalContext.current
    val pistachioBitmaps: List<ImageBitmap> = remember {
        listOf(
                        R.drawable.ic_pistachio, // vert clair (original)
                        R.drawable.ic_pistachio_rouge, // rouge
                        R.drawable.ic_pistachio_jaune, // jaune
                        R.drawable.ic_pistachio_vert_foret, // vert forêt
                        R.drawable.ic_pistachio_bleu, // bleu
                        R.drawable.ic_pistachio_rose, // rose
                        R.drawable.ic_pistachio_violet, // violet
                        R.drawable.ic_pistachio_orange // orange
                )
                .map { resId ->
                    val drawable = context.resources.getDrawable(resId, context.theme)
                    drawable.toBitmap(64, 64).asImageBitmap()
                }
    }

    // Générer les particules de pistaches
    val particles = remember {
        List(30) {
            PistachioParticle(
                    x = Random.nextFloat(),
                    startY = Random.nextFloat() * -0.4f,
                    speed = 0.3f + Random.nextFloat() * 0.6f,
                    amplitude = 25f + Random.nextFloat() * 50f,
                    frequency = 1f + Random.nextFloat() * 2.5f,
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = 80f + Random.nextFloat() * 250f,
                    scale = 1.2f + Random.nextFloat() * 1.6f,
                    variantIndex = Random.nextInt(8)
            )
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2500, easing = LinearEasing)
        )
        onFinished()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val currentProgress = progress.value

        particles.forEach { particle ->
            val bitmap = pistachioBitmaps[particle.variantIndex]

            // Position Y : tombe de haut en bas
            val y = (particle.startY + currentProgress * 1.3f * particle.speed) * canvasHeight

            // Position X : oscillation sinusoïdale
            val x =
                    particle.x * canvasWidth +
                            sin((currentProgress * particle.frequency * Math.PI * 2).toDouble())
                                    .toFloat() * particle.amplitude

            // Rotation de la pistache
            val rotation = particle.rotation + currentProgress * particle.rotationSpeed

            // Opacité : fade out vers la fin
            val alpha =
                    if (currentProgress > 0.75f) {
                        1f - ((currentProgress - 0.75f) / 0.25f)
                    } else {
                        1f
                    }

            val imgSize = (48 * particle.scale).toInt()

            if (y in -60f..canvasHeight + 60f) {
                rotate(degrees = rotation, pivot = Offset(x, y)) {
                    translate(left = x - imgSize / 2, top = y - imgSize / 2) {
                        drawImage(
                                image = bitmap,
                                dstOffset = IntOffset(0, 0),
                                dstSize = IntSize(imgSize, imgSize),
                                alpha = alpha
                        )
                    }
                }
            }
        }
    }
}

/** Représente une pistache individuelle dans l'animation. */
private data class PistachioParticle(
        val x: Float,
        val startY: Float,
        val speed: Float,
        val amplitude: Float,
        val frequency: Float,
        val rotation: Float,
        val rotationSpeed: Float,
        val scale: Float,
        val variantIndex: Int
)
