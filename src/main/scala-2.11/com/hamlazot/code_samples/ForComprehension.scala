package com.hamlazot.code_samples

/**
 * @author yoav @since 10/19/16.
 */


object ForComprehension extends App{
  case class M1[T](value: T){
    def map[S](f: T => S): M1[S] = {
      println(s"Calling map value ${value.getClass.getSimpleName}")

      M1(f(value))

    }

    def flatMap[S](f: T => M1[S]): M1[S] = {
      println(s"Calling flatMap value ${value.getClass.getSimpleName}")
      f(value)
    }
  }

  def inti: Int => M1[String] = i => {
    if(i == 300)println("YeaHeah!!!")
    println(s"Calling inti")
    M1(i.toString)}
  def stringi: String => M1[Double] = i => {
    println(s"Calling stringi")
    M1(i.toDouble)
  }
  def doubli: Double => M1[Int] = i => {
    if(i == 300.0)println("YeaHeah!!!")
    println(s"Calling doubli")
    M1(i.toInt)
  }

  val int2String2Double2Int = for{
    str <- inti(3)
    doub <- stringi(str)
    i <- doubli(doub)
  } yield i

  //println(int2String2Double2Int)

  Thread.sleep(1000)

}

