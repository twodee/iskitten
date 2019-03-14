package org.twodee.iskitten

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.JsonReader
import android.util.JsonWriter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MainActivity : Activity() {
  private lateinit var root: Node// = Node("Does it have a tail?", Node("kitten"), Node("egg"))
  private lateinit var node: Node// = root
  private lateinit var promptText: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    promptText = findViewById(R.id.promptText)
    val noButton: Button = findViewById(R.id.yesButton)
    val yesButton: Button = findViewById(R.id.noButton)

    yesButton.setOnClickListener {
      if (node.isLeaf()) {
        playAgain()
      } else {
        visit(node.left!!)
      }
    }

    noButton.setOnClickListener {
      if (node.isLeaf()) {
        bifurcate()
      } else {
        visit(node.right!!)
      }
    }
  }

  override fun onStart() {
    super.onStart()

    try {
      val file = openFileInput("tree.json")
      val reader = JsonReader(InputStreamReader(file))
      root = Node.read(reader)
      reader.close()
    } catch (e: FileNotFoundException) {
      root = Node("Does it have a tail?", Node("kitten"), Node("egg"))
    }

    visit(root)
  }

  override fun onStop() {
    super.onStop()

    val file = openFileOutput("tree.json", Context.MODE_PRIVATE)
    val jsonWriter = JsonWriter(OutputStreamWriter(file))
    jsonWriter.setIndent("  ")
    root.write(jsonWriter)
    jsonWriter.close()
  }

  private fun visit(node: Node) {
    promptText.text = node.prompt()
    this.node = node
  }

  private fun bifurcate() {
    val builder = AlertDialog.Builder(this)

    val form = layoutInflater.inflate(R.layout.dialog, null, false)
    builder.setView(form)

    val thingBox: EditText = form.findViewById(R.id.thingBox)
    val questionBox: EditText = form.findViewById(R.id.questionBox)
    val questionPrompt: TextView = form.findViewById(R.id.questionPrompt)

    questionPrompt.text = questionPrompt.text.toString().replace("OLD", node.text)

    builder.setPositiveButton("Add") { _, _ ->
      node.bifurcate(thingBox.text.toString(), questionBox.text.toString())
      playAgain()
    }

    builder.show()
  }

  private fun playAgain() {
    val builder = AlertDialog.Builder(this)
    builder.setMessage("Shall we play again?")
    builder.setPositiveButton("Play") { _, _ ->
      visit(root)
    }
    builder.show()
  }
}
