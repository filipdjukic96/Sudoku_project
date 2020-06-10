package gui

class Composition(val sudokuGrid: SudokuGrid, val name: String, val composition: List[String], val param: (Int,Int)) {


  def execute: Unit = {

    println("executed "+name+" as composition "+composition+" with param "+param)

    //sequence iterator, iterates over operations
    val seqIt: Iterator[String] = composition.iterator

    executeInternal(seqIt,param)
  }

  private def executeInternal(seqIt: Iterator[String],param:(Int, Int)): Unit = {
    if(seqIt.hasNext){
      seqIt.next match {
        case "transpose" => sudokuGrid.transposeTable
        case "exchange" => sudokuGrid.changeTable(0,0) //change always starts at (0,0)
        case "filterRowCol" => sudokuGrid.filterRowColOnField(param)
        case "filterSquare" => sudokuGrid.filterSquareOnFiled(param)
      }
      executeInternal(seqIt,param)
    }
  }
}

