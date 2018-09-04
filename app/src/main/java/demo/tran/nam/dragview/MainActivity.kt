package demo.tran.nam.dragview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity(), DragView.onDragViewListener {
    override fun onDragCancel() {
        Toast.makeText(this,"onDragCancel",Toast.LENGTH_SHORT).show()
    }

    override fun onDragStart() {
        Toast.makeText(this,"onDragStart",Toast.LENGTH_SHORT).show()
    }

    override fun onDragWrong() {
        Toast.makeText(this,"onDragWrong",Toast.LENGTH_SHORT).show()
    }

    override fun onDragSuccess() {
        Toast.makeText(this,"onDragSuccess",Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val data = ArrayList<String>()
        data.add("text")
        data.add("abc")
        data.add("def")
        findViewById<DragView>(R.id.drag_view).onDragListener = this
        findViewById<DragView>(R.id.drag_view).setData(data)
    }
}
