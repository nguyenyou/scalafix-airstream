package fix

import scalafix.testkit._
import org.scalatest.funsuite.AnyFunSuiteLike

class NoCombineWithSuite extends AbstractSemanticRuleSuite with AnyFunSuiteLike {
  runAllTests()
}
