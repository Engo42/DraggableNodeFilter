package DraggableNodeFilter

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import java.util.*

lateinit var LinkStartNode: DraggableNode

open class DraggableNode : AnchorPane() {
    @FXML
    var root_pane: AnchorPane? = null
    @FXML
    var node_overlay: GridPane? = null
    @FXML
    var left_links: VBox? = null
    @FXML
    var right_links: VBox? = null
    @FXML
    var title_bar: GridPane? = null
    @FXML
    var title_label: Label? = null
    @FXML
    var title_close: AnchorPane? = null
    @FXML
    var content: VBox? = null

    var next_node: DraggableNode? = null
    var inputs: MutableMap<Any, DraggableNode> = mutableMapOf()
    var inputsByIndex: MutableMap<Int, Any> = mutableMapOf()
    var imageView = ImageView()
    var inputTypes: MutableMap<Any, String> = mutableMapOf()
    var outputType: String? = null

    var x: Int = 0
    var y: Int = 0

    lateinit var contextDragOver: EventHandler<DragEvent>
    lateinit var contextDragDropped: EventHandler<DragEvent>

    lateinit var linkDragDetected: EventHandler<MouseEvent>
    lateinit var linkDragDropped: EventHandler<DragEvent>
    lateinit var contextLinkDragOver: EventHandler<DragEvent>
    lateinit var contextLinkDragDropped: EventHandler<DragEvent>

    var myLink = NodeLink()
    var offset = Point2D(0.0, 0.0)

    var superParent: AnchorPane? = null

    @FXML
    private fun initialize() {
        nodeHandlers()
        linkHandlers()

        //left_link_handle!!.onDragDropped = linkDragDropped
        //right_link_handle!!.onDragDetected = linkDragDetected

        myLink.isVisible = false

        parentProperty().addListener{ o, old, new -> superParent = parent as AnchorPane? }
    }

    init {
        val fxmlLoader = FXMLLoader(
            javaClass.getResource("/DraggableNode.fxml")
        )
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        fxmlLoader.load<Any>()
        id = UUID.randomUUID().toString()
        app.nodeList.add(this)
    }

    fun updatePoint(p: Point2D) {
        val local = parent.sceneToLocal(p)
        relocate(
            (local.x - offset.x),
            (local.y - offset.y)
        )
    }

    fun nodeHandlers() {

        contextDragOver = EventHandler { event ->
            if (event.x - offset.x >= 0 && event.y - offset.y >= 0)
                updatePoint(Point2D(event.sceneX, event.sceneY))
            event.consume()
        }

        contextDragDropped = EventHandler { event ->
            parent.onDragDropped = null
            parent.onDragOver = null
            event.isDropCompleted = true
            event.consume()
        }

        title_bar!!.onDragDetected = EventHandler { event ->
            parent.onDragOver = contextDragOver
            parent.onDragDropped = contextDragDropped

            offset = Point2D(event.x, event.y)
            updatePoint(Point2D(event.sceneX, event.sceneY))

            val content = ClipboardContent()
            content[stateAddNode] = "node"
            startDragAndDrop(*TransferMode.ANY).setContent(content)
        }

        title_close!!.onMousePressed = EventHandler { event ->
            if (next_node != null) {
                for ((key, value) in next_node!!.inputs)
                    if (this == value) {
                        next_node!!.inputs.remove(key)
                        break
                    }
                next_node!!.update()
            }
            for ((key, value) in this.inputs) {
                value.next_node = null
                superParent!!.children.remove(value.myLink)
            }
            myLink.isVisible = false
            app.nodeList.remove(this)
            superParent!!.children.remove(myLink)
            superParent!!.children.remove(this)
        }

    }

