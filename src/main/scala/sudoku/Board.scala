package sudoku

import scala.io.Source

/*
* Klasa koja predstavlja sudoku tablu
* */



class Board {

  //celije u tabeli, organizovane kao niz nizova
  //ofDim kreira multidimenzionalni niz zadatih dimenzija sa praznim poljima
  val cells: Array[Array[Cell]] = Array.ofDim[Cell](Board.boardDimension,Board.boardDimension)

  /*
  * apply metoda da bi se celije mogle dohvatati sa Board(r,c)
  * */

  def apply(row: Int)(col: Int): Cell = {
    //provjera
    require( row >=0 && row < Board.boardDimension, "Invalid row number!" )
    require( col >=0 && col < Board.boardDimension, "Invalid column number!" )

    cells(row)(col)
  }

  /*
  * update metoda kojom se celije mijenjaju sa Borad(r,c) = val
  * */
  def update(row: Int)(col: Int)(cell: Cell): Unit = {
    //provjera
    require( row >=0 && row < Board.boardDimension, "Invalid row number!" )
    require( col >=0 && col < Board.boardDimension, "Invalid column number!" )

    cells(row)(col) = cell
  }


  override def toString: String = {
    val sb = new StringBuilder("")
    for(r <- cells){
      for(cell <- r){
        sb.append(" ").append(cell).append(" ")
      }
      sb.append("\n").append("\n")
    }
    //vratiti string iz StringBuildera
    sb.toString
  }
}


object Board {

  //dimenzija jednog kvadrata
  val squareDimension = 3

  //dimnezija table
  val boardDimension = squareDimension * squareDimension

  //velicina table
  val boardSize = boardDimension * boardDimension

  //karakter koji se uzima za prazno polje
  val emptyChar: Char = '-'

  //karakter koji se uzima za olovku
  val penChar: Char = 'P'


  //metoda koja parsira fajl u kom se nalazi sudoku tabela
  //@param file -> ime fajla iz kog se ucitava tabela
  def parseInputFile(file: String): Array[Array[Char]] = {
    //ucitavanje svih linija iz fajla
    val lines:Array[String] = Source.fromFile(file).getLines.toArray
    //svaku liniju koja je string pretvaramo u niz karatera
    lines.map(line => line.toArray)
  }


  //apply metoda
  //kreira novu tabelu na osnovu stringa koji predstavlja ime fajla
  //@param file -> ime fajla iz kog se ucitava tabela
  def apply(file: String): Board = {

    val arr = parseInputFile(file)
    val board = new Board

    var row = 0
    var col = 0

    for(r <- arr){
      for(chr <- r){
        //TODO: Dodati provjere da li je originalna celija, da li je mjesto gdje je olovka postavljena i ostalo
        //TODO: Provjeriti kako napisati update metodu za Board klasu
        board(row)(col) = new Cell(chr,(row,col),true)
        //povecati kolonu nakon svakog upisa
        col += 1
      }
      //nakon svakog upisaog reda resetovati kolonu na 0, a red povecati za 1
      row += 1
      col = 0
    }
    //vraca tabelu
    board
  }




}