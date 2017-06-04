/*
 * Copyright (c) 2017. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.summary.parser

import org.antlr.v4.runtime.tree.ParseTree
import org.argus.jawa.core.Signature
import org.argus.jawa.core.util.Antlr4
import org.argus.jawa.summary.rule._
import org.argus.jawa.summary.grammar.SafsuBaseVisitor
import org.argus.jawa.summary.grammar.SafsuParser._

import scala.collection.JavaConverters._

/**
  * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
  */
object SummaryParserVisitor {
  def apply[T <: SuRuleNode](t: ParseTree): T =
    new SummaryParserVisitor().
      visit(t).asInstanceOf[T]
}

class SummaryParserVisitor()
  extends SafsuBaseVisitor[SuRuleNode]
  with Antlr4.Visitor {

  override def visitSummaryFile(ctx: SummaryFileContext): SuRuleNode =
    SummaryFile(ctx.summary.asScala.map{ s =>
      val summary = getChild[Summary](s)
      (summary.signature, summary)
    }.toMap)

  override def visitSummary(ctx: SummaryContext): SuRuleNode =
    Summary(new Signature(getUID(ctx.signature.UID.getText)), getChildren(ctx.suRule.asScala))

  override def visitClearRule(ctx: ClearRuleContext): SuRuleNode =
    ClearRule({
      if(ctx.suThis != null) getChild[SuThis](ctx.suThis)
      else if(ctx.arg != null) getChild[SuArg](ctx.arg)
      else getChild[SuGlobal](ctx.global)
    })

  override def visitBinaryRule(ctx: BinaryRuleContext): SuRuleNode =
    BinaryRule(getChild[RuleLhs](ctx.lhs), ctx.ops.getText match {
      case "+=" => Ops.`+=`
      case "-=" => Ops.`-=`
      case _ => Ops.`=`
    }, getChild[RuleRhs](ctx.rhs))

  override def visitSuThis(ctx: SuThisContext): SuRuleNode =
    SuThis(getOptChild[SuHeap](ctx.heap))

  override def visitArg(ctx: ArgContext): SuRuleNode =
    SuArg(ctx.Digits.getText.toInt, getOptChild[SuHeap](ctx.heap))

  override def visitGlobal(ctx: GlobalContext): SuRuleNode =
    SuGlobal(getUID(ctx.UID.getText), getOptChild[SuHeap](ctx.heap))

  override def visitHeap(ctx: HeapContext): SuRuleNode =
    SuHeap(getChildren(ctx.heapAccess.asScala))

  override def visitFieldAccess(ctx: FieldAccessContext): SuRuleNode =
    SuFieldAccess(ctx.ID.getText)

  override def visitArrayAccess(ctx: ArrayAccessContext): SuRuleNode =
    SuArrayAccess()

  override def visitRet(ctx: RetContext): SuRuleNode =
    SuRet(getOptChild[SuHeap](ctx.heap))

  override def visitType(ctx: TypeContext): SuRuleNode =
    SuType(ctx.ID.asScala.map(_.getText).mkString("."), getChild[SuLocation](ctx.location))

  override def visitVirtualLocation(ctx: VirtualLocationContext): SuRuleNode =
    SuVirtualLocation()

  override def visitConcreteLocation(ctx: ConcreteLocationContext): SuRuleNode =
    SuConcreteLocation(ctx.ID.getText)

  private def getUID(text: String): String = text.substring(1, text.length - 1)
}