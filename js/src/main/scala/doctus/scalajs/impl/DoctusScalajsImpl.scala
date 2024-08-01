package doctus.scalajs.impl

import org.scalajs.dom

import doctus.core._
import doctus.core.util.DoctusPoint
import doctus.scalajs._

private[scalajs] trait DoctusCanvasScalajs1 extends DoctusCanvas {

  dom.window.addEventListener("load", (_: dom.Event) => repaint())

  dom.window.addEventListener("resize", (_: dom.Event) => repaint())

  def elem: dom.HTMLCanvasElement

  private val ctx = elem.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
  ctx.translate(0.5, 0.5)

  var fopt: Option[DoctusGraphics => Unit] = None

  def onRepaint(f: DoctusGraphics => Unit): Unit = fopt = Some(f)

  def repaint(): Unit = fopt foreach (f => f(DoctusGraphicsScalajs(ctx)))

  def width: Int = elem.clientWidth

  def height: Int = elem.clientHeight
}

private[scalajs] trait DoctusDraggableScalajs1
    extends DoctusPointableScalajs1
    with DoctusDraggable {

  def onDrag(f: DoctusPoint => Unit): Unit = em.onDrag(f)

}

private[scalajs] trait DoctusPointableScalajs1 extends DoctusPointable {

  def elem: dom.HTMLElement

  def scrollTopLeft(elem: dom.HTMLElement): (Double, Double) = {
    if (elem.localName.equals("html")) (elem.scrollTop, elem.scrollLeft)
    else
      scrollTopLeft(elem.parentElement) match {
        case (t, l) => (t + elem.scrollTop, l + elem.scrollLeft)
      }
  }

  val em = new DoctusEventManager

  var mousedown = false

  elem.addEventListener(
    "mousedown",
    (e: dom.Event) => {
      // 'mousedown' always produces a MouseEvent
      // Patternmatching can cause problems
      mousedown = true
      val me = e.asInstanceOf[dom.MouseEvent]
      e.preventDefault()
      em.addEvent(MouseDown(point(me)))
    }
  )

  elem.addEventListener(
    "mouseout",
    (e: dom.Event) => {
      // 'mouseup' always produces a MouseEvent
      val me = e.asInstanceOf[dom.MouseEvent]
      e.preventDefault()
      if (mousedown) {
        mousedown = false
        em.addEvent(MouseUp(point(me)))
      }
    }
  )

  elem.addEventListener(
    "mouseup",
    (e: dom.Event) => {
      // 'mouseup' always produces a MouseEvent
      mousedown = false
      val me = e.asInstanceOf[dom.MouseEvent]
      e.preventDefault()
      em.addEvent(MouseUp(point(me)))
    }
  )

  elem.addEventListener(
    "mousemove",
    (e: dom.Event) => {
      // 'mousemove' always produces a MouseEvent
      val me = e.asInstanceOf[dom.MouseEvent]
      e.preventDefault()
      em.addEvent(MouseMove(point(me)))
    }
  )

  elem.addEventListener(
    "touchstart",
    (e: dom.Event) => {
      // 'touchstart' always produces a TouchEvent
      mousedown = true
      val te = e.asInstanceOf[dom.TouchEvent]
      e.preventDefault()
      em.addEvent(TouchStart(idpoints(te)))
    }
  )

  elem.addEventListener(
    "touchend",
    (e: dom.Event) => {
      // 'touchstart' always produces a TouchEvent
      mousedown = false
      val te = e.asInstanceOf[dom.TouchEvent]
      e.preventDefault()
      em.addEvent(TouchEnd(idpoints(te)))
    }
  )

  elem.addEventListener(
    "touchmove",
    (e: dom.Event) => {
      // 'touchstart' always produces a TouchEvent
      val te = e.asInstanceOf[dom.TouchEvent]
      e.preventDefault()
      em.addEvent(TouchMove(idpoints(te)))
    }
  )

  def onStart(f: DoctusPoint => Unit): Unit = {
    em.onStart(f)
  }

  def onStop(f: DoctusPoint => Unit): Unit = {
    em.onStop(f)
  }

  private def point(m: dom.MouseEvent): DoctusPoint = {
    val (st, sl) = scrollTopLeft(elem)
    val x = m.clientX - elem.offsetLeft + sl - 1
    val y = m.clientY - elem.offsetTop + st - 1
    DoctusPoint(x, y)
  }

  private def idpoints(te: dom.TouchEvent): List[DoctusIdPoint] = {
    val tl = te.targetTouches
    (for (i <- 0 until tl.length.intValue) yield {
      val t: dom.Touch = tl(i)
      val x = t.clientX - elem.offsetLeft - 1
      val y = t.clientY - elem.offsetTop - 1
      DoctusIdPoint(t.identifier, DoctusPoint(x, y))
    }).toList
  }

}

trait DoctusKeyScalajs1 extends DoctusKey {

  def elem: dom.Element

  private def mapKeyCode(code: Int): Option[DoctusKeyCode] = {

    if (org.scalajs.dom.KeyCode.Down == code) Some(DKC_Down)
    else if (org.scalajs.dom.KeyCode.Up == code) Some(DKC_Up)
    else if (org.scalajs.dom.KeyCode.Right == code) Some(DKC_Right)
    else if (org.scalajs.dom.KeyCode.Left == code) Some(DKC_Left)
    else if (org.scalajs.dom.KeyCode.Space == code) Some(DKC_Space)
    else if (org.scalajs.dom.KeyCode.Enter == code) Some(DKC_Enter)
    else None
  }

  private var onPressedOpt: Option[DoctusKeyCode => Unit] = None
  private var onReleasedOpt: Option[DoctusKeyCode => Unit] = None

  private var active = false

  dom.window.addEventListener(
    "keydown",
    (e: dom.Event) => {
      val kevent: dom.KeyboardEvent = e.asInstanceOf[dom.KeyboardEvent]
      if (!active) mapKeyCode(kevent.keyCode) match {
        case Some(key) =>
          e.preventDefault()
          e.cancelBubble
          active = true
          onPressedOpt.foreach(f => f(key))
        case None => // Nothing to do
      }
    }
  )

  dom.window.addEventListener(
    "keyup",
    (e: dom.Event) => {
      val kevent: dom.KeyboardEvent = e.asInstanceOf[dom.KeyboardEvent]
      if (active) mapKeyCode(kevent.keyCode) match {
        case Some(key) =>
          e.preventDefault()
          e.cancelBubble
          active = false
          onReleasedOpt.foreach(f => f(key))
        case None => // Nothing to do
      }
    }
  )

  def onKeyPressed(f: DoctusKeyCode => Unit): Unit = onPressedOpt = Some(f)
  def onKeyReleased(f: DoctusKeyCode => Unit): Unit = onReleasedOpt = Some(f)

}
