package DraggableNodeFilter

import javafx.scene.control.TextField

class IntNode: DraggableNode() {
    var input_field: TextField? = null
    var int_val = 0;
    init {
        title_label?.text = "Integer"

        addRightHandle("Int")

        input_field = TextField()
        input_field!!.textProperty().addListener { observable, oldValue, newValue ->
            if (newValue.toIntOrNull() != null)
                int_val = newValue.toInt()
            next_node?.update()
        }
        input_field!!.prefWidth = 60.0
        content?.children?.add(input_field)

    }
    override fun update() {
        next_node?.update()
    }
    override fun getValue(): Any {
        return int_val
    }
}