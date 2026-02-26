package fix

import scala.meta._
import scalafix.v1._
import metaconfig.{Configured, ConfDecoder}
import metaconfig.generic.{Surface, deriveSurface, deriveDecoder}

case class NoCombineWithConfig(
    defaultCompanion: String = "Signal"
)

object NoCombineWithConfig {
  val default: NoCombineWithConfig = NoCombineWithConfig()
  implicit val surface: Surface[NoCombineWithConfig] = deriveSurface[NoCombineWithConfig]
  implicit val decoder: ConfDecoder[NoCombineWithConfig] = deriveDecoder[NoCombineWithConfig](default)
}

class NoCombineWith(config: NoCombineWithConfig) extends SyntacticRule("NoCombineWith") {

  def this() = this(NoCombineWithConfig.default)

  override def withConfiguration(conf: Configuration): Configured[Rule] =
    conf.conf
      .getOrElse("NoCombineWith")(NoCombineWithConfig.default)
      .map(new NoCombineWith(_))

  override def description: String =
    "Rewrites instance methods .combineWith() and .combineWithFn() to " +
      "companion object methods Signal.combine() / EventStream.combine()"

  private val companions = Set("Signal", "EventStream")
  private val comp = config.defaultCompanion

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      // receiver.combineWithFn(args...)(fn...)
      case t @ Term.Apply(
            Term.Apply(Term.Select(qual, Term.Name("combineWithFn")), signalArgs),
            fnArgs
          ) if !isCompanionCall(qual) =>
        val sigStr = (qual +: signalArgs).map(_.syntax).mkString(", ")
        val fnStr = fnArgs.map(_.syntax).mkString(", ")
        Patch.replaceTree(t, s"$comp.combineWithFn($sigStr)($fnStr)")

      // receiver.combineWith(args...)
      case t @ Term.Apply(Term.Select(qual, Term.Name("combineWith")), args)
          if !isCompanionCall(qual) =>
        val allArgs = (qual +: args).map(_.syntax).mkString(", ")
        Patch.replaceTree(t, s"$comp.combine($allArgs)")
    }.asPatch
  }

  private def isCompanionCall(qual: Term): Boolean = qual match {
    case Term.Name(q) => companions.contains(q)
    case _            => false
  }
}
