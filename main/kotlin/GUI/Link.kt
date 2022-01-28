package GUI

import javafx.beans.binding.Bindings
import javafx.beans.binding.When
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.shape.CubicCurve
import java.io.IOException
import java.util.*

class NodeLink : AnchorPane() {
    @FXML
    var node_link: CubicCurve? = null

    val offsetX = SimpleDoubleProperty()
    val offsetY = SimpleDoubleProperty()
    val offsetDirX1 = SimpleDoubleProperty()
    val offsetDirX2 = SimpleDoubleProperty()
    val offsetDirY1 = SimpleDoubleProperty()
    val offsetDirY2 = SimpleDoubleProperty()

    @FXML
    private fun initialize() {
        offsetX.set(100.0)
        offsetY.set(50.0)

        offsetDirX1.bind(
            When(node_link!!.startXProperty().greaterThan(node_link!!.endXProperty())).then(-1.0).otherwise(1.0))

        offsetDirX2.bind(
            When(node_link!!.startXProperty().greaterThan(node_link!!.endXProperty())).then(1.0).otherwise(-1.0))

        node_link!!.controlX1Property().bind(Bindings.add(node_link!!.startXProperty(), offsetX.multiply(offsetDirX1)))
        node_link!!.controlX2Property().bind(Bindings.add(node_link!!.endXProperty(), offsetX.multiply(offsetDirX2)))
        node_link!!.controlY1Property().bind(Bindings.add(node_link!!.startYProperty(), offsetY.multiply(offsetDirY1)))
        node_link!!.controlY2Property().bind(Bindings.add(node_link!!.endYProperty(), offsetY.multiply(offsetDirY2)))
    }

    fun setStart(point: Point2D) {
        node_link!!.startX = point.x
        node_link!!.startY = point.y
    }

    fun setEnd(point: Point2D) {
        node_link!!.endX = point.x
        node_link!!.endY = point.y
    }

    fun bindStartEnd(source1: DraggableNode, source2: DraggableNode) {
        node_link!!.startXProperty().bind(Bindings.add(source1.layoutXProperty(), 45.0))
        node_link!!.startYProperty().bind(Bindings.add(source1.layoutYProperty(), 45.0))
        node_link!!.endXProperty().bind(Bindings.add(source2.layoutXProperty(), 45.0))
        node_link!!.endYProperty().bind(Bindings.add(source2.layoutYProperty(), 45.0))
    }

    fun unbindStartEnd(source: DraggableNode) {
        node_link!!.startXProperty().unbind()
        node_link!!.startYProperty().unbind()
        node_link!!.endXProperty().unbind()
        node_link!!.endYProperty().unbind()
    }

    init {
        val fxmlLoader = FXMLLoader(
            javaClass.getResource("/NodeLink.fxml")
        )
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        try {
            fxmlLoader.load<Any>()
        } catch (exception: IOException) {
            throw RuntimeException(exception)
        }
        id = UUID.randomUUID().toString()
    }
}
