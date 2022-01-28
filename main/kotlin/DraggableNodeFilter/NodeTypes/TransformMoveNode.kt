package GUI

import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.WritableImage

class TransformMoveNode: DraggableNode() {
    var image: Image? = null
    var x_val = 0.0F;
    var y_val = 0.0F;
    init {
        title_label?.text = "T.Move"

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
        if (image != null) {
            val imageCanvas = Canvas(image!!.width, image!!.height)
            imageCanvas.graphicsContext2D.drawImage(image, x_val.toDouble(), y_val.toDouble())
            val params = SnapshotParameters()
            val newImage = WritableImage(image!!.width.toInt(), image!!.height.toInt())
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