package DraggableNodeFilter

import javafx.scene.image.Image

class FinishNode: DraggableNode() {
    var image: Image? = null
    init {
        title_label?.text = "FinishImage"

        title_close?.isVisible = false

        initImageView()

        addLeftHandle("Image")
    }

    override fun update() {
        image = readInput(0, null) as Image?
        imageView.image = image
        app.fullImageView.image = image
        next_node?.update()
    }
}