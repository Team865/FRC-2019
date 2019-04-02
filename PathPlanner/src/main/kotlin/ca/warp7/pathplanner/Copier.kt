package ca.warp7.pathplanner

import java.awt.Frame
import java.awt.TextArea
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent


fun showForCopy(s: String) {
    val data = StringSelection(s)
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(data, data)
    val f = Frame()
    val area = TextArea(s)
    area.setBounds(10, 30, 300, 300)
    f.add(area)
    f.setSize(300, 300)
    f.layout = null
    f.isVisible = true
    f.addWindowListener(object : WindowAdapter() {
        override fun windowClosing(we: WindowEvent) {
            f.dispose()
        }
    })
}