package doctus.scalajs

import utest._
import doctus.core.util._

object EventManagerSuite extends TestSuite {

  class Collector {
    private var result = List.empty[Char]
    def a(p: DoctusPoint): Unit = result ::= 'A'
    def b(p: DoctusPoint): Unit = result ::= 'B'
    def c(p: DoctusPoint): Unit = result ::= 'C'
    def resultString: String = result.reverse.mkString("")
  }

  def tests: Tests = utest.Tests {

    test("pointing one finger") {

      val col = new Collector

      val m = new DoctusEventManager

      m.onStart(col.a)
      m.onStop(col.b)

      m.addEvent(TouchStart(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
      m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
      m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
      m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
      m.addEvent(TouchEnd(List.empty[DoctusIdPoint]))

      assert("AB" == col.resultString)
    }

    test("dragging one finger") {
      test {
        val m = new DoctusEventManager

        val col = new Collector

        m.onStart(col.a)
        m.onDrag(col.b)
        m.onStop(col.c)

        m.addEvent(TouchStart(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchEnd(List.empty[DoctusIdPoint]))

        assert("ABBBC" == col.resultString)
      }

      test {
        val m = new DoctusEventManager

        val col = new Collector

        m.onStart(col.a)
        m.onDrag(col.b)
        m.onStop(col.c)

        m.addEvent(TouchStart(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 10)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 20)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 20)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 20)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 20)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 30)))))
        m.addEvent(TouchEnd(List.empty[DoctusIdPoint]))

        assert("ABBBBBBC" == col.resultString)
      }

    }
    test("intertouch with one finger") {
      test {
        val m = new DoctusEventManager

        val col = new Collector

        m.onStart(col.a)
        m.onDrag(col.b)
        m.onStop(col.c)

        m.addEvent(TouchStart(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))

        m.addEvent(TouchStart(List(DoctusIdPoint(0, DoctusPoint(0, 0)), DoctusIdPoint(1, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)), DoctusIdPoint(1, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)), DoctusIdPoint(1, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)), DoctusIdPoint(1, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)), DoctusIdPoint(1, DoctusPoint(0, 0)))))

        m.addEvent(TouchEnd(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))

        m.addEvent(TouchEnd(List.empty[DoctusIdPoint]))

        val re = col.resultString
        assert("ABBBBBBBBBBC" == re)
      }
      test {
        val m = new DoctusEventManager

        val col = new Collector

        m.onStart(col.a)
        m.onDrag(col.b)
        m.onStop(col.c)

        m.addEvent(TouchStart(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))

        m.addEvent(TouchStart(List(DoctusIdPoint(0, DoctusPoint(0, 0)), DoctusIdPoint(1, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)), DoctusIdPoint(1, DoctusPoint(0, 0)))))

        m.addEvent(TouchEnd(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))

        m.addEvent(TouchEnd(List.empty[DoctusIdPoint]))

        val re = col.resultString
        assert("ABBBC" == re)
      }
      test {
        val m = new DoctusEventManager

        val col = new Collector

        m.onStart(col.a)
        m.onDrag(col.b)
        m.onStop(col.c)

        m.addEvent(TouchStart(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))

        m.addEvent(TouchStart(List(DoctusIdPoint(0, DoctusPoint(0, 0)), DoctusIdPoint(1, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)), DoctusIdPoint(1, DoctusPoint(0, 0)))))

        m.addEvent(TouchEnd(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))

        m.addEvent(TouchEnd(List.empty[DoctusIdPoint]))

        val re = col.resultString
        assert("ABC" == re)
      }
    }
    test("altertouch with one finger") {
      test {
        val m = new DoctusEventManager

        val col = new Collector

        m.onStart(col.a)
        m.onDrag(col.b)
        m.onStop(col.c)

        m.addEvent(TouchStart(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)))))

        m.addEvent(TouchStart(List(DoctusIdPoint(0, DoctusPoint(0, 0)), DoctusIdPoint(1, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)), DoctusIdPoint(1, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)), DoctusIdPoint(1, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)), DoctusIdPoint(1, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(0, DoctusPoint(0, 0)), DoctusIdPoint(1, DoctusPoint(0, 0)))))

        m.addEvent(TouchEnd(List(DoctusIdPoint(1, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(1, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(1, DoctusPoint(0, 0)))))
        m.addEvent(TouchMove(List(DoctusIdPoint(1, DoctusPoint(0, 0)))))

        m.addEvent(TouchEnd(List.empty[DoctusIdPoint]))

        val re = col.resultString
        assert("ABBBBBBBC" == re)
      }
    }
  }
}