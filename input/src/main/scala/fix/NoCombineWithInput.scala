/*
rules = NoCombineWith
*/
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

  val s1: Signal[Int] = ???
  val s2: Signal[String] = ???

  // Should trigger lint — instance method calls
  val bad1 = s1.combineWith(s2) // assert: NoCombineWith
  val bad2 = s1.combineWithFn(s2)((a, b) => a.toString + b) // assert: NoCombineWith

  // Method result as qualifier
  def getSignal: Signal[Int] = ???
  val bad3 = getSignal.combineWith(s2) // assert: NoCombineWith

  // Syntactic rule triggers on any qualifier (can't distinguish types)
  trait Foo { def combineWith(x: Int): Int = ??? }
  val foo: Foo = ???
  val bad4 = foo.combineWith(1) // assert: NoCombineWith

  // Should NOT trigger lint — companion object calls
  val ok1 = Signal.combine(s1, s2)
  val ok2 = Signal.combineWithFn(s1, s2)((a, b) => a.toString + b)
}
