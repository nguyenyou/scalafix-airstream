package fix

import scala.meta._
import scalafix.v1._
import scalafix.lint.Diagnostic

class NoCombineWith extends SyntacticRule("NoCombineWith") {

  override def description: String =
    "Forbids instance methods .combineWith() and .combineWithFn() on Signal; " +
      "use Signal.combine() / Signal.combineWithFn() instead"

  override def isLinter: Boolean = true

  private val methods = Set("combineWith", "combineWithFn")

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Select(qual, Term.Name(name)) if methods.contains(name) =>
        qual match {
          case Term.Name("Signal") => Patch.empty
          case _ =>
            val replacement =
              if (name == "combineWith") "Signal.combine"
              else "Signal.combineWithFn"
            Patch.lint(
              Diagnostic("", s"Use $replacement instead of .$name", t.pos)
            )
        }
    }.asPatch
  }
}
