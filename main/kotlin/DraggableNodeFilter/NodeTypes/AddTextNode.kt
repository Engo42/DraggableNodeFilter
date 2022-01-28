package DraggableNodeFilter

import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image

class AddTextNode: DraggableNode() {
    var image: Image? = null
    var x_val = 0;
    var y_val = 0;
    var text_val = "";
    init {
        title_label?.text = "AddText"

        initImageView()

        addLeftHandle("Image")
        addLeftHandle("Int")
        addLeftHandle("Int")
        addLeftHandle("String")

        addRightHandle("Image")
    }

    override fun update() {
        image = readInput(0, null) as Image?
        x_val = readInput(1, 0) as Int
        y_val = readInput(2, 0) as Int
        text_val = readInput(3, "") as String
        if (image != null) {
            var imageCanvas = Canvas(image!!.width, image!!.height)
            imageCanvas.graphicsContext2D.drawImage(image, 0.0, 0.0)
            imageCanvas.graphicsContext2D.fillText(text_val, x_val.toDouble(), y_val.toDouble())

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