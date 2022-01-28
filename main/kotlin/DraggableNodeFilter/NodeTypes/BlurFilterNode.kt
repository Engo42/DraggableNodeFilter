package DraggableNodeFilter

import javafx.geometry.Rectangle2D
import javafx.scene.SnapshotParameters
import javafx.scene.effect.GaussianBlur
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage

class BlurFilterNode: DraggableNode() {
    var image: Image? = null
    var int_val = 0;
    init {
        title_label?.text = "BlurFilter"

        initImageView()

        addLeftHandle("Image")
        addLeftHandle("Int")

        addRightHandle("Image")
    }

    override fun update() {
        image = readInput(0, null) as Image?
        int_val = readInput(1, 0) as Int
        if (image != null) {
            val blurFilter = GaussianBlur()
            blurFilter.radius = int_val.toDouble()
            val blurView = ImageView()
            blurView.image = image
            blurView.effect = blurFilter
            val snapshotParameters = SnapshotParameters()
            snapshotParameters.viewport = Rectangle2D(0.0, 0.0, image!!.width, image!!.height)
            val newImage = WritableImage(image!!.width.toInt(), image!!.height.toInt())
            blurView.snapshot(snapshotParameters, newImage)
            image = newImage
        }
        imageView.image = image
        next_node?.update()
    }
    override fun getValue(): Any? {
        return image
    }
}