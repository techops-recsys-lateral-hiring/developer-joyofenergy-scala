package com.tw.energy.controller

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{HttpCharsets, MediaTypes}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.http.scaladsl.util.FastFuture
import akka.util.ByteString
import io.circe.{Decoder, Encoder, Error}
import io.circe.syntax.*
import io.circe.parser.decode

trait JsonSupport {

  implicit def jsonToEntityMarshaller[A](implicit encoder: Encoder[A]): ToEntityMarshaller[A] = {
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)((a:A) => a.asJson.noSpaces)
  }

  implicit def jsonFromEntityUnmarshaller[A](implicit decoder: Decoder[A]): FromEntityUnmarshaller[A] = {
    Unmarshaller.byteStringUnmarshaller.forContentTypes(MediaTypes.`application/json`).andThen(Unmarshaller.withMaterializer[ByteString, A](_ => _ => { bs =>
      decode[A](bs.decodeString(HttpCharsets.`UTF-8`.nioCharset)).fold(
        (error: Error) => FastFuture.failed(error),
        (a:A) => FastFuture.successful(a)
      )
    }))
  }

}