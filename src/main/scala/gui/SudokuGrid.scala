package gui

import java.awt.Color

import scala.collection.mutable.Map
import sudoku.Board.{inputDir, parseInputFile}
import sudoku.{Board, Cell}

import scala.io.Source
import swing._
import javax.swing._
import javax.swing.border.Border

import scala.swing.event.{KeyTyped, MouseClicked}

/*
* klasa koja predstavlja sudoku tabelu
*
* */

class SudokuGrid extends GridPanel(1,1) {

  //title = "Sudoku"
  preferredSize = SudokuGrid.preferredBoardSize

  //mreza text polja
  val grid: Array[Array[TextField]] = Array.ofDim[TextField](SudokuGrid.boardDimension,SudokuGrid.boardDimension)
  //mapa koja mapira text polja u Celije
  val gridMap: Map[TextField, Cell] = Map[TextField, Cell]()

  //panel koji ce sadrzati 9 manjih panela (za svaki kvadrat po jedan)
  val gridPanel: GridPanel = new GridPanel(SudokuGrid.squareDimension,SudokuGrid.squareDimension)

  //niz panela koji ce sadrzati panele manjih kvadratica
  val squarePanels: Array[Array[GridPanel]] = Array.ofDim[GridPanel](SudokuGrid.squareDimension,SudokuGrid.squareDimension)


  //stvaranje pojedinacnih polja
  for(x <- 0 until SudokuGrid.boardDimension)
    for(y <- 0 until SudokuGrid.boardDimension){


      var field: TextField = new TextField
      //border, font i velicina
      field.border = SudokuGrid.fieldBorder
      field.font = SudokuGrid.fieldFont
      field.preferredSize = SudokuGrid.fieldSize
      field.horizontalAlignment = Alignment.Center

      //TODO: dodati key listener
      field.listenTo(this.mouse.clicks)
      field.listenTo(this.keys)
      field.reactions += {
        case MouseClicked(_, p, _, _, _) => println("pritisnut mis")
        case KeyTyped(_, c, _, _) =>
          if ('1' <= c && c <= '9') {
            field.text = c.toString
            println(c)
          }
      }
      //DA LI TREBA???
      field.focusable = true
      field.requestFocus

      //postaviti defaul text
      field.text = ""
      //dodati u grid
      grid(x)(y) = field
    }

  //stvaranje i dodavanje kvadrata (njh 9)
  for(x <- 0 until SudokuGrid.squareDimension)
    for(y <- 0 until SudokuGrid.squareDimension){
      var panel: GridPanel = new GridPanel(SudokuGrid.squareDimension,SudokuGrid.squareDimension)
      //border
      panel.border = SudokuGrid.squareBorder
      //dodati u niz
      squarePanels(x)(y) = panel
      //dodati u gridpanel
      gridPanel.contents += panel
    }

  //dodavanje pojedinacnih polja u odgovarajuce kvadrate
  for(x <- 0 until SudokuGrid.boardDimension)
    for(y <- 0 until SudokuGrid.boardDimension){
      val squareX: Int = x / SudokuGrid.squareDimension
      val squareY: Int = y / SudokuGrid.squareDimension

      //dodati polje u odg kvadrat
      squarePanels(squareX)(squareY).contents += grid(x)(y)
    }


  //podesiti border gridPanela
  gridPanel.border = SudokuGrid.gridBorder

  //dodati gridPanel u ovaj panel

  contents += gridPanel







  //METODE
  ///////////////////////////////////////////////////////


  //metoda koja parsira fajl u kom se nalazi sudoku tabela
  //@param file -> ime fajla iz kog se ucitava tabela
  def parseInputFile(file: String): Array[Array[Char]] = {
    //ucitavanje svih linija iz fajla
    val lines:Array[String] = Source.fromFile(file).getLines.toArray
    //svaku liniju koja je string pretvaramo u niz karatera
    lines.map(line => line.toArray)
  }

  //metoda za ucitavanje sudoku-a iz fajla
  def importFromFile(file: String): Unit = {
    //dodaje se putanja na ime fajla da bi se dohvatio u input folderu
    val fileFullPath = inputDir + file
    val arr = parseInputFile(fileFullPath)


    var row = 0
    var col = 0

    for(r <- arr){
      for(chr <- r){

        val textField: TextField = grid(row)(col)
        textField.text = chr match {
          case SudokuGrid.emptyChar => ""
          case SudokuGrid.penChar => ""
          case _ => textField.background = Color.LIGHT_GRAY; chr.toString
        }

        //mapirati text field u Celiju
        gridMap(textField) = chr match {
          case SudokuGrid.emptyChar => new Cell('-',(row,col),false)
          case SudokuGrid.penChar => new Cell('-',(row,col),false);
          case _ => new Cell(chr,(row,col),true)
        }

        //povecati kolonu nakon svakog upisa
        col += 1
      }
      //nakon svakog upisaog reda resetovati kolonu na 0, a red povecati za 1
      row += 1
      col = 0
    }
  }


  focusable = true

}



object SudokuGrid {

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

  //border za gridPanel
  val gridBorder: Border = BorderFactory.createLineBorder(Color.BLACK,2)

  //border za svako polje
  val fieldBorder: Border = BorderFactory.createLineBorder(Color.BLACK,1)
  //font za svako polje
  val fieldFont: Font = Font(Font.SansSerif,Font.Plain,20)
  //velicina svakog polja
  val fieldSize: Dimension = new Dimension(30,30)

  //border za svaki kvadrat
  val squareBorder: Border = BorderFactory.createLineBorder(Color.BLACK,1)

  //velicina panela za tabelu
  val preferredBoardSize = new Dimension(600,600)




  /*
  //apply metoda
  //kreira novu tabelu na osnovu stringa koji predstavlja ime fajla
  //@param file -> ime fajla iz kog se ucitava tabela
  def apply(file: String): SudokuGrid = {
    /
    //vraca tabelu
    board
  }*/


}