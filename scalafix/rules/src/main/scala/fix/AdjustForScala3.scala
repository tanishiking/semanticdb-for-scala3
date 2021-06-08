package fix

import scalafix.v1._
import scala.meta._
import java.util.regex.Pattern

class AdjustForScala3 extends SyntacticRule("AdjustForScala3") {
  private val companionPattern = Pattern.compile(
    ".*companion.*",
    Pattern.CASE_INSENSITIVE
  )
  private val descriptorPattern = Pattern.compile(
    ".*descriptor.*",
    Pattern.CASE_INSENSITIVE
  )
  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tree.collect {
      case annot @ Mod.Annot(Init(Type.Name(name), _, _))
          if name == "transient" =>
        Patch.replaceTree(annot, "@sharable")

      case annot @ Mod.Annot(Init(Type.Name(name), _, _))
          if name == "SerialVersionUID" =>
        Patch.replaceTree(annot, "")

      case select @ Type.Select(_, name) if name.value == "TypeMapper" =>
        Patch.replaceTree(select, "SemanticdbTypeMapper")

      // remove getter
      case d: Defn.Def if d.name.value.startsWith("getField") =>
        Patch.replaceTree(d, "")

      case d: Defn.Def if d.name.value.startsWith("messageReads") =>
        Patch.replaceTree(d, "")

      case t @ Term.Select(_, name) if name.value == "CodedOutputStream" =>
        Patch.replaceTree(t, "SemanticdbOutputStream")
      case t @ Type.Select(_, name) if name.value == "CodedOutputStream" =>
        Patch.replaceTree(t, "SemanticdbOutputStream")

      // Remove companion accessor
      case d: Defn.Def if companionPattern.matcher(d.name.value).matches() =>
        Patch.replaceTree(d, "")
      case v: Defn.Val
          if v.pats
            .exists(pat => companionPattern.matcher(pat.syntax).matches()) =>
        Patch.replaceTree(v, "")

      // Remove descriptor
      case d: Defn.Def if descriptorPattern.matcher(d.name.value).matches() =>
        Patch.replaceTree(d, "")
      case v: Defn.Val
          if v.pats
            .exists(pat => descriptorPattern.matcher(pat.syntax).matches()) =>
        Patch.replaceTree(v, "")

      case d: Defn.Def
          if d.name.value == "parseFrom" || d.name.value == "toProtoString" || d.name.value == "asRecognized" =>
        Patch.replaceTree(d, "")

      // Remove extends from scalapb
      case defn: Defn.Trait =>
        val derive = deriveCanEqual(defn.templ)
        if (extendsScalapbClass(defn.templ))
          removeScalapbExtends(defn.templ) + derive
        else derive
      case defn: Defn.Class =>
        val derive = deriveCanEqual(defn.templ)
        if (extendsScalapbClass(defn.templ))
          removeScalapbExtends(defn.templ) + derive
        else derive
      case defn: Defn.Object if extendsScalapbClass(defn.templ) =>
        removeScalapbExtends(defn.templ)
    }.asPatch

  }

  private def deriveCanEqual(templ: Template): Patch = {
    templ.tokens
      .find(tok => tok.isInstanceOf[Token.LeftBrace])
      .map { lbrace =>
        Patch.addLeft(lbrace, " derives CanEqual ")
      }
      .getOrElse(Patch.empty)
  }

  private def extendsScalapbClass(templ: Template): Boolean =
    templ.inits.exists(init => isFromScalaPb(init.tpe))

  private def removeScalapbExtends(templ: Template): Patch = {
    templ.inits.lastOption
      .map { last =>
        val remain: List[String] =
          templ.inits.filterNot(init => isFromScalaPb(init.tpe)).map(_.syntax)
        val replace = templ.inits.flatMap { init =>
          init.tpe match {
            case s: Type.Select =>
              extendsReplaces.get(s.name.value)
            case _ => None
          }
        }
        val newExtends = remain ++ replace
        val extendsToks = templ.tokens.takeWhile(t => t.pos.end <= last.pos.end)
        Patch.removeTokens(extendsToks) +
          Patch.addRight(
            last,
            newExtends
              .mkString(
                if (newExtends.nonEmpty) " extends " else "",
                " with ",
                ""
              )
          )
      }
      .getOrElse(Patch.empty)
  }

  private val extendsReplaces = Map(
    "GeneratedOneof" -> "SemanticdbGeneratedOneof",
    "GeneratedMessage" -> "SemanticdbGeneratedMessage",
    "GeneratedSealedOneof" -> "SemanticdbGeneratedSealedOneof",
    "GeneratedEnum" -> "SemanticdbGeneratedEnum",
    "UnrecognizedEnum" -> "SemanticdbUnrecognizedEnum"
  )

  private def tryIsFromScalaPb(tpe: Option[Type]): Boolean = {
    tpe.map(isFromScalaPb).getOrElse(false)
  }

  private def isFromScalaPb(tpe: Type): Boolean = {
    def loop(typ: Tree): Boolean = {
      typ match {
        case Type.Name(name) => name == "scalapb"
        case Term.Name(name) => name == "scalapb"
        case Type.Select(qual, name) =>
          name.value == "scalapb" || loop(qual)
        case Term.Select(qual, name) =>
          name.value == "scalapb" || loop(qual)
        case Type.Apply(tpe, args) => loop(tpe) || args.exists(loop)
        case _                     => false
      }
    }
    loop(tpe)
  }
}
