package DraggableNodeFilter

import javafx.scene.SnapshotParameters
import javafx.scene.effect.ColorAdjust
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage

class BrightnessNode: DraggableNode() {
    var image: Image? = null
    var float_val = 0.0F;
    init {
        title_label?.text = "Brightness"

        initImageView()

        addLeftHandle("Image")
        addLeftHandle("Float")

        addRightHandle("Image")
    }

    override fun update() {
        image = readInput(0, null) as Image?
        float_val = readInput(1, 0.0F) as Float
        if (image != null) {
            val colorAdjust = ColorAdjust()
            colorAdjust.brightness = float_val.toDouble() / 100
            val brightnessView = ImageView()
            brightnessView.image = image
            brightnessView.effect = colorAdjust
            val snapshotParameters = SnapshotParameters()
            val newImage = WritableImage(image!!.width.toInt(), image!!.height.toInt())
            brightnessView.snapshot(snapshotParameters, newImage)
            image = newImage
        }
        imageView.image = image
        next_node?.update()
    }
    override fun getValue(): Any? {
        return image
    }
}