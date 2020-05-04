package sudoku

import scala.io.Source
import java.io._
import io.AnsiColor._

/*
* Klasa koja predstavlja sudoku tablu
* */



class Board {

  //celije u tabeli, organizovane kao niz nizova
  //ofDim kreira multidimenzionalni niz zadatih dimenzija sa praznim poljima
  val cells: Array[Array[Cell]] = Array.ofDim[Cell](Board.boardDimension,Board.boardDimension)

  //promjenljiva koja oznacava polozaj olovke (row,col)
  var pencilCell: Cell = null

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


  /*
  * metoda za pomjeranje olovke shodno korisnikovom unosu
  * @param moveOption -> izabrani potez korisnika iz skupa ('U','D','L','R')
  */
  def movePencil(moveOption: Char): Unit = {

    require(pencilCell != null, "Tabela nije podesena!")

    val pencilRow = pencilCell.row
    val pencilCol = pencilCell.col

    var pencilChanged: Boolean = true

    moveOption match {
      case 'U' if pencilRow > 0 => pencilCell = cells(pencilRow-1)(pencilCol)
      case 'D' if pencilRow < Board.boardDimension => pencilCell = cells(pencilRow + 1)(pencilCol)
      case 'L' if pencilCol > 0 => pencilCell = cells(pencilRow)(pencilCol-1)
      case 'R' if pencilCol < Board.boardDimension => pencilCell = cells(pencilRow)(pencilCol+1)
      case _ => pencilChanged = false; println("Nevalidan potez pomjereanja olovke! \n")
    }



  }



  /*
  * metoda koja ispisuje tabelu u fajl
  * @param file -> fajl u koji se vrsi upis
  */
  def outputToFile(file: String): Unit = {
    //dodaje se putanja na ime fajl da bi se kreirao u output folderu
    val fileFullPath = Board.outputDir + file
    // PrintWriter iz Java
    val pw = new PrintWriter(new File(fileFullPath ))

    for (r <- cells){
      for(cell <- r){
        pw.write(cell.value)
      }
      pw.write("\n")
    }
    pw.close
  }


  //nadjacana toString metoda
  override def toString: String = {
    val sb = new StringBuilder("")
    for(r <- cells){
      for(cell <- r){
        //ako je pencil pozicija, ispisati reversed
        if(cell == pencilCell){
          sb.append(" ").append(s"${REVERSED}${BOLD}").append(cell).append(s"${RESET}").append(" ")
        }else{
          sb.append(" ").append(cell).append(" ")
        }
      }
      sb.append("\n").append("\n")
    }
    sb.append("Polozaj olovke: ("+pencilCell.row + ","+pencilCell.col+")")
    sb.append("\n").append("\n")
    //ako olovka pokazuje validan broj, ispisati ga
    if(pencilCell.isValid) sb.append("Vrijednost pokazana olovkom: ["+pencilCell.value+"]")

    //vratiti string iz StringBuildera
    sb.toString
  }
}


object Board {

  //direktorijum ulaznih fajlova
  val inputDir = "src/main/scala/input/"

  //direktorijum izlaznih fajlova
  val outputDir = "src/main/scala/output/"

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
    //dodaje se putanja na ime fajla da bi se dohvatio u input folderu
    val fileFullPath = inputDir + file
    val arr = parseInputFile(fileFullPath)
    val board = new Board

    var row = 0
    var col = 0

    for(r <- arr){
      for(chr <- r){
        //TODO: Provjeriti kako napisati update metodu za Board klasu
        board.cells(row)(col) = chr match {
          case '-' => new Cell(chr,(row,col),false)
          case 'P' => board.pencilCell = new Cell('-',(row,col),false); board.pencilCell
          case _ => new Cell(chr,(row,col),true)
        }

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