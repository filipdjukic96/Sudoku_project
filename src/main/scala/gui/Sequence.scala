package gui

class Sequence(val sudokuGrid: SudokuGrid, val name: String, val sequence: List[String], val params: List[(Int,Int)]) {


  def execute: Unit = {
    //TODO: Implement the execute method
    println("executed "+name+" as seq "+sequence+" with params "+params)

    //sequence iterator, iterates over operations
    val seqIt: Iterator[String] = sequence.iterator
    //parameters iterator, iterates over parameters
    val paramIt: Iterator[(Int,Int)] = params.iterator

    //iterate over operations, call
    while(seqIt.hasNext){
      seqIt.next match {
        case "transpose" => sudokuGrid.transposeTable
        case "exchange" => sudokuGrid.changeTable(0,0) //change always starts at (0,0)
        case "filterRowCol" => sudokuGrid.filterRowColOnField(paramIt.next)
        case "filterSquare" => sudokuGrid.filterSquareOnFiled(paramIt.next)
      }
    }

  }
}
