package sudoku

/*
*  Struktura koja predstavlja jednu celiju sudoku tabele
* @param value -> vrijednost celije (broj,'-','P')
* @param loc -> tuple koji predstavlja lokaciju celije na tabeli (row,col)
* @param original -> flag koji pokazuje da li je celija dio originalne tabele ili ne
*
* */

class Cell(var value: Char, val loc: (Int,Int), val original: Boolean ){

  //red u kom se celija nalazi
  val row = loc._1
  //kolona u kojoj se celija nalazi
  val col = loc._2




}
