package com.tw.energy.domain

import io.circe.parser.decode
import org.scalatest.freespec._
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import squants.market.{EUR, Money}
import io.circe.syntax._

class SquantsJsonSupportTest extends AnyFreeSpec with TableDrivenPropertyChecks with Matchers with SquantsJsonSupport {

  "SquantsJsonSupport" - {
    "should decode instance of money:" - {
      val inputs =
        Table(
          ("amount", "currency", "result"),
          (1, "EUR", EUR(1)),
          (-1, "EUR", EUR(-1)),
          (100.2, "EUR", EUR(100.20)),
          (100.24, "EUR", EUR(100.24)),
          (100.243, "EUR", EUR(100.243)),
          (1000000.243, "EUR", EUR(1000000.243)),
          (0.24, "EUR", EUR(0.24)),
        )

      forAll(inputs) { (amount: Any, currency: String, expected: Money) =>
        s"should decode $amount $currency" in {
          val input = s"""{ "amount": $amount, "currency": "$currency" }"""
          import org.scalatest.EitherValues._
          decode[Money](input).value shouldBe (expected)
        }
      }
    }

    "should encode instance of money:" - {
      val inputs =
        Table(
          ("money", "amount", "currency"),
          (EUR(1), "1.0", "EUR"),
          (EUR(-1), "-1.0", "EUR"),
          (EUR(100.20), "100.2", "EUR"),
          (EUR(100.24), "100.24", "EUR"),
          (EUR(100.243), "100.243", "EUR"),
          (EUR(1000000.243), "1000000.243", "EUR"),
          (EUR(0.24), "0.24", "EUR"),
        )

      forAll(inputs) { (money: Money, amount: String, currency: String) =>
        s"should encode $amount $currency" in {
          val expected =
            s"""{
               |  "amount" : $amount,
               |  "currency" : "$currency"
               |}""".stripMargin
          money.asJson.toString should equal(expected)
        }
      }
    }

  }
}
