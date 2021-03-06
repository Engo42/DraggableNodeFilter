package DraggableNodeFilter

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.image.Image

class ImageNode() : DraggableNode() {
    val openButton = Button("Open")
    var imagePath: String = "null"
    init {
        title_label?.text = "Image"

        addRightHandle("Image")
        initImageView()
        imageView.fitHeight = 40.0
        imageView.isManaged = false

        openButton.onAction = EventHandler {
            val file = app.chooseFileToOpen()
            app.writeLastDirectory("open", file.path)
            imagePath = file.path
            val image = Image(file.toURI().toString())
            imageView.isManaged = true
            imageView.image = image
            next_node?.update()
        }
        openButton.prefWidth = 60.0
        content?.children?.add(openButton)
    }
    override fun update() {
        next_node?.update()
    }
    override fun getValue(): Any? {
        return imageView.image
    }
}