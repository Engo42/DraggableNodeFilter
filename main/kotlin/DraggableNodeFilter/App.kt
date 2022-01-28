package GUI

import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.*
import javafx.scene.layout.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_4BYTE_ABGR
import java.io.*
import javax.imageio.ImageIO

lateinit var app: App
var stateAddLink = DataFormat("linkAdd")
var stateAddNode = DataFormat("nodeAdd")


class FilterList {
    var node = VBox();
    init {
        val nodeSpace = app.nodeSpace
        val primaryStage = app.primaryStage
        node.prefWidth = 150.0

        addButton("Int", EventHandler {  nodeSpace.children.add(IntNode())  })
        addButton("Float", EventHandler {  nodeSpace.children.add(FloatNode())  })
        addButton("String", EventHandler {  nodeSpace.children.add(StringNode())  })
        addButton("Image", EventHandler {  nodeSpace.children.add(ImageNode())  })
        addButton("Add Text", EventHandler {  nodeSpace.children.add(AddTextNode())  })
        addButton("Add Image", EventHandler {  nodeSpace.children.add(AddImageNode())  })
        addButton("Gray Filter", EventHandler {  nodeSpace.children.add(GrayFilterNode())  })
        addButton("Brightness", EventHandler {  nodeSpace.children.add(BrightnessNode())  })
        addButton("Sepia", EventHandler {  nodeSpace.children.add(SepiaNode())  })
        addButton("Invert Filter", EventHandler {  nodeSpace.children.add(InvertFilterNode())  })
        addButton("Blur Filter", EventHandler {  nodeSpace.children.add(BlurFilterNode())  })
        addButton("Transform Move", EventHandler {  nodeSpace.children.add(TransformMoveNode())  })
        addButton("Transform Scale", EventHandler {  nodeSpace.children.add(TransformScaleNode())  })
        addButton("Transform Rotate", EventHandler {  nodeSpace.children.add(TransformRotateNode())  })
    }
    private fun addButton(name: String, callback: EventHandler<ActionEvent>) {
        val btnAddImage = Button(name)
        btnAddImage.onAction = callback
        btnAddImage.prefWidth = 150.0
        node.children.add(btnAddImage)
    }
}

class MyMenuBar(primaryStage: Stage, app: App) {
    var node = MenuBar();
    init {
        val fileMenu = Menu("Фаил")
        val fileMenuSave = MenuItem("Сохранить")
        fileMenuSave.onAction = EventHandler {
            app.saveImage(false, app.nodeFinish as FinishNode, primaryStage)
        }
        val fileMenuQuickSave = MenuItem("Быстрое сохранение")
        fileMenuQuickSave.onAction = EventHandler {
            app.saveImage(true, app.nodeFinish as FinishNode, primaryStage)
        }
        val fileMenuOpen = MenuItem("Открыть")
        fileMenuOpen.onAction = EventHandler {
            app.openImage(app.nodeStart as StartNode, primaryStage)
        }
        val fileMenuSaveScene = MenuItem("Сохранить сцену")
        fileMenuSaveScene.onAction = EventHandler {
            app.saveScene(app.nodeList, primaryStage)
        }
        val fileMenuOpenScene = MenuItem("Открыть сцену")
        fileMenuOpenScene.onAction = EventHandler {
            app.openScene(app.nodeList, primaryStage)
        }
        val fileMenuExit = MenuItem("Выйти")
        fileMenuExit.onAction = EventHandler {
            primaryStage.close();
        }
        fileMenu.accelerator = KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN)
        fileMenuSave.accelerator = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)
        fileMenuQuickSave.accelerator = KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN)
        fileMenu.items.addAll(fileMenuOpen, fileMenuSave, fileMenuQuickSave, fileMenuSaveScene, fileMenuOpenScene, fileMenuExit)
        node.menus.add(fileMenu)
    }
}