    fun linkHandlers() {

        linkDragDetected = EventHandler { event ->
            parent.onDragOver = null
            parent.onDragDropped = null

            parent.onDragOver = contextLinkDragOver
            parent.onDragDropped = contextLinkDragDropped
            if (!superParent!!.children.contains(myLink))
                superParent!!.children.add(0, myLink)
            myLink.isVisible = true
            myLink.unbindStartEnd(this)
            LinkStartNode = this

            if (this.next_node != null) {
                for ((key, value) in next_node!!.inputs)
                    if (this == value) {
                        next_node!!.inputs.remove(key)
                        break
                    }
                next_node!!.update()
                this.next_node = null
            }

            val p = Point2D(layoutX + width/2, layoutY+height/2)
            myLink.setStart(p)

            val content = ClipboardContent()
            content[stateAddLink] = "link"
            startDragAndDrop(*TransferMode.ANY).setContent(content)
            event.consume()
        }

        linkDragDropped = EventHandler { event ->
            parent.onDragDropped = null
            parent.onDragOver = null

            LinkStartNode.myLink.isVisible = false

            if (inputTypes[event.source] == LinkStartNode.outputType && inputs[event.source] == null && notALoop(LinkStartNode, this)) {
                LinkStartNode.myLink.isVisible = true
                println("link connect")
                LinkStartNode.myLink.bindStartEnd(LinkStartNode, this)

                inputs[event.source] = LinkStartNode
                LinkStartNode.next_node = this
                this.update()
            }
            event.isDropCompleted = true
            event.consume()
        }


        contextLinkDragOver = EventHandler { event ->
            if (event.x >= 0 && event.y >= 0) {
                event.acceptTransferModes(*TransferMode.ANY)
                if (!myLink.isVisible)
                    myLink.isVisible = true
                myLink.setEnd(Point2D(event.x, event.y))
            }

            event.consume()
        }

        contextLinkDragDropped = EventHandler { event ->
            println("link dropped")
            parent.onDragDropped = null
            parent.onDragOver = null

            myLink.isVisible = false
            superParent!!.children.remove(myLink)

            event.isDropCompleted = true
            event.consume()
        }
    }

    fun notALoop(node1: DraggableNode, node2: DraggableNode): Boolean {
        var it = node2
        while (it.next_node != null) {
            if (it.next_node == node1)
                return false
            it = it.next_node!!
        }
        return true
    }

    fun initImageView() {
        imageView.fitWidth = 60.0
        imageView.fitHeight = 81.0
        imageView.isPreserveRatio = true
        content?.children?.add(imageView)
    }

    fun addLeftHandle(type: String) {
        val new_handle = AnchorPane()
        left_links?.children?.add(new_handle)
        new_handle.styleClass.add("link-handle")
        new_handle.onDragDropped = linkDragDropped
        inputTypes[left_links?.children!![left_links?.children!!.size - 1]] = type
        inputsByIndex[left_links?.children!!.size - 1] = left_links?.children!![left_links?.children!!.size - 1]
        when (type) {
            "Int" -> new_handle.styleClass.add("int-handle")
            "Float" -> new_handle.styleClass.add("float-handle")
            "String" -> new_handle.styleClass.add("string-handle")
            "Image" -> new_handle.styleClass.add("image-handle")
        }
        for (item: Node in left_links?.children!!) {
            (item as AnchorPane).prefHeight = 81.0 / left_links?.children!!.size
        }
    }

    fun addRightHandle(type: String) {
        val new_handle = AnchorPane()
        new_handle.styleClass.add("link-handle")
        new_handle.prefHeight = 81.0
        new_handle.onDragDetected = linkDragDetected
        right_links?.children?.add(new_handle)
        outputType = type
        when (type) {
            "Int" -> new_handle.styleClass.add("int-handle")
            "Float" -> new_handle.styleClass.add("float-handle")
            "String" -> new_handle.styleClass.add("string-handle")
            "Image" -> new_handle.styleClass.add("image-handle")
        }
    }

    fun readInput(index: Int, default: Any?): Any? {
        if (inputs[left_links?.children!![index]] != null)
            return inputs[left_links?.children!![index]]?.getValue()
        return default
    }

    open fun update() {}
    open fun getValue(): Any? {
        return 0
    }
}
