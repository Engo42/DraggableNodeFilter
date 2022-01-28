package GUI

import javafx.scene.control.TextField

class StringNode: DraggableNode() {
    var input_field: TextField? = null
    var string_val = "";
    init {
        title_label?.text = "String"

        addRightHandle("String")

        input_field = TextField()
        input_field!!.textProperty().addListener { observable, oldValue, newValue ->
            string_val = newValue
            next_node?.update()
        }
        input_field!!.prefWidth = 60.0
        content?.children?.add(input_field)
    }
    override fun update() {
        next_node?.update()
    }
    override fun getValue(): Any? {
        return string_val
    }
}