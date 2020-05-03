import sudoku.Board
//glavna funkcija


object Sudoku extends App{
  //putanja pocinje gdje i projekat
  val board = Board("src/main/scala/input/board1.txt")
  println(board)
}
