package gui

import java.awt.Color

import scala.collection.mutable.Map

import scala.io.Source
import swing._
import javax.swing._
import javax.swing.border.Border

import scala.swing.event.{Key, KeyPressed, KeyTyped, MouseClicked}
import scala.swing.event.Key.Value
/*
* class representing a sudoku grid
*
* */

class SudokuGrid extends GridPanel(1,1) {


  preferredSize = SudokuGrid.preferredBoardSize

  //grid of text fields
  val grid: Array[Array[TextField]] = Array.ofDim[TextField](SudokuGrid.boardDimension,SudokuGrid.boardDimension)
  //map that maps text fields to their corresponding cells
  val gridMap: Map[TextField, Cell] = Map[TextField, Cell]()

  //denotes the current pencil field
  var penField: TextField = grid(0)(0)

  //panel which contains 9 smaller panels (for each square)
  val gridPanel: GridPanel = new GridPanel(SudokuGrid.squareDimension,SudokuGrid.squareDimension)

  //array of panels which will contain the square panels
  val squarePanels: Array[Array[GridPanel]] = Array.ofDim[GridPanel](SudokuGrid.squareDimension,SudokuGrid.squareDimension)


  //text fields creation
  for(x <- 0 until SudokuGrid.boardDimension)
    for(y <- 0 until SudokuGrid.boardDimension){

      //create field and its corresponding cell
      val field: TextField = new TextField
      val cell: Cell = new Cell('-',(x,y),false)
      //map field to cell
      gridMap(field) = cell

      //border, font and size
      field.border = SudokuGrid.fieldBorder
      field.font = SudokuGrid.fieldFont
      field.preferredSize = SudokuGrid.fieldSize
      field.horizontalAlignment = Alignment.Center

      //TODO: add key listener
      field.listenTo(field.keys)
      field.reactions += {

        case e: KeyTyped if (!e.char.isDigit) => e.consume //only digits are allowed, so consume is called
        case e: KeyTyped if (e.char.isDigit) => writeDigit(e.char,field); e.consume//e.consume because of double digits
        case KeyPressed(_,key,_,_) =>
          if (key == Key.Up || key == Key.Right || key == Key.Down || key == Key.Left){
            movePen(key,field)
          }
      }


      //set default text
      field.text = ""
      //add to grid
      grid(x)(y) = field
    }

  //creating and adding 9 squares
  for(x <- 0 until SudokuGrid.squareDimension)
    for(y <- 0 until SudokuGrid.squareDimension){
      var panel: GridPanel = new GridPanel(SudokuGrid.squareDimension,SudokuGrid.squareDimension)
      //border
      panel.border = SudokuGrid.squareBorder
      //add to array
      squarePanels(x)(y) = panel
      //add to gridpanel
      gridPanel.contents += panel
    }

  //adding fields to corresponding squares
  for(x <- 0 until SudokuGrid.boardDimension)
    for(y <- 0 until SudokuGrid.boardDimension){
      val squareX: Int = x / SudokuGrid.squareDimension
      val squareY: Int = y / SudokuGrid.squareDimension

      //add field to its square
      squarePanels(squareX)(squareY).contents += grid(x)(y)
    }


  //adjust the border of gridPanel
  gridPanel.border = SudokuGrid.gridBorder

  //add gridPanel to this panel
  contents += gridPanel

  //set pen text field
  penField = grid(0)(0)
  penField.background = Color.YELLOW






  //METODE
  ///////////////////////////////////////////////////////


  //method which parses a file containing the sudoku board
  //@param file -> name of the file
  private def parseInputFile(file: String): Array[Array[Char]] = {
    //input all lines from the file
    val lines:Array[String] = Source.fromFile(file).getLines.toArray
    //every line which is a string transform to an array of characters
    lines.map(line => line.toArray)
  }

  //method for sudoku input from file
  def importFromFile(file: String): Unit = {
    //add the file's path to its name
    val fileFullPath = SudokuGrid.inputDir + file
    val arr = parseInputFile(fileFullPath)


    var row = 0
    var col = 0

    for(r <- arr){
      for(chr <- r){

        //field and its cell
        val textField: TextField = grid(row)(col)
        val cell: Cell = gridMap(textField)

        //set textfield text, and background/editable if needed
        textField.text = chr match {
          case SudokuGrid.emptyChar =>textField.background = Color.WHITE; ""
          case SudokuGrid.penChar => penField = textField; textField.background = Color.YELLOW;textField.requestFocus;""
          case _ => textField.background = Color.LIGHT_GRAY; textField.editable = false;chr.toString
        }

        //set cell value and original flag
        cell.value = chr match {
          case SudokuGrid.emptyChar | SudokuGrid.penChar => cell.original = false; '-'
          case _ => cell.original = true; chr
        }

        //inc col
        col += 1
      }
      //after each row, reset column to 0 and inc row
      row += 1
      col = 0
    }

  }

  def importMovesFromFile(file: String): Unit = {
    //add the file's path to its name
    val fileFullPath = SudokuGrid.inputDirMoves + file
    val moves = Source.fromFile(fileFullPath).getLines.toArray

    for(move <- moves){
      move match {
        case "l" => movePen(Key.Left, penField)
        case "r" => movePen(Key.Right, penField)
        case "u" => movePen(Key.Up, penField)
        case "d" => movePen(Key.Down, penField)
        case d if d(0).isDigit => writeDigit(d(0),penField)
      }
    }
  }


  //method that writes a digit into the specified text field
  private def writeDigit(c: Char, field: TextField): Unit = {

    val cell = gridMap(field)
    if(cell.original == true){
      Dialog.showMessage(null, "Nedozvoljeno mijenjanje originalnog polja!", title = "Nedozvoljen unos")
    }else{
      cell.value = c
      field.text = c.toString
    }
  }