class App: Application() {
    var nodeList = mutableSetOf<DraggableNode>()
    lateinit var nodeStart: DraggableNode
    lateinit var nodeFinish: DraggableNode
    val nodeSpace = AnchorPane()
    val fullImageView = ImageView()
    lateinit var primaryStage: Stage

    override fun start(stage: Stage) {
        primaryStage = stage
        app = this
        nodeStart = StartNode()
        nodeFinish = FinishNode()
        val root = VBox()
        val hBox = HBox()

        val menuBar = MyMenuBar(primaryStage, this)
        root.children.add(menuBar.node)

        fullImageView.isManaged = false
        fullImageView.isVisible = false

        val leftPanel = VBox()
        leftPanel.spacing = 20.0
        val btnFullImageView = Button("Изображение")
        val btnNodeView = Button("Фильтры")
        btnFullImageView.onAction = EventHandler {
            btnFullImageView.isVisible = false
            btnFullImageView.isManaged = false
            btnNodeView.isVisible = true
            btnNodeView.isManaged = true
            nodeSpace.isManaged = false
            nodeSpace.isVisible = false
            fullImageView.isManaged = true
            fullImageView.isVisible = true
        }
        btnNodeView.onAction = EventHandler {
            btnFullImageView.isVisible = true
            btnFullImageView.isManaged = true
            btnNodeView.isVisible = false
            btnNodeView.isManaged = false
            nodeSpace.isManaged = true
            nodeSpace.isVisible = true
            fullImageView.isManaged = false
            fullImageView.isVisible = false
        }
        btnFullImageView.minWidth = 150.0
        btnNodeView.minWidth = 150.0
        leftPanel.children.add(btnFullImageView)
        leftPanel.children.add(btnNodeView)
        btnNodeView.isVisible = false
        btnNodeView.isManaged = false

        val filterList = FilterList()
        leftPanel.children.add(filterList.node)

        val leftAnchorPane = AnchorPane(leftPanel)
        AnchorPane.setTopAnchor(leftPanel, 8.0);
        AnchorPane.setLeftAnchor(leftPanel, 8.0);
        AnchorPane.setRightAnchor(leftPanel, 8.0);
        hBox.children.add(leftAnchorPane)

        val scene = Scene(root, 800.0, 600.0)
        nodeSpace.prefHeightProperty().bind(scene.heightProperty())
        nodeSpace.prefWidthProperty().bind(scene.widthProperty())
        nodeSpace.children.add(nodeStart)
        nodeStart.updatePoint(Point2D(0.0, 220.0))
        nodeSpace.children.add(nodeFinish)
        nodeFinish.updatePoint(Point2D(520.0, 220.0))
        hBox.children.add(nodeSpace)
        hBox.children.add(fullImageView)

        root.children.add(hBox)

        primaryStage.scene = scene
        primaryStage.show()
    }

