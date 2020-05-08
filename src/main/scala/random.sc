/*worksheet for different calculations*/


val sudoku =
  Array(
    Array(1, 1, 1, 2, 2, 2, 3, 3, 3),
    Array(1, 1, 1, 2, 2, 2, 3, 3, 3),
    Array(1, 1, 1, 2, 2, 2, 3, 3, 3),
    Array(4, 4, 4, 5, 5, 5, 6, 6, 6),
    Array(4, 4, 4, 5, 5, 5, 6, 6, 6),
    Array(4, 4, 4, 5, 5, 5, 6, 6, 6),
    Array(7, 7, 7, 8, 8, 8, 9, 9, 9),
    Array(7, 7, 7, 8, 8, 8, 9, 9, 9),
    Array(7, 7, 7, 8, 8, 8, 9, 9, 9)
  )

val rowBlocks = sudoku.grouped(3).toArray
def splitRow(row: Array[Int]) = row.grouped(3).toArray

//val squares = rowBlocks.map( block => block.map(splitRow).transpose)
//val squares = rowBlocks(0).map(row => splitRow(row)(1)).flatten
val squares = rowBlocks(0).map(row => row.grouped(3).toArray)
squares.map(matrix => matrix(1)).flatten
