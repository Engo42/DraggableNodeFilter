package DraggableNodeFilter

import javafx.scene.control.TextField

class FloatNode: DraggableNode() {
    var input_field: TextField? = null
    var float_val = 0.0F;
    init {
        title_label?.text = "Float"

        addRightHandle("Float")

        input_field = TextField()
        input_field!!.textProperty().addListener { observable, oldValue, newValue ->
            if (newValue.toFloatOrNull() != null)
                float_val = newValue.toFloat()
            next_node?.update()
        }
        input_field!!.prefWidth = 60.0
        content?.children?.add(input_field)
    }
    override fun update() {
        next_node?.update()
    }
    override fun getValue(): Any? {
        return float_val
    }
}