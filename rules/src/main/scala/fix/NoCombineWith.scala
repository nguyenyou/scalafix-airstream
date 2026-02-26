package fix

import scala.meta._
import scalafix.v1._
import scalafix.lint.Diagnostic

class NoCombineWith extends SyntacticRule("NoCombineWith") {

  override def description: String =
    "Forbids instance methods .combineWith() and .combineWithFn(); " +
      "use Signal.combine() / EventStream.combine() companions instead"

  override def isLinter: Boolean = true

  private val methods = Set("combineWith", "combineWithFn")
  private val companions = Set("Signal", "EventStream")

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(qual, Term.Name(name)) if methods.contains(name) =>
        qual match {
          case Term.Name(q) if companions.contains(q) => Patch.empty
          case _ =>
            val obj = "Signal.combine or EventStream.combine"
            val replacement =
              if (name == "combineWith") obj
              else "Signal.combineWithFn or EventStream.combineWithFn"
            Patch.lint(
              Diagnostic("", s"Use $replacement instead of .$name", t.pos)
            )
        }
    }.asPatch
  }
}
