package gui

/*
* class representing a single sudoku table cell
*
* @param value -> cell value
* @param loc -> tuple denoting the coordinates of the cell
* @param original -> flag which denotes if the cell is mutable or not
*
* */

class Cell(var value: Char, val loc: (Int,Int), var original: Boolean ){

  require( loc._1 >=0 && loc._1 < SudokuGrid.boardDimension, "Invalid row number!" )
  require( loc._2 >=0 && loc._2 < SudokuGrid.boardDimension, "Invalid column number!" )

  //cell row
  val row = loc._1
  //cell column
  val col = loc._2
  //denotes if the cell is valid
  def isValid: Boolean = value != 'P' && value != '-'

}

