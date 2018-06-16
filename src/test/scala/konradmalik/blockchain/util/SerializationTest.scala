package konradmalik.blockchain.util

import java.io.NotSerializableException

import org.scalatest.{FlatSpec, Matchers}

case class Case()

class SerClass() extends Serializable

class NotSerClass()

class SerializationTest extends FlatSpec with Matchers {
  "Serialization" should "serialize an object and deserialize it." in {
    Serialization.deserialize(Serialization.serialize("abc")) shouldEqual "abc"
    Serialization.deserialize(Serialization.serialize(123)) shouldEqual 123
    Serialization.deserialize(Serialization.serialize(Case())) shouldEqual Case()
    Serialization.deserialize(Serialization.serialize(new SerClass())) shouldBe an[SerClass]
    a[NotSerializableException] should be thrownBy Serialization.serialize(new NotSerClass())
  }

}