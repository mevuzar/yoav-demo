package com.hamlazot.code_samples

import scala.util.Try

/**
 * @author yoav @since 10/26/16.
 */
object EffExample extends App {

  import org.atnos.eff._
  import EvalEffect.Eval

  import scalaz._

  type ReaderInt[A] = Reader[Int, A]
  type WriterString[A] = Writer[String, A]

  type Stack = Fx.fx3[WriterString, ReaderInt, Eval]


  import org.atnos.eff.all._
  import org.atnos.eff.syntax.all._

  // useful type aliases showing that the ReaderInt and the WriterString effects are "members" of R
  // note that R could have more effects
  type _readerInt[R] = ReaderInt |= R
  type _writerString[R] = WriterString |= R
  type _tryString[R] = Try |= R

  def program[R: _readerInt : _writerString : _eval ]: Eff[R, Int] = for {
  // get the configuration
    n <- ask[R, Int]

    // log the current configuration value
    _ <- tell("the required power is " + n)

    // compute the nth power of 2
    a <- delay(math.pow(2, n.toDouble).toInt)

    // log the result
    _ <- tell("the result is " + a)
  } yield a

  // run the action with all the interpreters
  // each interpreter running one effect
  val lala = program[Stack].runReader(6).runWriter.runEval.run
  println(lala)
}
