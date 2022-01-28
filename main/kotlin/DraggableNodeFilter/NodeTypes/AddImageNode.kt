package DraggableNodeFilter

import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image

class AddImageNode: DraggableNode() {
    var image: Image? = null
    var x_val = 0;
    var y_val = 0;
    var new_image_val: Image? = null
    init {
        title_label?.text = "AddImage"

        initImageView()

        addLeftHandle("Image")
        addLeftHandle("Int")
        addLeftHandle("Int")
        addLeftHandle("Image")

        addRightHandle("Image")
    }

    override fun update() {
        image = readInput(0, null) as Image?
        x_val = readInput(1, 0) as Int
        y_val = readInput(2, 0) as Int
        new_image_val = readInput(3, null) as Image?
        if (image != null && new_image_val != null) {
            var imageCanvas = Canvas(image!!.width, image!!.height)
            imageCanvas.graphicsContext2D.drawImage(image, 0.0, 0.0)
            imageCanvas.graphicsContext2D.drawImage(new_image_val, x_val.toDouble(), y_val.toDouble())

            var params = SnapshotParameters()
            image = imageCanvas.snapshot(params, null)
        }
        imageView.image = image
        next_node?.update()
    }
    override fun getValue(): Any? {
        return image
    }
}