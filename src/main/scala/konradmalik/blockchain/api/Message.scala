package konradmalik.blockchain.api

final case class Input(content: String)

sealed trait Message
final case class SuccessMsg(rId: Long, message: String) extends Message
final case class FailureMsg(rId: Long, error: String) extends Message
