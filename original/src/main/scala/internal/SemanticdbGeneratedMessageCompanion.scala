package dotty.tools.dotc.semanticdb.internal

trait SemanticdbGeneratedOneof extends Any with Product with Serializable {
  type ValueType
  def number: Int
  def isDefined: Boolean
  def isEmpty: Boolean
  def value: ValueType
  def valueOption: Option[ValueType] = if (isDefined) Some(value) else None
}

trait SemanticdbGeneratedMessage extends Any with Product with Serializable {
  def serializedSize: Int
}

trait SemanticdbGeneratedSealedOneof
    extends Any
    with Product
    with Serializable {
  type MessageType <: SemanticdbGeneratedMessage
  def isEmpty: Boolean
  def isDefined: Boolean
  def asMessage: MessageType
}

trait SemanticdbGeneratedEnum extends Any with Product with Serializable {
  type EnumType <: SemanticdbGeneratedEnum

  def value: Int

  def index: Int

  def name: String

  override def toString = name

  def isUnrecognized: Boolean = false

}

trait SemanticdbUnrecognizedEnum extends SemanticdbGeneratedEnum {
  def name = "UNRECOGNIZED"

  def index = -1

  override def isUnrecognized: Boolean = true
}
