package example

// scala类型.
object Types extends App {
  val str1 = "Hello"
  println(str1)

  val str2: String = "world"
  println(str2)

  val str3 = s"$str1 $str2"
  println(str3)

  val str4 =
    """今晚
       |吃
       |什么？
    """.stripMargin
  println(str4)
}
