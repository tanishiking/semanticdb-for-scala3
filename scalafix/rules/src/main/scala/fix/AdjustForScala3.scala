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
      // Replace `private[this]` in compiler with simply `private`
      case priv: Mod.Private if priv.within.isInstanceOf[Term.This] =>
        Patch.replaceTree(priv, "private")
      // Replace `._` import with `.*`
      case im: Importee.Wildcard =>
        Patch.replaceTree(im, "*")
      // Replace `@transient` annotation with `@sharable`
      // https://github.com/scalameta/metals/discussions/2593#discussioncomment-529949
      // https://github.com/scalapb/ScalaPB/blob/1159f1738efcb4cb0a620a4e6f14f6489710b5d1/compiler-plugin/src/main/scala/scalapb/compiler/ProtobufGenerator.scala#L544
      case annot @ Mod.Annot(Init(Type.Name(name), _, _))
          if name == "transient" =>
        Patch.addRight(annot, " @sharable")

      // Replace `_root_.scalapb.TypeMapper` with `SemanticdbTypeMapper`
      // to remove the dependency to `scalapb-runtime`
      // https://github.com/scalapb/ScalaPB/blob/1159f1738efcb4cb0a620a4e6f14f6489710b5d1/compiler-plugin/src/main/scala/scalapb/compiler/ProtobufGenerator.scala#L972-L990
      case select @ Type.Select(_, name) if name.value == "TypeMapper" =>
        Patch.replaceTree(select, "SemanticdbTypeMapper")

      // Remove getter whose signature is
      // `def getField(__field: _root_.scalapb.descriptors.FieldDescriptor): _root_.scalapb.descriptors.PValue`
      // to remove dependency to `scalapb-runtime`
      // https://github.com/scalapb/ScalaPB/blob/1159f1738efcb4cb0a620a4e6f14f6489710b5d1/compiler-plugin/src/main/scala/scalapb/compiler/ProtobufGenerator.scala#L396
      case d: Defn.Def if d.name.value.startsWith("getField") =>
        Patch.replaceTree(d, "")

      // Remove messageReads
      // https://github.com/scalapb/ScalaPB/blob/1159f1738efcb4cb0a620a4e6f14f6489710b5d1/compiler-plugin/src/main/scala/scalapb/compiler/ProtobufGenerator.scala#L808
      case d: Defn.Def if d.name.value.startsWith("messageReads") =>
        Patch.replaceTree(d, "")

      // Replace `_root_.com.google.protobuf.Coded(Input|Output)Stream` with
      // `Semanticdb(Input|Output)Stream` to drop the dependency to `com.google.protobuf`
      case t @ Term.Select(_, name) if name.value == "CodedInputStream" =>
        Patch.replaceTree(t, "SemanticdbInputStream")
      case t @ Type.Select(_, name) if name.value == "CodedInputStream" =>
        Patch.replaceTree(t, "SemanticdbInputStream")

      case t @ Term.Select(_, name) if name.value == "CodedOutputStream" =>
        Patch.replaceTree(t, "SemanticdbOutputStream")
      case t @ Type.Select(_, name) if name.value == "CodedOutputStream" =>
        Patch.replaceTree(t, "SemanticdbOutputStream")

      case s @ Term.Select(_, qual) if qual.value == "LiteParser" =>
        Patch.replaceTree(s, "LiteParser")

      case d: Defn.Def
          if companionPattern
            .matcher(d.name.value)
            .matches() =>
        if (d.name.value == "messageCompanion") {
          d.collect {
            case s @ Type.Select(_, name)
                if name.value == "GeneratedMessageCompanion" =>
              Patch.replaceTree(s, "SemanticdbGeneratedMessageCompanion")
          }.asPatch
        } else {
          Patch.replaceTree(d, "")
        }
      case v: Defn.Val
          if v.pats
            .exists(pat => companionPattern.matcher(pat.syntax).matches()) =>
        Patch.replaceTree(v, "")

      // Remove descriptor which depends on `scalapb` and `com.google.protobuf`
      // https://developers.google.com/protocol-buffers/docs/reference/cpp/google.protobuf.descriptor
      // https://github.com/scalapb/ScalaPB/blob/1159f1738efcb4cb0a620a4e6f14f6489710b5d1/compiler-plugin/src/main/scala/scalapb/compiler/ProtobufGenerator.scala#L1471-L1524
      case d: Defn.Def if descriptorPattern.matcher(d.name.value).matches() =>
        Patch.replaceTree(d, "")
      case v: Defn.Val
          if v.pats
            .exists(pat => descriptorPattern.matcher(pat.syntax).matches()) =>
        Patch.replaceTree(v, "")

      // Remove `toProtoString` that depends on `scalapb.TextFormat`
      // https://github.com/scalapb/ScalaPB/blob/1159f1738efcb4cb0a620a4e6f14f6489710b5d1/compiler-plugin/src/main/scala/scalapb/compiler/ProtobufGenerator.scala#L1390-L1392
      case d: Defn.Def if d.name.value == "toProtoString" =>
        Patch.replaceTree(d, "")

      // - Remove extends from scalapb classes
      //   - from `object MethodSignature extends scalapb.GeneratedMessageCompanion[dotty.tools.dotc.semanticdb.MethodSignature]`
      //   - to `object MethodSignature`
      // - Replace scalapb classes into our hand-crafted classes
      //   - see `extendsReplaces`
      // - Add `derives CanEqual` to classes and traits
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
    }.asPatch + addGeneratedComment(doc)
  }

  private def addGeneratedComment(doc: SyntacticDocument): Patch = {
    Patch.addLeft(
      doc.tokens.head,
      "// Generated by https://github.com/tanishiking/semanticdb-for-scala3\n"
    )
  }

  private val extendsReplaces = Map(
    "GeneratedOneof" -> "SemanticdbGeneratedOneof",
    "GeneratedMessage" -> "SemanticdbGeneratedMessage",
    "GeneratedSealedOneof" -> "SemanticdbGeneratedSealedOneof",
    "GeneratedEnum" -> "SemanticdbGeneratedEnum",
    "UnrecognizedEnum" -> "SemanticdbUnrecognizedEnum"
  )

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
            case app @ Type.Apply(Type.Select(_, name), _)
                if name.value == "GeneratedMessageCompanion" =>
              Some(
                app.syntax.replaceAll(
                  "scalapb.GeneratedMessageCompanion",
                  "SemanticdbGeneratedMessageCompanion"
                )
              )
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

  private def tryIsFromScalaPb(tpe: Option[Type]): Boolean = {
    tpe.map(isFromScalaPb).getOrElse(false)
  }

  private def isFromScalaPb(tpe: Tree): Boolean = {
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
