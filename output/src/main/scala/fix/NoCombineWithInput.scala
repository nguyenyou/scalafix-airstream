package fix

object NoCombineWithInput {

  trait Signal[A] {
    def combineWith[B](other: Signal[B]): Signal[(A, B)] = ???
    def combineWithFn[B, Out](other: Signal[B])(fn: (A, B) => Out): Signal[Out] = ???
  }

  object Signal {
    def combine[A, B](s1: Signal[A], s2: Signal[B]): Signal[(A, B)] = ???
    def combineWithFn[A, B, Out](s1: Signal[A], s2: Signal[B])(fn: (A, B) => Out): Signal[Out] = ???
  }

  trait EventStream[A] {
    def combineWith[B](other: EventStream[B]): EventStream[(A, B)] = ???
    def combineWithFn[B, Out](other: EventStream[B])(fn: (A, B) => Out): EventStream[Out] = ???
  }

  object EventStream {
    def combine[A, B](s1: EventStream[A], s2: EventStream[B]): EventStream[(A, B)] = ???
    def combineWithFn[A, B, Out](s1: EventStream[A], s2: EventStream[B])(fn: (A, B) => Out): EventStream[Out] = ???
  }

  val s1: Signal[Int] = ???
  val s2: Signal[String] = ???
  val e1: EventStream[Int] = ???
  val e2: EventStream[String] = ???

  // Should rewrite — instance method calls
  val bad1 = Signal.combine(s1, s2)
  val bad2 = Signal.combineWithFn(s1, s2)((a, b) => a.toString + b)
  val bad3 = Signal.combine(e1, e2)
  val bad4 = Signal.combineWithFn(e1, e2)((a, b) => a.toString + b)

  // Should NOT rewrite — Signal companion
  val ok1 = Signal.combine(s1, s2)
  val ok2 = Signal.combineWithFn(s1, s2)((a, b) => a.toString + b)

  // Should NOT rewrite — EventStream companion
  val ok3 = EventStream.combine(e1, e2)
  val ok4 = EventStream.combineWithFn(e1, e2)((a, b) => a.toString + b)
}