    fun chooseFileToOpen(primaryStage: Stage): File {
        val fileChooser = FileChooser()
        val imageFilter = FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png")
        fileChooser.extensionFilters.add(imageFilter)
        fileChooser.initialDirectory = File(getLastDirectory("open"))
        return fileChooser.showOpenDialog(primaryStage)
    }
    fun openImage(nodeStart: StartNode, primaryStage: Stage) {
        var file = chooseFileToOpen(primaryStage)
        writeLastDirectory("open", file.path)
        val image = Image(file.toURI().toString());
        nodeStart.imageView.image = image
        nodeStart.imagePath = file.path
        nodeStart.update()
    }
    fun saveImage(useDefaultDirectory: Boolean, nodeFinish: FinishNode, primaryStage: Stage) {
        val file: File
        if (useDefaultDirectory) {
            file = File(System.getenv("USERPROFILE") + "\\Documents\\quickSave.png")
        } else {
            val fileChooser = FileChooser()
            val imageFilter = FileChooser.ExtensionFilter("Image Files", "*.png")
            fileChooser.extensionFilters.add(imageFilter)
            fileChooser.initialDirectory = File(getLastDirectory("save"))
            file = fileChooser.showSaveDialog(primaryStage)
        }
        val image = nodeFinish.image
        ImageIO.write(convertToBufferedImage(image), "png", file)
        if (!useDefaultDirectory) {
            writeLastDirectory("save", file.path)
        }
    }
    fun openScene(nodeList: MutableSet<DraggableNode>, primaryStage: Stage) {
        val fileChooser = FileChooser()
        val imageFilter = FileChooser.ExtensionFilter("Scene Files", "*.scene")
        fileChooser.extensionFilters.add(imageFilter)
        fileChooser.initialDirectory = File(getLastDirectory("openScene"))
        val file = fileChooser.showOpenDialog(primaryStage)
        var text: String

        BufferedReader(FileReader(file)).use { reader ->
            text = reader.readText()
        }

        nodeList.clear()
        nodeSpace.children.clear()

        var i = 0
        val nodeMap = mutableMapOf<String, DraggableNode>()
        val linkMap = mutableMapOf<String, Pair<String, String>>()
        while(i < text.length) {
            var id = ""
            while(text[i] != ' ') {
                id += text[i]
                i++
            }
            i++
            val info = arrayOf("", "", "", "", "", "")
            for (j: Int in 0..4) {
                var item = ""
                while (text[i] != ' ') {
                    item += text[i]
                    i++
                }
                i++
                info[j] = item
            }
            linkMap[id] = Pair(info[3], info[4])
            var item = ""
            while (text[i] != '\n') {
                item += text[i]
                i++
            }
            i++
            info[5] = item

            when (info[0]) {
                "Integer" -> {
                    nodeMap[id] = IntNode()
                    (nodeMap[id] as IntNode).input_field!!.text = info[5]
                    (nodeMap[id] as IntNode).int_val = info[5].toInt()
                }
                "Float" -> {
                    nodeMap[id] = FloatNode()
                    (nodeMap[id] as FloatNode).input_field!!.text = info[5]
                    (nodeMap[id] as FloatNode).float_val = info[5].toFloat()
                }
                "String" -> {
                    nodeMap[id] = StringNode()
                    (nodeMap[id] as StringNode).input_field!!.text = info[5]
                    (nodeMap[id] as StringNode).string_val = info[5]
                }
                "Image" -> {
                    nodeMap[id] = ImageNode()
                    if (info[5] != "null") {
                        val stream: InputStream = FileInputStream(info[5])
                        nodeMap[id]?.imageView?.image = Image(stream)
                        nodeMap[id]?.imageView?.isManaged = true
                        (nodeMap[id] as ImageNode).imagePath = info[5]
                    }
                }
                "StartImage" -> {
                    nodeMap[id] = StartNode()
                    if (info[5] != "null") {
                        val stream: InputStream = FileInputStream(info[5])
                        nodeMap[id]?.imageView?.image = Image(stream)
                        (nodeMap[id] as StartNode).imagePath = info[5]
                    }
                    nodeStart = nodeMap[id]!!
                }
                "FinishImage" -> {
                    nodeMap[id] = FinishNode()
                    nodeFinish = nodeMap[id]!!
                }
                "AddImage" -> nodeMap[id] = AddImageNode()
                "AddText" -> nodeMap[id] = AddTextNode()
                "BlurFilter" -> nodeMap[id] = BlurFilterNode()
                "Brightness" -> nodeMap[id] = BrightnessNode()
                "GrayFilter" -> nodeMap[id] = GrayFilterNode()
                "InvertFilter" -> nodeMap[id] = InvertFilterNode()
                "Sepia" -> nodeMap[id] = SepiaNode()
                "T.Move" -> nodeMap[id] = TransformMoveNode()
                "T.Rotate" -> nodeMap[id] = TransformRotateNode()
                "T.Scale" -> nodeMap[id] = TransformScaleNode()
            }
            nodeSpace.children.add(nodeMap[id])
            val local = nodeMap[id]?.parent?.sceneToLocal(Point2D(0.0, 0.0))
            if (local != null)
                nodeMap[id]?.updatePoint(Point2D(info[1].toDouble() - local.x, info[2].toDouble() - local.y))
        }

        for ((key, item) in nodeMap) {
            if (linkMap[key]?.first != "null") {
                val nextNode = nodeMap[linkMap[key]?.first]
                val leftHandle = linkMap[key]?.second?.let { nextNode?.inputsByIndex?.get(it.toInt()) }

                item.myLink.isVisible = true
                item.superParent!!.children.add(0, item.myLink)
                if (nextNode != null) {
                    item.myLink.bindStartEnd(item, nextNode)
                    nextNode.inputs[leftHandle as Any] = item
                }
                item.next_node = nextNode
            }
            item.update()
        }

        writeLastDirectory("openScene", file.path)
    }
    fun saveScene(nodeList: MutableSet<DraggableNode>, primaryStage: Stage) {
        val fileDir: File
        val fileChooser = FileChooser()
        val imageFilter = FileChooser.ExtensionFilter("Scene Files", "*.scene")
        fileChooser.extensionFilters.add(imageFilter)
        fileChooser.initialDirectory = File(getLastDirectory("saveScene"))
        fileDir = fileChooser.showSaveDialog(primaryStage)

        var text = ""
        for (item in nodeList) {
            text += item.id + " "
            text += item.title_label?.text + " "
            text += item.layoutX.toString() + " "
            text += item.layoutY.toString() + " "
            text += item.next_node?.id.toString() + " "
            if (item.next_node != null) {
                for ((key, value) in item.next_node!!.inputsByIndex)
                    if (item == item.next_node!!.inputs[value])
                        text += key.toString() + " "
            } else {
                text += "null" + " "
            }
            if (item.title_label?.text == "Integer")
                text += (item as IntNode).int_val.toString()
            if (item.title_label?.text == "Float")
                text += (item as FloatNode).float_val.toString()
            if (item.title_label?.text == "String")
                text += (item as StringNode).string_val
            if (item.title_label?.text == "Image")
                text += (item as ImageNode).imagePath
            if (item.title_label?.text == "StartImage") {
                text += (item as StartNode).imagePath
            }
            text += "\n"
        }

        try {
            BufferedWriter(PrintWriter(fileDir.path)).use { bw ->
                val file = File(text);
                if (file.isFile)
                    bw.write(file.parent)
                else
                    bw.write(text)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        writeLastDirectory("saveScene", fileDir.path)
    }
    fun writeLastDirectory(filename: String, text: String) {
        try {
            BufferedWriter(PrintWriter(System.getenv("USERPROFILE") + "\\Documents\\" + filename + ".txt")).use { bw ->
                val file = File(text);
                if (file.isFile)
                    bw.write(file.parent)
                else
                    bw.write(text)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun getLastDirectory(filename: String): String {
        try {
            BufferedReader(FileReader(System.getenv("USERPROFILE") + "\\Documents\\" + filename + ".txt")).use { reader ->
                return reader.readLine()
            }
        } catch (e: IOException) {
            return System.getenv("USERPROFILE") + "\\Documents\\"
        }
    }
    private fun convertToBufferedImage(image: Image?): BufferedImage? {
        var wr: BufferedImage? = null
        if (image != null) {
            wr = BufferedImage(image.width.toInt(), image.height.toInt(), TYPE_4BYTE_ABGR)
            val pw = image.pixelReader
            for (x in 0 until image.width.toInt()) {
                for (y in 0 until image.height.toInt()) {
                    wr.setRGB(x, y, pw.getArgb(x, y))
                }
            }
        }
        return wr
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(App::class.java)
        }
    }
}
