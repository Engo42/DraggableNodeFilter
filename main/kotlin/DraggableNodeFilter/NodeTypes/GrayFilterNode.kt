package DraggableNodeFilter

import javafx.scene.image.Image
import javafx.scene.image.WritableImage

class GrayFilterNode: DraggableNode() {
    var image: Image? = null
    init {
        title_label?.text = "GrayFilter"

        initImageView()

        addLeftHandle("Image")

        addRightHandle("Image")
    }

    override fun update() {
        image = readInput(0, null) as Image?
        if (image != null) {
            val newImage = WritableImage(image!!.width.toInt(), image!!.height.toInt())
            val reader = image!!.pixelReader
            val writer = newImage.pixelWriter
            for (y in 0 until image!!.height.toInt()) {
                for (x in 0 until image!!.width.toInt()) {
                    writer.setColor(x, y, reader.getColor(x, y).grayscale())
                }
            }
            image = newImage
        }
        imageView.image = image
        this.next_node?.update()
    }
    override fun getValue(): Any? {
        return image
    }
}