  //method that resets the text field's background
  private def resetFieldBackground(field: TextField): Unit = {
    val cell = gridMap(field)
    cell.original match {
      case true => field.background = Color.LIGHT_GRAY
      case false => field.background = Color.WHITE
    }
  }

  //method that colors the field's background according to its original flag
  private def colorFieldPenBackground(field: TextField): Unit = {
    val cell = gridMap(field)
    cell.original match {
      case true => field.background = Color.ORANGE
      case false => field.background = Color.YELLOW
    }
  }

  //method for moving the pencil
  private def  movePen(key: Value, field: TextField) = {

      val cell = gridMap(field)
      key match {
        case Key.Up =>
          if(cell.row > 0) {
            resetFieldBackground(field)
            val newField = grid(cell.row - 1)(cell.col)
            colorFieldPenBackground(newField)
            newField.requestFocus
            penField = newField
          }
        case Key.Down =>
          if(cell.row < SudokuGrid.boardDimension - 1) {
            resetFieldBackground(field)
            val newField = grid(cell.row + 1)(cell.col)
            colorFieldPenBackground(newField)
            newField.requestFocus
            penField = newField
          }
        case Key.Left =>
          if(cell.col > 0) {
            resetFieldBackground(field)
            val newField = grid(cell.row)(cell.col - 1)
            colorFieldPenBackground(newField)
            newField.requestFocus
            penField = newField
          }
        case Key.Right =>
          if(cell.col < SudokuGrid.boardDimension - 1) {
            resetFieldBackground(field)
            val newField = grid(cell.row)(cell.col + 1)
            colorFieldPenBackground(newField)
            newField.requestFocus
            penField = newField
          }
      }
  }


  //method which returns a row
  //@param row -> index of the row
  private def row(row: Int): Array[Int] = {
    //take the row, apply map function so every field returns its digit (or 0 if it's empty)
    grid(row).map(field => if(!field.text.isEmpty) field.text.toInt else 0)
  }

  //method which returns a column
  //@param col -> index of the column
  private def col(col: Int): Array[Int] = {
    grid.map(arrFlds => if (!arrFlds(col).text.isEmpty) arrFlds(col).text.toInt else 0)
  }

  //method which returns a square within the sudoku table
  //@param square -> index of the square (squares are indexed from left to right, starting with 0, ending with 8)
  private def square(square: Int): Array[Int] = {
    //take groups of 3 rows from the matrix
    val rowBlocks = grid.grouped(SudokuGrid.squareDimension).toArray

    val horizontalGroup: Int = square / SudokuGrid.squareDimension
    val verticalGroup: Int = square % SudokuGrid.squareDimension

    val arrMatrices = rowBlocks(horizontalGroup).map(row => row.grouped(SudokuGrid.squareDimension).toArray)

    arrMatrices.map(matrix => matrix(verticalGroup)).flatten.map(textField => if(!textField.text.isEmpty) textField.text.toInt else 0)
  }


  //method which checks whether the row/col/square is valid
  //according to sudoku rules
  //@ param arr -> array which contains the elements of a row/col/square
  private def isValid(arr: Array[Int]): Boolean = {
    //for each number from 1 to 9 check whether it is in the array
    (1 to 9).forall(x => arr.contains(x))
  }

  //method which checks the solution of the sudoku
  private def isSudokuSolved: Boolean = {
    //forall -> takes a function p as parameter, returns true if p(x) is true for every x in the collection
    //return true if every row, col and square is valid
    (0 to 8).forall( x => isValid(row(x)) && isValid(col(x)) && isValid(square(x)))
  }


  //method which checks the current sudoku solution and notifies the user
  def checkSolution: Unit = {
    isSudokuSolved match {
      case true => Dialog.showMessage(null, "Rjesenje sudoku igre je ispravno, igra je gotova!", title = "Rjesenje ispravno"); clearAll
      case false => Dialog.showMessage(null, "Rjesenje sudoku igre je neispravno!", title = "Rjesenje neispravno")
    }
  }

  //method which resets the board
  private def clearAll: Unit = {
    for(arr <- grid)
      for(field <- arr){
        val cell: Cell = gridMap(field)

        field.text = ""
        field.background = Color.WHITE
        cell.original = false
        cell.value = '-'
      }
    penField = grid(0)(0)
    penField.background = Color.YELLOW
    penField.requestFocus
  }


  /*TODO: Iskoristiti ovu metodu za upis tabele u fajl
  *
  *   /*
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

  * */

}



object SudokuGrid {

  //input files for sudoku tables directory
  val inputDir = "src/main/scala/input/"

  //input files for moves directory
  val inputDirMoves = "src/main/scala/moves/"
  //output files directory
  val outputDir = "src/main/scala/output/"

  //dimension of a single square
  val squareDimension = 3

  //table dimension
  val boardDimension = squareDimension * squareDimension

  //table size (in number of fields)
  val boardSize = boardDimension * boardDimension

  //character taken as an empty char
  val emptyChar: Char = '-'

  //character which denotes the pen's starting position
  val penChar: Char = 'P'

  //grid panel border
  val gridBorder: Border = BorderFactory.createLineBorder(Color.BLACK,2)

  //single field border
  val fieldBorder: Border = BorderFactory.createLineBorder(Color.BLACK,1)
  //single field font
  val fieldFont: Font = Font(Font.SansSerif,Font.Plain,20)
  //dimension of am single field
  val fieldSize: Dimension = new Dimension(30,30)

  //single square border
  val squareBorder: Border = BorderFactory.createLineBorder(Color.BLACK,1)

  //dimension of table panel
  val preferredBoardSize = new Dimension(600,600)


}