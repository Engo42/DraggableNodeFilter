package GUI

class StartNode: DraggableNode() {
    var imagePath: String = "null"
    init {
        title_label?.text = "StartImage"

        title_close?.isVisible = false

        initImageView()

        addRightHandle("Image")
    }
    override fun update() {
        next_node?.update()
    }
    override fun getValue(): Any? {
        return imageView.image
    }
}