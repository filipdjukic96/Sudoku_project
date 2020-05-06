package gui

/*
* Klasa koja predstavlja jednu celiju sudoku tabele
*
* @param value -> vrijednost celije (broj,'-','P')
* @param loc -> tuple koji predstavlja lokaciju celije na tabeli (row,col)
* @param original -> flag koji pokazuje da li je celija dio originalne tabele ili ne
*
* */

class Cell(var value: Char, val loc: (Int,Int), val original: Boolean ){

  require( loc._1 >=0 && loc._1 < SudokuGrid.boardDimension, "Invalid row number!" )
  require( loc._2 >=0 && loc._2 < SudokuGrid.boardDimension, "Invalid column number!" )

  //red u kom se celija nalazi
  val row = loc._1
  //kolona u kojoj se celija nalazi
  val col = loc._2

  def isValid: Boolean = value != 'P' && value != '-'

  //dodati squareNumber parametar -> oznacava u kom je kvadratu celija






  override def toString: String = "[" + value.toString + "]"
}

