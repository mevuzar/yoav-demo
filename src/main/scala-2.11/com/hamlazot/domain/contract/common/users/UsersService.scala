package com.hamlazot
package domain.contract.common.users

import java.time.ZonedDateTime

import com.hamlazot.domain.contract.client.users.UsersProtocol
import com.hamlazot.{AuthenticatedOperations, CommonTerms}

/**
 * Created by Owner on 9/30/2016.
 */
private[domain] trait UsersService[A <: UsersAggregate]  extends CommonOperations{

  val aggregate: A
  val protocol: UsersProtocol[A]

  def createUser: Operation[protocol.CreateUserRequest, protocol.CreateUserResponse]

  def getUser: Operation[protocol.GetUserRequest, protocol.GetUserResponse]

  def deleteUser: Operation[protocol.DeleteUserRequest, protocol.DeleteUserResponse]

  def addTrustees: Operation[protocol.AddTrusteesRequest, Boolean]

  def addTrusters: Operation[protocol.AddTrustersRequest, Boolean]

  def removeTrustees: Operation[protocol.RemoveTrusteesRequest, Boolean]

  def removeTrusters: Operation[protocol.RemoveTrusteesRequest, Boolean]

}



object UsersServiceDescriptionApp extends App{
  val ru = scala.reflect.runtime.universe
  val m = ru.runtimeMirror(getClass.getClassLoader)
  val im = ru.typeOf[UsersService[UsersAggregate]]
  //im.baseClasses(1).asType.info.decls.toList(0)
  //im.baseClasses(1).asType.info.decls.toList(0).asType.typeParams
  im.members.filter(_.info.dealias.resultType.toString.contains("Operation")).foreach(m => println(s"$m ${m.typeSignature}"))
//  {m =>
//    val sig = m.info.dealias.resultType.toString
//    println()
//  }
  println(im.baseClasses(1).asType.info.decls.toList(0))
  println(im.baseClasses(1).asType.info.decls.toList(0).asType.typeParams)
}