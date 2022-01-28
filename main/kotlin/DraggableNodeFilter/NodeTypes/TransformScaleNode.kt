package DraggableNodeFilter

import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.WritableImage

class TransformScaleNode: DraggableNode() {
    var image: Image? = null
    var x_val = 0.0F;
    var y_val = 0.0F;
    init {
        title_label?.text = "T.Scale"

        initImageView()

        addLeftHandle("Image")
        addLeftHandle("Float")
        addLeftHandle("Float")

        addRightHandle("Image")
    }

    override fun update() {
        image = readInput(0, null) as Image?
        x_val = readInput(1, 0.0F) as Float
        y_val = readInput(2, 0.0F) as Float
        if (image != null && x_val > 0 && y_val > 0) {
            val imageCanvas = Canvas(x_val.toDouble(), y_val.toDouble())
            imageCanvas.graphicsContext2D.drawImage(image, 0.0, 0.0, x_val.toDouble(), y_val.toDouble())
            val params = SnapshotParameters()
            val newImage = WritableImage(x_val.toInt(), y_val.toInt())
            imageCanvas.snapshot(params, newImage)
            image = newImage
        }
        imageView.image = image
        next_node?.update()
    }
    override fun getValue(): Any? {
        return image
    }
}