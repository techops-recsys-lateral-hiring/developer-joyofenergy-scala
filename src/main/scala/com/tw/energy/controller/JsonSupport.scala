package com.tw.energy.controller

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{HttpCharsets, MediaTypes}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.http.scaladsl.util.FastFuture
import akka.util.ByteString
import io.circe.{Decoder, Encoder, Error}
import io.circe.syntax.*
import io.circe.parser.decode

import java.nio.charset.StandardCharsets

/**
 * Rudimentary support for handling json marshalling and unmarshalling JSON using Circe. To use,
 * ensure that `Encoder` and `Decoder` implicits are available for the type you want to handle. For example,
 * by `import io.circe.generic.auto.*` to gain automatic encoder and decoder generation for case classes.
 *
 * Inspired by Heiko Seeberger's code at https://github.com/hseeberger/akka-http-json/tree/master/akka-http-circe/src - but
 * which doesn't yet support Scala 3 out of the box.
 */
trait JsonSupport {

  implicit def jsonToEntityMarshaller[A](implicit encoder: Encoder[A]): ToEntityMarshaller[A] =
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)((a:A) => a.asJson.noSpaces)

  implicit def jsonFromEntityUnmarshaller[A](implicit decoder: Decoder[A]): FromEntityUnmarshaller[A] = {
    Unmarshaller
      .byteStringUnmarshaller
      .forContentTypes(MediaTypes.`application/json`)
      .andThen(stringViaDecoderUnmarshaller)
  }

  private def stringViaDecoderUnmarshaller[A](implicit decoder: Decoder[A]): Unmarshaller[ByteString, A] =
    Unmarshaller.withMaterializer[ByteString, A](_ => _ => { bs =>
      decode[A](bs.decodeString(StandardCharsets.UTF_8)).fold(
        (error: Error) => FastFuture.failed(error),
        (a: A) => FastFuture.successful(a)
      )
    })

}