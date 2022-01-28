package DraggableNodeFilter

import javafx.scene.SnapshotParameters
import javafx.scene.effect.SepiaTone
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage

class SepiaNode: DraggableNode() {
    var image: Image? = null
    init {
        title_label?.text = "Sepia"

        initImageView()

        addLeftHandle("Image")

        addRightHandle("Image")
    }

    override fun update() {
        image = readInput(0, null) as Image?
        if (image != null) {
            val sepiaFilter = SepiaTone()
            sepiaFilter.level = 1.0
            val sepiaView = ImageView()
            sepiaView.image = image
            sepiaView.effect = sepiaFilter
            val snapshotParameters = SnapshotParameters()
            val newImage = WritableImage(image!!.width.toInt(), image!!.height.toInt())
            sepiaView.snapshot(snapshotParameters, newImage)
            image = newImage
        }
        imageView.image = image
        next_node?.update()
    }
    override fun getValue(): Any? {
        return image
    }
}