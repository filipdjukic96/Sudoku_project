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






  //METODE
  ///////////////////////////////////////////////////////


  //method which parses a file containing the sudoku board
  //@param file -> name of the file
  def parseInputFile(file: String): Array[Array[Char]] = {
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
          case SudokuGrid.emptyChar => ""
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
  def writeDigit(c: Char, field: TextField): Unit = {
    println("write digit" + c.toString)
    val cell = gridMap(field)
    if(cell.original == true){
      //TODO: if its and original, call a pop-up window
      Dialog.showMessage(null, "Nedozvoljeno mijenjanje originalnog polja!", title = "Nedozvoljen unos")
    }else{
      cell.value = c
      field.text = c.toString
    }
    println("polje: "+field.text)
  }


  //method that resets the text field's background
  def resetFieldBackground(field: TextField): Unit = {
    val cell = gridMap(field)
    cell.original match {
      case true => field.background = Color.LIGHT_GRAY
      case false => field.background = Color.WHITE
    }
  }

  //method that colors the field's background according to its original flag
  def colorFieldPenBackground(field: TextField): Unit = {
    val cell = gridMap(field)
    cell.original match {
      case true => field.background = Color.ORANGE
      case false => field.background = Color.YELLOW
    }
  }

  //method for moving the pencil
  def  movePen(key: Value, field: TextField) = {

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