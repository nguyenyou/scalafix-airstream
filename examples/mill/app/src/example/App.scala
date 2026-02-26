package example

trait Signal[A] {
  def combineWith[B](other: Signal[B]): Signal[(A, B)] = ???
  def combineWithFn[B, Out](other: Signal[B])(fn: (A, B) => Out): Signal[Out] = ???
}

object Signal {
  def combine[A, B](s1: Signal[A], s2: Signal[B]): Signal[(A, B)] = ???
  def combineWithFn[A, B, Out](s1: Signal[A], s2: Signal[B])(fn: (A, B) => Out): Signal[Out] = ???
}

object App {

  val s1: Signal[Int] = ???
  val s2: Signal[String] = ???

  // Bad: instance method calls — NoCombineWith will flag these
  val bad1 = s1.combineWith(s2)
  val bad2 = s1.combineWithFn(s2)((a, b) => a.toString + b)

  // Good: companion object calls — these are fine
  val ok1 = Signal.combine(s1, s2)
  val ok2 = Signal.combineWithFn(s1, s2)((a, b) => a.toString + b)
}
