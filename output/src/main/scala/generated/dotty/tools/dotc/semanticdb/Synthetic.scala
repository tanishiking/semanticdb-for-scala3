// Generated by https://github.com/tanishiking/semanticdb-for-scala3
// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package dotty.tools.dotc.semanticdb
import dotty.tools.dotc.semanticdb.internal.*
import scala.annotation.internal.sharable

@SerialVersionUID(0L)
final case class Synthetic(
    range: _root_.scala.Option[dotty.tools.dotc.semanticdb.Range] = _root_.scala.None,
    tree: dotty.tools.dotc.semanticdb.Tree = dotty.tools.dotc.semanticdb.Synthetic._typemapper_tree.toCustom(dotty.tools.dotc.semanticdb.TreeMessage.defaultInstance)
    )  extends SemanticdbGeneratedMessage  derives CanEqual {
    @transient @sharable
    private var __serializedSizeMemoized: _root_.scala.Int = 0
    private def __computeSerializedSize(): _root_.scala.Int = {
      var __size = 0
      if (range.isDefined) {
        val __value = range.get
        __size += 1 + SemanticdbOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
      };
      
      {
        val __value = dotty.tools.dotc.semanticdb.Synthetic._typemapper_tree.toBase(tree)
        if (__value.serializedSize != 0) {
          __size += 1 + SemanticdbOutputStream.computeUInt32SizeNoTag(__value.serializedSize) + __value.serializedSize
        }
      };
      __size
    }
    override def serializedSize: _root_.scala.Int = {
      var __size = __serializedSizeMemoized
      if (__size == 0) {
        __size = __computeSerializedSize() + 1
        __serializedSizeMemoized = __size
      }
      __size - 1
      
    }
    def writeTo(`_output__`: SemanticdbOutputStream): _root_.scala.Unit = {
      range.foreach { __v =>
        val __m = __v
        _output__.writeTag(1, 2)
        _output__.writeUInt32NoTag(__m.serializedSize)
        __m.writeTo(_output__)
      };
      {
        val __v = dotty.tools.dotc.semanticdb.Synthetic._typemapper_tree.toBase(tree)
        if (__v.serializedSize != 0) {
          _output__.writeTag(2, 2)
          _output__.writeUInt32NoTag(__v.serializedSize)
          __v.writeTo(_output__)
        }
      };
    }
    def getRange: dotty.tools.dotc.semanticdb.Range = range.getOrElse(dotty.tools.dotc.semanticdb.Range.defaultInstance)
    def clearRange: Synthetic = copy(range = _root_.scala.None)
    def withRange(__v: dotty.tools.dotc.semanticdb.Range): Synthetic = copy(range = Option(__v))
    def withTree(__v: dotty.tools.dotc.semanticdb.Tree): Synthetic = copy(tree = __v)
    
    
    
    
    // @@protoc_insertion_point(GeneratedMessage[dotty.tools.dotc.semanticdb.Synthetic])
}

object Synthetic  extends SemanticdbGeneratedMessageCompanion[dotty.tools.dotc.semanticdb.Synthetic] {
  implicit def messageCompanion: SemanticdbGeneratedMessageCompanion[dotty.tools.dotc.semanticdb.Synthetic] = this
  def parseFrom(`_input__`: SemanticdbInputStream): dotty.tools.dotc.semanticdb.Synthetic = {
    var __range: _root_.scala.Option[dotty.tools.dotc.semanticdb.Range] = _root_.scala.None
    var __tree: _root_.scala.Option[dotty.tools.dotc.semanticdb.TreeMessage] = _root_.scala.None
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 10 =>
          __range = Option(__range.fold(LiteParser.readMessage[dotty.tools.dotc.semanticdb.Range](_input__))(LiteParser.readMessage(_input__, _)))
        case 18 =>
          __tree = _root_.scala.Some(__tree.fold(LiteParser.readMessage[dotty.tools.dotc.semanticdb.TreeMessage](_input__))(LiteParser.readMessage(_input__, _)))
        case tag => _input__.skipField(tag)
      }
    }
    dotty.tools.dotc.semanticdb.Synthetic(
        range = __range,
        tree = dotty.tools.dotc.semanticdb.Synthetic._typemapper_tree.toCustom(__tree.getOrElse(dotty.tools.dotc.semanticdb.TreeMessage.defaultInstance))
    )
  }
  
  
  
  
  
  
  lazy val defaultInstance = dotty.tools.dotc.semanticdb.Synthetic(
    range = _root_.scala.None,
    tree = dotty.tools.dotc.semanticdb.Synthetic._typemapper_tree.toCustom(dotty.tools.dotc.semanticdb.TreeMessage.defaultInstance)
  )
  final val RANGE_FIELD_NUMBER = 1
  final val TREE_FIELD_NUMBER = 2
  @transient @sharable
  private[semanticdb] val _typemapper_tree: SemanticdbTypeMapper[dotty.tools.dotc.semanticdb.TreeMessage, dotty.tools.dotc.semanticdb.Tree] = implicitly[SemanticdbTypeMapper[dotty.tools.dotc.semanticdb.TreeMessage, dotty.tools.dotc.semanticdb.Tree]]
  def of(
    range: _root_.scala.Option[dotty.tools.dotc.semanticdb.Range],
    tree: dotty.tools.dotc.semanticdb.Tree
  ): _root_.dotty.tools.dotc.semanticdb.Synthetic = _root_.dotty.tools.dotc.semanticdb.Synthetic(
    range,
    tree
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[dotty.tools.dotc.semanticdb.Synthetic])
}
