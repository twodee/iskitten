package org.twodee.iskitten

import android.util.JsonReader
import android.util.JsonWriter

class Node(var text: String, var left: Node? = null, var right: Node? = null) {
  fun prompt() = if (isLeaf()) "Are you thinking of ${text}?" else text
  fun isLeaf() = left == null && right == null

  fun bifurcate(newThing: String, yesQuestion: String) {
    left = Node(newThing)
    right = Node(text)
    text = yesQuestion
  }

  fun write(writer: JsonWriter) {
    writer.beginObject()
    writer.name("text").value(text)
    if (!isLeaf()) {
      writer.name("left")
      left?.write(writer)
      writer.name("right")
      right?.write(writer)
    }
    writer.endObject()
  }

  companion object {
    fun read(reader: JsonReader): Node {
      val tree = Node("")

      reader.beginObject()
      while (reader.hasNext()) {
        val key = reader.nextName()
        when (key) {
          "text" -> tree.text = reader.nextString()
          "left" -> tree.left = read(reader)
          "right" -> tree.right = read(reader)
        }
      }
      reader.endObject()

      return tree
    }
  }
}

