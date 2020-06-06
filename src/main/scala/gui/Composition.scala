package gui

class Composition(val sudokuGrid: SudokuGrid, val name: String, val composition: List[String], val param: (Int,Int)) {


  def execute: Unit = {

    println("executed "+name+" as composition "+composition+" with param "+param)

    //sequence iterator, iterates over operations
    val seqIt: Iterator[String] = composition.iterator

    //iterate over operations, call
    //TODO: Convert while to tail recursive method call
    while(seqIt.hasNext){
      seqIt.next match {
        case "transpose" => sudokuGrid.transposeTable
        case "exchange" => sudokuGrid.changeTable(0,0) //change always starts at (0,0)
        case "filterRowCol" => sudokuGrid.filterRowColOnField(param)
        case "filterSquare" => sudokuGrid.filterSquareOnFiled(param)
      }
    }

  }
}

