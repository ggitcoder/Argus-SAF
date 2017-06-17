/*
 * Copyright (c) 2017. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.alir.summaryBasedAnalysis.model

import org.argus.jawa.alir.Context
import org.argus.jawa.alir.pta.reachingFactsAnalysis.{RFAFact, SimHeap}
import org.argus.jawa.alir.pta.summaryBasedAnalysis.SummaryManager
import org.argus.jawa.core.Signature
import org.argus.jawa.core.util.{IList, ISet, isetEmpty}
import org.scalatest.{FlatSpec, Matchers}

import scala.language.implicitConversions

/**
  * Created by fgwei on 6/16/17.
  */
abstract class SuTestBase(fileName: String) extends FlatSpec with Matchers {
  implicit val heap: SimHeap = new SimHeap
  val sm: SummaryManager = new SummaryManager()
  sm.registerFileInternal("summaries/" + fileName)

  val context: Context = new Context("SuTest")
  val currentContext: Context = context.copy.setContext(new Signature("Lmy/Class;.main:()V"), "L888")
  val defContext: Context = context.copy.setContext(new Signature("Lmy/Class;.main:()V"), "L800")
  val defContext2: Context = context.copy.setContext(new Signature("Lmy/Class;.main:()V"), "L801")
  val defContext3: Context = context.copy.setContext(new Signature("Lmy/Class;.main:()V"), "L802")
  val defContext4: Context = context.copy.setContext(new Signature("Lmy/Class;.main:()V"), "L803")
  val defContext5: Context = context.copy.setContext(new Signature("Lmy/Class;.main:()V"), "L804")

  implicit def string2TestSignature(s: String): TestSignature =
    new TestSignature(new Signature(s))

  class TestSignature(signature: Signature) {
    var retOpt: Option[String] = _
    var recvOpt: Option[String] = _
    var args: IList[String] = _
    var input: ISet[RFAFact] = _

    def with_vars(retOpt: Option[String], recvOpt: Option[String], args: IList[String]): TestSignature = {
      this.retOpt = retOpt
      this.recvOpt = recvOpt
      this.args = args
      this
    }

    def with_input(input: RFAFact*): TestSignature = {
      this.input = input.toSet
      this
    }

    def produce(expected: RFAFact*): Unit = {
      signature.signature should "produce as expected" in {
        val summaries = sm.getSummaries(fileName)
        val output: ISet[RFAFact] =
          summaries.get(signature) match {
            case Some(summary) =>
              sm.process(summary, retOpt, recvOpt, args, input, currentContext)
            case None =>
              isetEmpty
          }
        assert(output == expected.toSet)
      }
    }
  }
}