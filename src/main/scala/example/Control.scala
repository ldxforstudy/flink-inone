package example

object Control extends App {
  val a = 1
  val b = 2
  val c = if (a>b) a else b
  println(c)

  val nums = Seq(1, 2, 3)
  for (n <- nums) print(s"$n ")
  println()

  nums.foreach(n => print(s"$n "))
}
