// Generated by https://github.com/tanishiking/semanticdb-for-scala3
// Generated by the Scala Plugin for the Protocol Buffer Compiler.
// Do not edit!
//
// Protofile syntax: PROTO3

package dotty.tools.dotc.semanticdb
import dotty.tools.dotc.semanticdb.internal.*
import scala.annotation.internal.sharable

@SerialVersionUID(0L)
final case class SymbolOccurrence(
    range: _root_.scala.Option[dotty.tools.dotc.semanticdb.Range] = _root_.scala.None,
    symbol: _root_.scala.Predef.String = "",
    role: dotty.tools.dotc.semanticdb.SymbolOccurrence.Role = dotty.tools.dotc.semanticdb.SymbolOccurrence.Role.UNKNOWN_ROLE
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
        val __value = symbol
        if (!__value.isEmpty) {
          __size += SemanticdbOutputStream.computeStringSize(2, __value)
        }
      };
      
      {
        val __value = role.value
        if (__value != 0) {
          __size += SemanticdbOutputStream.computeEnumSize(3, __value)
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
        val __v = symbol
        if (!__v.isEmpty) {
          _output__.writeString(2, __v)
        }
      };
      {
        val __v = role.value
        if (__v != 0) {
          _output__.writeEnum(3, __v)
        }
      };
    }
    def getRange: dotty.tools.dotc.semanticdb.Range = range.getOrElse(dotty.tools.dotc.semanticdb.Range.defaultInstance)
    def clearRange: SymbolOccurrence = copy(range = _root_.scala.None)
    def withRange(__v: dotty.tools.dotc.semanticdb.Range): SymbolOccurrence = copy(range = Option(__v))
    def withSymbol(__v: _root_.scala.Predef.String): SymbolOccurrence = copy(symbol = __v)
    def withRole(__v: dotty.tools.dotc.semanticdb.SymbolOccurrence.Role): SymbolOccurrence = copy(role = __v)
    
    
    
    
    // @@protoc_insertion_point(GeneratedMessage[dotty.tools.dotc.semanticdb.SymbolOccurrence])
}

object SymbolOccurrence  extends SemanticdbGeneratedMessageCompanion[dotty.tools.dotc.semanticdb.SymbolOccurrence] {
  implicit def messageCompanion: SemanticdbGeneratedMessageCompanion[dotty.tools.dotc.semanticdb.SymbolOccurrence] = this
  def parseFrom(`_input__`: SemanticdbInputStream): dotty.tools.dotc.semanticdb.SymbolOccurrence = {
    var __range: _root_.scala.Option[dotty.tools.dotc.semanticdb.Range] = _root_.scala.None
    var __symbol: _root_.scala.Predef.String = ""
    var __role: dotty.tools.dotc.semanticdb.SymbolOccurrence.Role = dotty.tools.dotc.semanticdb.SymbolOccurrence.Role.UNKNOWN_ROLE
    var _done__ = false
    while (!_done__) {
      val _tag__ = _input__.readTag()
      _tag__ match {
        case 0 => _done__ = true
        case 10 =>
          __range = Option(__range.fold(LiteParser.readMessage[dotty.tools.dotc.semanticdb.Range](_input__))(LiteParser.readMessage(_input__, _)))
        case 18 =>
          __symbol = _input__.readStringRequireUtf8()
        case 24 =>
          __role = dotty.tools.dotc.semanticdb.SymbolOccurrence.Role.fromValue(_input__.readEnum())
        case tag => _input__.skipField(tag)
      }
    }
    dotty.tools.dotc.semanticdb.SymbolOccurrence(
        range = __range,
        symbol = __symbol,
        role = __role
    )
  }
  
  
  
  
  
  
  lazy val defaultInstance = dotty.tools.dotc.semanticdb.SymbolOccurrence(
    range = _root_.scala.None,
    symbol = "",
    role = dotty.tools.dotc.semanticdb.SymbolOccurrence.Role.UNKNOWN_ROLE
  )
  sealed abstract class Role(val value: _root_.scala.Int)  extends SemanticdbGeneratedEnum  derives CanEqual {
    type EnumType = Role
    def isUnknownRole: _root_.scala.Boolean = false
    def isReference: _root_.scala.Boolean = false
    def isDefinition: _root_.scala.Boolean = false
    
    final def asRecognized: _root_.scala.Option[dotty.tools.dotc.semanticdb.SymbolOccurrence.Role.Recognized] = if (isUnrecognized) _root_.scala.None else _root_.scala.Some(this.asInstanceOf[dotty.tools.dotc.semanticdb.SymbolOccurrence.Role.Recognized])
  }
  
  object Role  {
    sealed trait Recognized extends Role
    
    
    @SerialVersionUID(0L)
    case object UNKNOWN_ROLE extends Role(0) with Role.Recognized {
      val index = 0
      val name = "UNKNOWN_ROLE"
      override def isUnknownRole: _root_.scala.Boolean = true
    }
    
    @SerialVersionUID(0L)
    case object REFERENCE extends Role(1) with Role.Recognized {
      val index = 1
      val name = "REFERENCE"
      override def isReference: _root_.scala.Boolean = true
    }
    
    @SerialVersionUID(0L)
    case object DEFINITION extends Role(2) with Role.Recognized {
      val index = 2
      val name = "DEFINITION"
      override def isDefinition: _root_.scala.Boolean = true
    }
    
    @SerialVersionUID(0L)
    final case class Unrecognized(unrecognizedValue: _root_.scala.Int)  extends Role(unrecognizedValue) with SemanticdbUnrecognizedEnum
    lazy val values = scala.collection.immutable.Seq(UNKNOWN_ROLE, REFERENCE, DEFINITION)
    def fromValue(__value: _root_.scala.Int): Role = __value match {
      case 0 => UNKNOWN_ROLE
      case 1 => REFERENCE
      case 2 => DEFINITION
      case __other => Unrecognized(__other)
    }
    
    
  }
  final val RANGE_FIELD_NUMBER = 1
  final val SYMBOL_FIELD_NUMBER = 2
  final val ROLE_FIELD_NUMBER = 3
  def of(
    range: _root_.scala.Option[dotty.tools.dotc.semanticdb.Range],
    symbol: _root_.scala.Predef.String,
    role: dotty.tools.dotc.semanticdb.SymbolOccurrence.Role
  ): _root_.dotty.tools.dotc.semanticdb.SymbolOccurrence = _root_.dotty.tools.dotc.semanticdb.SymbolOccurrence(
    range,
    symbol,
    role
  )
  // @@protoc_insertion_point(GeneratedMessageCompanion[dotty.tools.dotc.semanticdb.SymbolOccurrence])
}
