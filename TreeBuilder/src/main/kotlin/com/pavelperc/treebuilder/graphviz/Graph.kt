package com.pavelperc.treebuilder.graphviz

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

/**
 * Created by pavel on 02.04.2018.
 */

/** Node for GraphViz*/
class GVNode(
    val id: Int,
    var label: String = "n$id",
    var shape: String = "oval",
    var color: String = "black",
    var fillColor: String? = null
) {
    
    override fun toString(): String {
        var node = "n"
        
        node += id
        node += " [label=\"$label\""
        node += ",shape=$shape"
        node += ",color=$color"
        if (fillColor != null) {
            node += ",style=filled,fillcolor=$fillColor"
        }
        
        node += "];"
        
        return node
    }
}

/** Edge for GraphViz*/
class GVEdge(
        val first: GVNode,
        val second: GVNode,
        var style: String = "solid"
) {
    
    override fun toString(): String {
        var edge = ""
        edge += "n${first.id} -> n${second.id}"
        edge += " [style=$style]"
        edge += ";"
        
        return edge
    }
}

/** Used for creating visualized graph for GraphViz.
 * Created graph is saved to [fileName] after calling method [writeToFile].
 * [fileName] should have .gv extension*/
class Graph(val fileName:String, val graphLabel: String = fileName, val fontsize: Int = 80) {
    
    private val nodes = mutableListOf<GVNode>()
    
    private val edges = mutableListOf<GVEdge>()

//    fun createNewNode(): GVNode.Builder {
//        val builder = GVNode.Builder(nodes.size.toString())
//        return builder
//    }
    
    init {
        File(fileName).parentFile?.mkdirs()
    }
    
    fun newNode(): GVNode {
        val node = GVNode(nodes.size)
        nodes.add(node)
        return node
    }
    
    fun newEdge(first: GVNode, second: GVNode): GVEdge {
        val edge = GVEdge(first, second)
        edges.add(edge)
        return edge
    }
    
    
    private fun generate(): String {
        var ans = "digraph G {\n"
        ans += "labelloc=\"t\";"
        ans += "fontsize=$fontsize;"
        ans += "label=\"$graphLabel\";\n"
        //        ans += "graph [ dpi = 300 ];\n";
        
        
        ans += nodes.joinToString("\n", postfix = "\n")
        ans += edges.joinToString("\n", postfix = "\n")
        
        ans += "}"
        return ans
    }
    
    
    fun writeToFile() {
        val printWriter = PrintWriter(FileWriter(fileName))
        printWriter.print(generate())
        printWriter.close()
        println("printed gv to $fileName")
    }
}