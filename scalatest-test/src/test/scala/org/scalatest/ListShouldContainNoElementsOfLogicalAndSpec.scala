/*
* Copyright 2001-2013 Artima, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.scalatest

import org.scalactic.Equality
import org.scalactic.Uniformity
import org.scalactic.StringNormalizations._
import SharedHelpers._
import FailureMessages._
import scala.collection.JavaConverters._
import Matchers._
import exceptions.TestFailedException

class ListShouldContainNoElementsOfLogicalAndSpec extends FunSpec {

  val invertedStringEquality =
    new Equality[String] {
      def areEqual(a: String, b: Any): Boolean = a != b
    }

  val invertedListOfStringEquality =
    new Equality[List[String]] {
      def areEqual(a: List[String], b: Any): Boolean = a != b
    }

  val upperCaseStringEquality =
    new Equality[String] {
      def areEqual(a: String, b: Any): Boolean = a.toUpperCase == b
    }

  val upperCaseListOfStringEquality =
    new Equality[List[String]] {
      def areEqual(a: List[String], b: Any): Boolean = a.map(_.toUpperCase) == b
    }

  private def upperCase(value: Any): Any =
    value match {
      case l: List[_] => l.map(upperCase(_))
      case s: String => s.toUpperCase
      case c: Char => c.toString.toUpperCase.charAt(0)
      case (s1: String, s2: String) => (s1.toUpperCase, s2.toUpperCase)
      case e: java.util.Map.Entry[_, _] =>
        (e.getKey, e.getValue) match {
          case (k: String, v: String) => Entry(k.toUpperCase, v.toUpperCase)
          case _ => value
        }
      case _ => value
    }

  //ADDITIONAL//

  val fileName: String = "ListShouldContainNoElementsOfLogicalAndSpec.scala"

  describe("a List") {

    val fumList: List[String] = List("fum")
    val toList: List[String] = List("to")

    describe("when used with (contain noElementsOf Seq(..) and contain noElementsOf Seq(..))") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        fumList should (contain noElementsOf Seq("fee", "fie", "foe", "fam") and contain noElementsOf Seq("fie", "fee", "fam", "foe"))
        val e1 = intercept[TestFailedException] {
          fumList should (contain noElementsOf Seq("fee", "fie", "foe", "fum") and contain noElementsOf Seq("fie", "fee", "fam", "foe"))
        }
        checkMessageStackDepth(e1, FailureMessages.containedAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fum")), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          fumList should (contain noElementsOf Seq("fee", "fie", "foe", "fam") and contain noElementsOf Seq("fie", "fee", "fum", "foe"))
        }
        checkMessageStackDepth(e2, FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fam")) + ", but " + FailureMessages.containedAtLeastOneOf(fumList, Seq("fie", "fee", "fum", "foe")), fileName, thisLineNumber - 2)
      }

      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        fumList should (contain noElementsOf Seq("fee", "fie", "foe", "fum") and contain noElementsOf Seq("fee", "fie", "fum", "foe"))
        val e1 = intercept[TestFailedException] {
          fumList should (contain noElementsOf Seq("FEE", "FIE", "FOE", "FUM") and contain noElementsOf Seq("fee", "fie", "fum", "foe"))
        }
        checkMessageStackDepth(e1, FailureMessages.containedAtLeastOneOf(fumList, Seq("FEE", "FIE", "FOE", "FUM")), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          fumList should (contain noElementsOf Seq("fee", "fie", "foe", "fum") and (contain noElementsOf Seq("FEE", "FIE", "FUM", "FOE")))
        }
        checkMessageStackDepth(e2, FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fum")) + ", but " + FailureMessages.containedAtLeastOneOf(fumList, Seq("FEE", "FIE", "FUM", "FOE")), fileName, thisLineNumber - 2)
      }

      it("should use an explicitly provided Equality") {
        (fumList should (contain noElementsOf Seq("fee", "fie", "foe", "fum") and contain noElementsOf Seq("fee", "fie", "fum", "foe"))) (decided by upperCaseStringEquality, decided by upperCaseStringEquality)
        val e1 = intercept[TestFailedException] {
          (fumList should (contain noElementsOf Seq("FEE", "FIE", "FOE", "FUM") and contain noElementsOf Seq("fee", "fie", "fum", "foe"))) (decided by upperCaseStringEquality, decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e1, FailureMessages.containedAtLeastOneOf(fumList, Seq("FEE", "FIE", "FOE", "FUM")), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          (fumList should (contain noElementsOf Seq("fee", "fie", "foe", "fum") and contain noElementsOf Seq("FEE", "FIE", "FUM", "FOE"))) (decided by upperCaseStringEquality, decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e2, FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fum")) + ", but " + FailureMessages.containedAtLeastOneOf(fumList, Seq("FEE", "FIE", "FUM", "FOE")), fileName, thisLineNumber - 2)
        (fumList should (contain noElementsOf Seq(" FEE ", " FIE ", " FOE ", " FAM ") and contain noElementsOf Seq(" FEE ", " FIE ", " FOE ", " FAM "))) (after being lowerCased and trimmed, after being lowerCased and trimmed)
      }

      it("should allow RHS to contain duplicated value") {
        fumList should (contain noElementsOf Seq("fee", "fie", "foe", "fie", "fam") and contain noElementsOf Seq("fie", "fee", "fam", "foe"))
        fumList should (contain noElementsOf Seq("fie", "fee", "fam", "foe") and contain noElementsOf Seq("fee", "fie", "foe", "fie", "fam"))
      }
    }

    describe("when used with (equal (..) and contain noElementsOf Seq(..))") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        fumList should (equal (fumList) and contain noElementsOf Seq("fie", "fee", "fam", "foe"))
        val e1 = intercept[TestFailedException] {
          fumList should (equal (toList) and contain noElementsOf Seq("fee", "fie", "foe", "fam"))
        }
        checkMessageStackDepth(e1, FailureMessages.didNotEqual(fumList, toList), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          fumList should (equal (fumList) and contain noElementsOf Seq("fee", "fie", "foe", "fum"))
        }
        checkMessageStackDepth(e2, FailureMessages.equaled(fumList, fumList) + ", but " + FailureMessages.containedAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fum")), fileName, thisLineNumber - 2)
      }

      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        fumList should (equal (fumList) and contain noElementsOf Seq("fee", "fie", "foe", "fum"))
        val e1 = intercept[TestFailedException] {
          fumList should (equal (toList) and contain noElementsOf Seq("fee", "fie", "foe", "fum"))
        }
        checkMessageStackDepth(e1, FailureMessages.didNotEqual(fumList, toList), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          fumList should (equal (fumList) and (contain noElementsOf Seq("FEE", "FIE", "FOE", "FUM")))
        }
        checkMessageStackDepth(e2, FailureMessages.equaled(fumList, fumList) + ", but " + FailureMessages.containedAtLeastOneOf(fumList, Seq("FEE", "FIE", "FOE", "FUM")), fileName, thisLineNumber - 2)
      }

      it("should use an explicitly provided Equality") {
        (fumList should (equal (toList) and contain noElementsOf Seq("fee", "fie", "foe", "fum"))) (decided by invertedListOfStringEquality, decided by upperCaseStringEquality)
        val e1 = intercept[TestFailedException] {
          (fumList should (equal (fumList) and contain noElementsOf Seq("fee", "fie", "foe", "fum"))) (decided by invertedListOfStringEquality, decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e1, FailureMessages.didNotEqual(fumList, fumList), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          (fumList should (equal (toList) and contain noElementsOf Seq("FEE", "FIE", "FOE", "FUM"))) (decided by invertedListOfStringEquality, decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e2, FailureMessages.equaled(fumList, toList) + ", but " + FailureMessages.containedAtLeastOneOf(fumList, Seq("FEE", "FIE", "FOE", "FUM")), fileName, thisLineNumber - 2)
        (fumList should (equal (toList) and contain noElementsOf Seq(" FEE ", " FIE ", " FOE ", " FAM "))) (decided by invertedListOfStringEquality, after being lowerCased and trimmed)
      }

      it("should allow RHS to contain duplicated value") {
        fumList should (equal (fumList) and contain noElementsOf Seq("fee", "fie", "foe", "fie", "fam"))
      }
    }

    describe("when used with (be (..) and contain noElementsOf Seq(..))") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        fumList should (be (fumList) and contain noElementsOf Seq("fie", "fee", "fam", "foe"))
        val e1 = intercept[TestFailedException] {
          fumList should (be (toList) and contain noElementsOf Seq("fee", "fie", "foe", "fam"))
        }
        checkMessageStackDepth(e1, FailureMessages.wasNotEqualTo(fumList, toList), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          fumList should (be (fumList) and contain noElementsOf Seq("fee", "fie", "foe", "fum"))
        }
        checkMessageStackDepth(e2, FailureMessages.wasEqualTo(fumList, fumList) + ", but " + FailureMessages.containedAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fum")), fileName, thisLineNumber - 2)
      }

      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        fumList should (be (fumList) and contain noElementsOf Seq("fee", "fie", "foe", "fum"))
        val e1 = intercept[TestFailedException] {
          fumList should (be (toList) and contain noElementsOf Seq("fee", "fie", "foe", "fum"))
        }
        checkMessageStackDepth(e1, FailureMessages.wasNotEqualTo(fumList, toList), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          fumList should (be (fumList) and (contain noElementsOf Seq("FEE", "FIE", "FOE", "FUM")))
        }
        checkMessageStackDepth(e2, FailureMessages.wasEqualTo(fumList, fumList) + ", but " + FailureMessages.containedAtLeastOneOf(fumList, Seq("FEE", "FIE", "FOE", "FUM")), fileName, thisLineNumber - 2)
      }

      it("should use an explicitly provided Equality") {
        (fumList should (be (fumList) and contain noElementsOf Seq("fee", "fie", "foe", "fum"))) (decided by upperCaseStringEquality)
        val e1 = intercept[TestFailedException] {
          (fumList should (be (fumList) and contain noElementsOf Seq("FEE", "FIE", "FOE", "FUM"))) (decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e1, FailureMessages.wasEqualTo(fumList, fumList) + ", but " + FailureMessages.containedAtLeastOneOf(fumList, Seq("FEE", "FIE", "FOE", "FUM")), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          (fumList should (be (toList) and contain noElementsOf Seq("fee", "fie", "foe", "fum"))) (decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e2, FailureMessages.wasNotEqualTo(fumList, toList), fileName, thisLineNumber - 2)
        (fumList should (be (fumList) and contain noElementsOf Seq(" FEE ", " FIE ", " FOE ", " FAM "))) (after being lowerCased and trimmed)
      }

      it("should allow RHS to contain duplicated value") {
        fumList should (be (fumList) and contain noElementsOf Seq("fee", "fie", "foe", "fie", "fam"))
      }
    }

    describe("when used with (contain noElementsOf Seq(..) and be (..))") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        fumList should (contain noElementsOf Seq("fie", "fee", "fam", "foe") and be (fumList))
        val e1 = intercept[TestFailedException] {
          fumList should (contain noElementsOf Seq("fee", "fie", "foe", "fam") and be (toList))
        }
        checkMessageStackDepth(e1, FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fam")) + ", but " + FailureMessages.wasNotEqualTo(fumList, toList), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          fumList should (contain noElementsOf Seq("fee", "fie", "foe", "fum") and be (fumList))
        }
        checkMessageStackDepth(e2, FailureMessages.containedAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fum")), fileName, thisLineNumber - 2)
      }

      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        fumList should (contain noElementsOf Seq("fee", "fie", "foe", "fum") and be (fumList))
        val e1 = intercept[TestFailedException] {
          fumList should (contain noElementsOf Seq("FEE", "FIE", "FOE", "FUM") and be (toList))
        }
        checkMessageStackDepth(e1, FailureMessages.containedAtLeastOneOf(fumList, Seq("FEE", "FIE", "FOE", "FUM")), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          fumList should (contain noElementsOf Seq("FEE", "FIE", "FOE", "FUM") and (be (fumList)))
        }
        checkMessageStackDepth(e2, FailureMessages.containedAtLeastOneOf(fumList, Seq("FEE", "FIE", "FOE", "FUM")), fileName, thisLineNumber - 2)
      }

      it("should use an explicitly provided Equality") {
        (fumList should (contain noElementsOf Seq("fee", "fie", "foe", "fum") and be (fumList))) (decided by upperCaseStringEquality)
        val e1 = intercept[TestFailedException] {
          (fumList should (contain noElementsOf Seq("FEE", "FIE", "FOE", "FUM") and be (fumList))) (decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e1, FailureMessages.containedAtLeastOneOf(fumList, Seq("FEE", "FIE", "FOE", "FUM")), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          (fumList should (contain noElementsOf Seq("fee", "fie", "foe", "fum") and be (toList))) (decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e2, FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fum")) + ", but " + FailureMessages.wasNotEqualTo(fumList, toList), fileName, thisLineNumber - 2)
        (fumList should (contain noElementsOf Seq(" FEE ", " FIE ", " FOE ", " FAM ") and be (fumList))) (after being lowerCased and trimmed)
      }

      it("should allow RHS to contain duplicated value") {
        fumList should (contain noElementsOf Seq("fee", "fie", "foe", "fie", "fam") and be (fumList))
      }
    }

    describe("when used with (not contain noElementsOf Seq(..) and not contain noElementsOf Seq(..))") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        fumList should (not contain noElementsOf (Seq("fee", "fie", "foe", "fum")) and not contain noElementsOf (Seq("fee", "fie", "fum", "foe")))
        val e1 = intercept[TestFailedException] {
          fumList should (not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")) and not contain noElementsOf (Seq("fee", "fie", "fum", "foe")))
        }
        checkMessageStackDepth(e1, FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("FEE", "FIE", "FOE", "FUM")), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          fumList should (not contain noElementsOf (Seq("fee", "fie", "foe", "fum")) and not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")))
        }
        checkMessageStackDepth(e2, FailureMessages.containedAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fum")) + ", but " + FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("FEE", "FIE", "FOE", "FUM")), fileName, thisLineNumber - 2)
      }

      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        fumList should (not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")) and not contain noElementsOf (Seq("FEE", "FIE", "FUM", "FOE")))
        val e1 = intercept[TestFailedException] {
          fumList should (not contain noElementsOf (Seq("fee", "fie", "foe", "fum")) and not contain noElementsOf (Seq("FEE", "FIE", "FUM", "FOE")))
        }
        checkMessageStackDepth(e1, FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fum")), fileName, thisLineNumber - 2)

        val e2 = intercept[TestFailedException] {
          fumList should (not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")) and (not contain noElementsOf (Seq("fee", "fie", "fum", "foe"))))
        }
        checkMessageStackDepth(e2, FailureMessages.containedAtLeastOneOf(fumList, Seq("FEE", "FIE", "FOE", "FUM")) + ", but " + FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("fee", "fie", "fum", "foe")), fileName, thisLineNumber - 2)
      }

      it("should use an explicitly provided Equality") {
        (fumList should (not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")) and not contain noElementsOf (Seq("FEE", "FIE", "FUM", "FOE")))) (decided by upperCaseStringEquality, decided by upperCaseStringEquality)
        val e1 = intercept[TestFailedException] {
          (fumList should (not contain noElementsOf (Seq("fee", "fie", "foe", "fum")) and not contain noElementsOf (Seq("FEE", "FIE", "FUM", "FOE")))) (decided by upperCaseStringEquality, decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e1, FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fum")), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          (fumList should (not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")) and not contain noElementsOf (Seq("fee", "fie", "fum", "foe")))) (decided by upperCaseStringEquality, decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e2, FailureMessages.containedAtLeastOneOf(fumList, Seq("FEE", "FIE", "FOE", "FUM")) + ", but " + FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("fee", "fie", "fum", "foe")), fileName, thisLineNumber - 2)
        (fumList should (contain noElementsOf Seq(" FEE ", " FIE ", " FOE ", " FAM ") and contain noElementsOf Seq(" FEE ", " FIE ", " FOE ", " FAM "))) (after being lowerCased and trimmed, after being lowerCased and trimmed)
      }

      it("should allow RHS to contain duplicated value") {
        fumList should (not contain noElementsOf (Seq("fee", "fie", "foe", "fie", "fum")) and not contain noElementsOf (Seq("fee", "fie", "fum", "foe")))
        fumList should (not contain noElementsOf (Seq("fee", "fie", "fum", "foe")) and not contain noElementsOf (Seq("fee", "fie", "foe", "fie", "fum")))
      }
    }

    describe("when used with (not equal (..) and not contain noElementsOf Seq(..))") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        fumList should (not equal (toList) and not contain noElementsOf (Seq("fee", "fie", "foe", "fum")))
        val e1 = intercept[TestFailedException] {
          fumList should (not equal (fumList) and not contain noElementsOf (Seq("fee", "fie", "foe", "fum")))
        }
        checkMessageStackDepth(e1, FailureMessages.equaled(fumList, fumList), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          fumList should (not equal (toList) and not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")))
        }
        checkMessageStackDepth(e2, FailureMessages.didNotEqual(fumList, toList) + ", but " + FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("FEE", "FIE", "FOE", "FUM")), fileName, thisLineNumber - 2)
      }

      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        fumList should (not equal (toList) and not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")))
        val e1 = intercept[TestFailedException] {
          fumList should (not equal (fumList) and not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")))
        }
        checkMessageStackDepth(e1, FailureMessages.equaled(fumList, fumList), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          fumList should (not equal (toList) and (not contain noElementsOf (Seq("fee", "fie", "foe", "fum"))))
        }
        checkMessageStackDepth(e2, FailureMessages.didNotEqual(fumList, toList) + ", but " + FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fum")), fileName, thisLineNumber - 2)
      }

      it("should use an explicitly provided Equality") {
        (fumList should (not equal (fumList) and not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")))) (decided by invertedListOfStringEquality, decided by upperCaseStringEquality)
        val e1 = intercept[TestFailedException] {
          (fumList should (not equal (fumList) and not contain noElementsOf (Seq("fee", "fie", "foe", "fum")))) (decided by invertedListOfStringEquality, decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e1, FailureMessages.didNotEqual(fumList, fumList) + ", but " + FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fum")), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          (fumList should (not equal (toList) and not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")))) (decided by invertedListOfStringEquality, decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e2, FailureMessages.equaled(fumList, toList), fileName, thisLineNumber - 2)
        (fumList should (not contain noElementsOf (Seq(" FEE ", " FIE ", " FOE ", " FUM ")) and not contain noElementsOf (Seq(" FEE ", " FIE ", " FOE ", " FUM ")))) (after being lowerCased and trimmed, after being lowerCased and trimmed)
      }

      it("should allow RHS to contain duplicated value") {
        fumList should (not equal (toList) and not contain noElementsOf (Seq("fee", "fie", "foe", "fie", "fum")))
      }
    }

    describe("when used with (not be (..) and not contain noElementsOf Seq(..))") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        fumList should (not be (toList) and not contain noElementsOf (Seq("fee", "fie", "foe", "fum")))
        val e1 = intercept[TestFailedException] {
          fumList should (not be (fumList) and not contain noElementsOf (Seq("fee", "fie", "foe", "fum")))
        }
        checkMessageStackDepth(e1, FailureMessages.wasEqualTo(fumList, fumList), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          fumList should (not be (toList) and not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")))
        }
        checkMessageStackDepth(e2, FailureMessages.wasNotEqualTo(fumList, toList) + ", but " + FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("FEE", "FIE", "FOE", "FUM")), fileName, thisLineNumber - 2)
      }

      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality
        fumList should (not be (toList) and not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")))
        val e1 = intercept[TestFailedException] {
          fumList should (not be (fumList) and not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")))
        }
        checkMessageStackDepth(e1, FailureMessages.wasEqualTo(fumList, fumList), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          fumList should (not be (toList) and (not contain noElementsOf (Seq("fee", "fie", "foe", "fum"))))
        }
        checkMessageStackDepth(e2, FailureMessages.wasNotEqualTo(fumList, toList) + ", but " + FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fum")), fileName, thisLineNumber - 2)
      }

      it("should use an explicitly provided Equality") {
        (fumList should (not be (toList) and not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")))) (decided by upperCaseStringEquality)
        val e1 = intercept[TestFailedException] {
          (fumList should (not be (toList) and not contain noElementsOf (Seq("fee", "fie", "foe", "fum")))) (decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e1, FailureMessages.wasNotEqualTo(fumList, toList) + ", but " + FailureMessages.didNotContainAtLeastOneOf(fumList, Seq("fee", "fie", "foe", "fum")), fileName, thisLineNumber - 2)
        val e2 = intercept[TestFailedException] {
          (fumList should (not be (fumList) and not contain noElementsOf (Seq("FEE", "FIE", "FOE", "FUM")))) (decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e2, FailureMessages.wasEqualTo(fumList, fumList), fileName, thisLineNumber - 2)
        (fumList should (not contain noElementsOf (Seq(" FEE ", " FIE ", " FOE ", " FUM ")) and not contain noElementsOf (Seq(" FEE ", " FIE ", " FOE ", " FUM ")))) (after being lowerCased and trimmed, after being lowerCased and trimmed)
      }

      it("should allow RHS to contain duplicated value") {
        fumList should (not be (toList) and not contain noElementsOf (Seq("fee", "fie", "foe", "fie", "fum")))
      }
    }

  }

  describe("collection of Lists") {

    val list1s: Vector[List[Int]] = Vector(List(1), List(1), List(1))
    val lists: Vector[List[Int]] = Vector(List(1), List(1), List(2))
    val nils: Vector[List[Int]] = Vector(Nil, Nil, Nil)
    val listsNil: Vector[List[Int]] = Vector(List(1), List(1), Nil)
    val hiLists: Vector[List[String]] = Vector(List("hi"), List("hi"), List("hi"))
    val toLists: Vector[List[String]] = Vector(List("to"), List("to"), List("to"))

    def allErrMsg(index: Int, message: String, lineNumber: Int, left: Any): String =
      "'all' inspection failed, because: \n" +
        "  at index " + index + ", " + message + " (" + fileName + ":" + (lineNumber) + ") \n" +
        "in " + decorateToStringValue(left)

    describe("when used with (contain noElementsOf Seq(..) and contain noElementsOf Seq(..))") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        all (list1s) should (contain noElementsOf Seq(3, 2, 8) and contain noElementsOf Seq(2, 6, 8))
        atLeast (2, lists) should (contain noElementsOf Seq(3, 2, 5) and contain noElementsOf Seq(2, 3, 4))
        atMost (2, lists) should (contain noElementsOf Seq(3, 2, 8) and contain noElementsOf Seq(2, 3, 4))
        no (lists) should (contain noElementsOf Seq(1, 2, 8) and contain noElementsOf Seq(1, 2, 3))

        val e1 = intercept[TestFailedException] {
          all (lists) should (contain noElementsOf Seq(2, 6, 8) and contain noElementsOf Seq(3, 6, 9))
        }
        checkMessageStackDepth(e1, allErrMsg(2, FailureMessages.containedAtLeastOneOf(lists(2), Seq(2, 6, 8)), thisLineNumber - 2, lists), fileName, thisLineNumber - 2)

        val e2 = intercept[TestFailedException] {
          all (lists) should (contain noElementsOf Seq(3, 6, 9) and contain noElementsOf Seq(2, 6, 8))
        }
        checkMessageStackDepth(e2, allErrMsg(2, FailureMessages.didNotContainAtLeastOneOf(lists(2), Seq(3, 6, 9)) + ", but " + FailureMessages.containedAtLeastOneOf(lists(2), Seq(2, 6, 8)), thisLineNumber - 2, lists), fileName, thisLineNumber - 2)

        val e3 = intercept[TestFailedException] {
          all (hiLists) should (contain noElementsOf Seq("ho", "hello") and contain noElementsOf Seq("hi", "hey", "howdy"))
        }
        checkMessageStackDepth(e3, allErrMsg(0, FailureMessages.didNotContainAtLeastOneOf(hiLists(0), Seq("ho", "hello")) + ", but " + FailureMessages.containedAtLeastOneOf(hiLists(0), Seq("hi", "hey", "howdy")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)

        val e4 = intercept[TestFailedException] {
          all (lists) should (contain noElementsOf Seq(3, 6, 9) and contain noElementsOf Seq(2, 6, 8))
        }
        checkMessageStackDepth(e4, allErrMsg(2, FailureMessages.didNotContainAtLeastOneOf(lists(2), Seq(3, 6, 9)) + ", but " + FailureMessages.containedAtLeastOneOf(lists(2), Seq(2, 6, 8)), thisLineNumber - 2, lists), fileName, thisLineNumber - 2)
      }

      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality

        all (hiLists) should (contain noElementsOf Seq("hi", "he") and contain noElementsOf Seq("ho", "he"))

        val e1 = intercept[TestFailedException] {
          all (hiLists) should (contain noElementsOf Seq("HI", "HE") and contain noElementsOf Seq("ho", "he"))
        }
        checkMessageStackDepth(e1, allErrMsg(0, FailureMessages.containedAtLeastOneOf(hiLists(0), Seq("HI", "HE")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)

        val e2 = intercept[TestFailedException] {
          all (hiLists) should (contain noElementsOf Seq("hi", "he") and contain noElementsOf Seq("HI", "HE"))
        }
        checkMessageStackDepth(e2, allErrMsg(0, FailureMessages.didNotContainAtLeastOneOf(hiLists(0), Seq("hi", "he")) + ", but " + FailureMessages.containedAtLeastOneOf(hiLists(0), Seq("HI", "HE")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)
      }

      it("should use an explicitly provided Equality") {
        (all (hiLists) should (contain noElementsOf Seq("hi", "he") and contain noElementsOf Seq("ho", "he"))) (decided by upperCaseStringEquality, decided by upperCaseStringEquality)
        val e1 = intercept[TestFailedException] {
          (all (hiLists) should (contain noElementsOf Seq("HI", "HE") and contain noElementsOf Seq("ho", "he"))) (decided by upperCaseStringEquality, decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e1, allErrMsg(0, FailureMessages.containedAtLeastOneOf(hiLists(0), Seq("HI", "HE")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)

        val e2 = intercept[TestFailedException] {
          (all (hiLists) should (contain noElementsOf Seq("hi", "he") and contain noElementsOf Seq("HI", "HE"))) (decided by upperCaseStringEquality, decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e2, allErrMsg(0, FailureMessages.didNotContainAtLeastOneOf(hiLists(0), Seq("hi", "he")) + ", but " + FailureMessages.containedAtLeastOneOf(hiLists(0), Seq("HI", "HE")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)
      }

      it("should allow RHS to contain duplicated value") {
        all (list1s) should (contain noElementsOf Seq(8, 2, 2, 3) and contain noElementsOf Seq(2, 6, 8))
        all (list1s) should (contain noElementsOf Seq(2, 6, 8) and contain noElementsOf Seq(8, 2, 2, 3))
      }
    }

    describe("when used with (be (..) and contain noElementsOf Seq(..))") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        all (list1s) should (be (List(1)) and contain noElementsOf Seq(2, 3, 4))
        atLeast (2, lists) should (be (List(1)) and contain noElementsOf Seq(2, 3, 4))
        atMost (2, lists) should (be (List(1)) and contain noElementsOf Seq(2, 3, 4))
        no (lists) should (be (List(8)) and contain noElementsOf Seq(1, 2, 3))

        val e1 = intercept[TestFailedException] {
          all (lists) should (be (List(1)) and contain noElementsOf Seq(3, 6, 9))
        }
        checkMessageStackDepth(e1, allErrMsg(2, decorateToStringValue(List(2)) + " was not equal to " + decorateToStringValue(List(1)), thisLineNumber - 2, lists), fileName, thisLineNumber - 2)

        val e2 = intercept[TestFailedException] {
          all (list1s) should (be (List(1)) and contain noElementsOf Seq(1, 2, 3))
        }
        checkMessageStackDepth(e2, allErrMsg(0, decorateToStringValue(List(1)) + " was equal to " + decorateToStringValue(List(1)) + ", but " + FailureMessages.containedAtLeastOneOf(list1s(0), Seq(1, 2, 3)), thisLineNumber - 2, list1s), fileName, thisLineNumber - 2)

        val e3 = intercept[TestFailedException] {
          all (hiLists) should (be (List("hi")) and contain noElementsOf Seq("hi", "hey", "howdy"))
        }
        checkMessageStackDepth(e3, allErrMsg(0, decorateToStringValue(List("hi")) + " was equal to " + decorateToStringValue(List("hi")) + ", but " + FailureMessages.containedAtLeastOneOf(hiLists(0), Seq("hi", "hey", "howdy")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)

        val e4 = intercept[TestFailedException] {
          all (list1s) should (be (List(1)) and contain noElementsOf Seq(1, 2, 3))
        }
        checkMessageStackDepth(e4, allErrMsg(0, decorateToStringValue(List(1)) + " was equal to " + decorateToStringValue(List(1)) + ", but " + FailureMessages.containedAtLeastOneOf(list1s(0), Seq(1, 2, 3)), thisLineNumber - 2, list1s), fileName, thisLineNumber - 2)
      }

      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality

        all (hiLists) should (be (List("hi")) and contain noElementsOf Seq("hi", "he"))

        val e1 = intercept[TestFailedException] {
          all (hiLists) should (be (List("ho")) and contain noElementsOf Seq("hi", "he"))
        }
        checkMessageStackDepth(e1, allErrMsg(0, decorateToStringValue(List("hi")) + " was not equal to " + decorateToStringValue(List("ho")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)

        val e2 = intercept[TestFailedException] {
          all (hiLists) should (be (List("hi")) and contain noElementsOf Seq("HI", "HE"))
        }
        checkMessageStackDepth(e2, allErrMsg(0, decorateToStringValue(List("hi")) + " was equal to " + decorateToStringValue(List("hi")) + ", but " + FailureMessages.containedAtLeastOneOf(hiLists(0), Seq("HI", "HE")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)
      }

      it("should use an explicitly provided Equality") {
        (all (hiLists) should (be (List("hi")) and contain noElementsOf Seq("hi", "he"))) (decided by upperCaseStringEquality)
        val e1 = intercept[TestFailedException] {
          (all (hiLists) should (be (List("ho")) and contain noElementsOf Seq("hi", "he"))) (decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e1, allErrMsg(0, decorateToStringValue(List("hi")) + " was not equal to " + decorateToStringValue(List("ho")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)

        val e2 = intercept[TestFailedException] {
          (all (hiLists) should (be (List("hi")) and contain noElementsOf Seq("HI", "HE"))) (decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e2, allErrMsg(0, decorateToStringValue(List("hi")) + " was equal to " + decorateToStringValue(List("hi")) + ", but " + FailureMessages.containedAtLeastOneOf(hiLists(0), Seq("HI", "HE")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)
      }

      it("should allow RHS to contain duplicated value") {
        all (list1s) should (be (List(1)) and contain noElementsOf Seq(8, 2, 2, 3))
      }
    }

    describe("when used with (not contain noElementsOf Seq(..) and not contain noElementsOf Seq(..))") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        all (list1s) should (not contain noElementsOf (Seq(1, 2, 8)) and not contain noElementsOf (Seq(1, 6, 8)))
        atLeast (2, lists) should (not contain noElementsOf (Seq(1, 6, 8)) and not contain noElementsOf (Seq(1, 3, 8)))
        atMost (2, lists) should (not contain noElementsOf (Seq(1, 3, 8)) and contain noElementsOf (Seq(1, 6, 8)))
        no (lists) should (not contain noElementsOf (Seq(3, 6, 9)) and not contain noElementsOf (Seq(5, 7, 9)))

        val e1 = intercept[TestFailedException] {
          all (lists) should (not contain noElementsOf (Seq(1, 6, 8)) and not contain noElementsOf (Seq(1, 2, 3)))
        }
        checkMessageStackDepth(e1, allErrMsg(2, FailureMessages.didNotContainAtLeastOneOf(List(2), Seq(1, 6, 8)), thisLineNumber - 2, lists), fileName, thisLineNumber - 2)

        val e2 = intercept[TestFailedException] {
          all (lists) should (not contain noElementsOf (Seq(1, 2, 3)) and not contain noElementsOf (Seq(1, 6, 8)))
        }
        checkMessageStackDepth(e2, allErrMsg(2, FailureMessages.containedAtLeastOneOf(List(2), Seq(1, 2, 3)) + ", but " + FailureMessages.didNotContainAtLeastOneOf(List(2), Seq(1, 6, 8)), thisLineNumber - 2, lists), fileName, thisLineNumber - 2)

        val e3 = intercept[TestFailedException] {
          all (hiLists) should (not contain noElementsOf (Seq("ho", "hello")) and not contain noElementsOf (Seq("hi", "hey", "howdy")))
        }
        checkMessageStackDepth(e3, allErrMsg(0, FailureMessages.didNotContainAtLeastOneOf(List("hi"), Seq("ho", "hello")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)

        val e4 = intercept[TestFailedException] {
          all (hiLists) should (not contain noElementsOf (Seq("hi", "hey", "howdy")) and not contain noElementsOf (Seq("ho", "hello")))
        }
        checkMessageStackDepth(e4, allErrMsg(0, FailureMessages.containedAtLeastOneOf(List("hi"), Seq("hi", "hey", "howdy")) + ", but " + FailureMessages.didNotContainAtLeastOneOf(List("hi"), Seq("ho", "hello")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)
      }

      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality

        all (hiLists) should (not contain noElementsOf (Seq("HI", "HE")) and not contain noElementsOf (Seq("HI", "HE")))

        val e1 = intercept[TestFailedException] {
          all (hiLists) should (not contain noElementsOf (Seq("hi", "he")) and not contain noElementsOf (Seq("HI", "HE")))
        }
        checkMessageStackDepth(e1, allErrMsg(0, FailureMessages.didNotContainAtLeastOneOf(List("hi"), Seq("hi", "he")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)

        val e2 = intercept[TestFailedException] {
          all (hiLists) should (not contain noElementsOf (Seq("HI", "HE")) and not contain noElementsOf (Seq("hi", "he")))
        }
        checkMessageStackDepth(e2, allErrMsg(0, FailureMessages.containedAtLeastOneOf(List("hi"), Seq("HI", "HE")) + ", but " + FailureMessages.didNotContainAtLeastOneOf(hiLists(0), Seq("hi", "he")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)
      }

      it("should use an explicitly provided Equality") {
        (all (hiLists) should (not contain noElementsOf (Seq("HI", "HE")) and not contain noElementsOf (Seq("HI", "HE")))) (decided by upperCaseStringEquality, decided by upperCaseStringEquality)
        val e1 = intercept[TestFailedException] {
          (all (hiLists) should (not contain noElementsOf (Seq("hi", "he")) and not contain noElementsOf (Seq("HI", "HE")))) (decided by upperCaseStringEquality, decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e1, allErrMsg(0, FailureMessages.didNotContainAtLeastOneOf(List("hi"), Seq("hi", "he")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)

        val e2 = intercept[TestFailedException] {
          (all (hiLists) should (not contain noElementsOf (Seq("HI", "HE")) and not contain noElementsOf (Seq("hi", "he")))) (decided by upperCaseStringEquality, decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e2, allErrMsg(0, FailureMessages.containedAtLeastOneOf(List("hi"), Seq("HI", "HE")) + ", but " + FailureMessages.didNotContainAtLeastOneOf(hiLists(0), Seq("hi", "he")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)
      }

      it("should allow RHS to contain duplicated value") {
        all (list1s) should (not contain noElementsOf (Seq(1, 2, 2, 3)) and not contain noElementsOf (Seq(1, 6, 8)))
        all (list1s) should (not contain noElementsOf (Seq(1, 6, 8)) and not contain noElementsOf (Seq(1, 2, 2, 3)))
      }
    }

    describe("when used with (not be (..) and not contain oneOf (..))") {

      it("should do nothing if valid, else throw a TFE with an appropriate error message") {
        all (list1s) should (not be (List(2)) and not contain noElementsOf (Seq(1, 2, 3)))
        atLeast (2, lists) should (not be (List(3)) and not contain noElementsOf (Seq(1, 6, 8)))
        atMost (2, lists) should (not be (List(3)) and contain noElementsOf (Seq(1, 6, 8)))
        no (list1s) should (not be (List(1)) and not contain noElementsOf (Seq(3, 6, 9)))

        val e1 = intercept[TestFailedException] {
          all (lists) should (not be (List(2)) and not contain noElementsOf (Seq(1, 2, 3)))
        }
        checkMessageStackDepth(e1, allErrMsg(2, decorateToStringValue(List(2)) + " was equal to " + decorateToStringValue(List(2)), thisLineNumber - 2, lists), fileName, thisLineNumber - 2)

        val e2 = intercept[TestFailedException] {
          all (lists) should (not be (List(3)) and not contain noElementsOf (Seq(1, 6, 8)))
        }
        checkMessageStackDepth(e2, allErrMsg(2, decorateToStringValue(List(2)) + " was not equal to " + decorateToStringValue(List(3)) + ", but " + FailureMessages.didNotContainAtLeastOneOf(lists(2), Seq(1, 6, 8)), thisLineNumber - 2, lists), fileName, thisLineNumber - 2)

        val e3 = intercept[TestFailedException] {
          all (hiLists) should (not be (List("hi")) and not contain noElementsOf (Seq("hi", "hey", "howdy")))
        }
        checkMessageStackDepth(e3, allErrMsg(0, decorateToStringValue(List("hi")) + " was equal to " + decorateToStringValue(List("hi")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)

        val e4 = intercept[TestFailedException] {
          all (hiLists) should (not be (List("ho")) and not contain noElementsOf (Seq("ho", "hello")))
        }
        checkMessageStackDepth(e4, allErrMsg(0, decorateToStringValue(List("hi")) + " was not equal to " + decorateToStringValue(List("ho")) + ", but " + FailureMessages.didNotContainAtLeastOneOf(hiLists(0), Seq("ho", "hello")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)
      }

      it("should use the implicit Equality in scope") {
        implicit val ise = upperCaseStringEquality

        all (hiLists) should (not be (List("ho")) and not contain noElementsOf (Seq("HI", "HE")))

        val e1 = intercept[TestFailedException] {
          all (hiLists) should (not be (List("hi")) and not contain noElementsOf (Seq("HI", "HE")))
        }
        checkMessageStackDepth(e1, allErrMsg(0, decorateToStringValue(List("hi")) + " was equal to " + decorateToStringValue(List("hi")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)

        val e2 = intercept[TestFailedException] {
          all (hiLists) should (not be (List("ho")) and not contain noElementsOf (Seq("hi", "he")))
        }
        checkMessageStackDepth(e2, allErrMsg(0, decorateToStringValue(List("hi")) + " was not equal to " + decorateToStringValue(List("ho")) + ", but " + FailureMessages.didNotContainAtLeastOneOf(hiLists(0), Seq("hi", "he")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)
      }

      it("should use an explicitly provided Equality") {
        (all (hiLists) should (not be (List("ho")) and not contain noElementsOf (Seq("HI", "HE")))) (decided by upperCaseStringEquality)
        val e1 = intercept[TestFailedException] {
          (all (hiLists) should (not be (List("hi")) and not contain noElementsOf (Seq("HI", "HE")))) (decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e1, allErrMsg(0, decorateToStringValue(List("hi")) + " was equal to " + decorateToStringValue(List("hi")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)

        val e2 = intercept[TestFailedException] {
          (all (hiLists) should (not be (List("ho")) and not contain noElementsOf (Seq("hi", "he")))) (decided by upperCaseStringEquality)
        }
        checkMessageStackDepth(e2, allErrMsg(0, decorateToStringValue(List("hi")) + " was not equal to " + decorateToStringValue(List("ho")) + ", but " + FailureMessages.didNotContainAtLeastOneOf(hiLists(0), Seq("hi", "he")), thisLineNumber - 2, hiLists), fileName, thisLineNumber - 2)
      }

      it("should allow RHS to contain duplicated value") {
        all (list1s) should (not be (List(2)) and not contain noElementsOf (Seq(1, 2, 2, 3)))
      }
    }
  }
}