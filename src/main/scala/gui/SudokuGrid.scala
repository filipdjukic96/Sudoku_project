package gui

import java.awt.Color
import java.io.{File, PrintWriter}

import scala.collection.mutable.Map
import scala.io.Source
import swing._
import javax.swing._
import javax.swing.border.Border

import scala.swing.Dialog.Message
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


  //flag which determines whether the table is in solve or edit mode
  //solve mode means a table is read from a file, it can only be solved (original fields immutable)
  //edit mode means a table is read from a file, it can freely be edited (no immutable fields)
  var editMode: Boolean = false


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


      field.listenTo(field.keys)
      field.listenTo(field.mouse.clicks)
      field.reactions += {
        case e: MouseClicked => penField.requestFocus; e.consume
        case KeyPressed(_,Key.BackSpace,_,_) => writeDigit('-', field)
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
  penField.background = SudokuGrid.penFieldUnoriginalColor



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
  def importFromFile(file: String, testing: Boolean): Unit = {
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
          case SudokuGrid.emptyChar => textField.background = SudokuGrid.unoriginalFieldColor; ""
          case SudokuGrid.penChar => penField = textField; textField.background = SudokuGrid.penFieldUnoriginalColor;textField.requestFocus;""
          case x if (x.isDigit)  =>
            if (editMode == false){//if not editable, denote original field with a light gray background
              textField.background = SudokuGrid.originalFieldColor
              textField.editable = false
              chr.toString
            } else {
              textField.background = SudokuGrid.unoriginalFieldColor
              chr.toString
            }

        }

        //set cell value and original flag
        cell.value = chr match {
          case SudokuGrid.emptyChar | SudokuGrid.penChar => cell.original = false; '-'
          case x if (x.isDigit) => cell.original = !testing && !editMode; chr
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
  //@param c -> char to be written ('-' denotes an empty char, which in fact clears the field)
  //@param field -> text field which will be filled
  private def writeDigit(c: Char, field: TextField): Unit = {
    val cell = gridMap(field)
    if(cell.original == true){
      Dialog.showMessage(null, message = "Nedozvoljeno mijenjanje originalnog polja!", title = "Nedozvoljen unos",messageType = Message.Error)
    }else{
      cell.value = c
      c match {
        case '-' => field.text = ""
        case _ => field.text = c.toString
      }
    }
  }


  //method that resets the text field's background
  private def resetFieldBackground(field: TextField): Unit = {
    val cell = gridMap(field)
    cell.original match {
      case true => field.background = SudokuGrid.originalFieldColor
      case false => field.background = SudokuGrid.unoriginalFieldColor
    }
  }

  //method that colors the field's background according to its original flag
  //@param field -> chosen field
  private def colorFieldPenBackground(field: TextField): Unit = {
    val cell = gridMap(field)
    cell.original match {
      case true => field.background = SudokuGrid.penFieldOriginalColor
      case false => field.background = SudokuGrid.penFieldUnoriginalColor
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

  //method which sets the pen on a certain position
  //@param coord -> coordinates of the new pen position
  private def setPen(coord: (Int, Int)): Unit = {
    resetFieldBackground(penField)
    val newField = grid(coord._1)(coord._2)
    colorFieldPenBackground(newField)
    newField.requestFocus
    penField = newField
  }


  //method which returns a row
  //row is returned as Array[Int]
  //@param row -> index of the row
  private def row(row: Int): Array[Int] = {
    //take the row, apply map function so every field returns its digit (or 0 if it's empty)
    grid(row).map(field => if(!field.text.isEmpty) field.text.toInt else 0)
  }

  //method which returns a column
  //column is returned as Array[Int]
  //@param col -> index of the column
  private def col(col: Int): Array[Int] = {
    grid.map(arrFlds => if (!arrFlds(col).text.isEmpty) arrFlds(col).text.toInt else 0)
  }

  //method which returns a square within the sudoku table
  //square is returned as Array[Int]
  //@param square -> index of the square (squares are indexed from left to right, starting with 0, ending with 8)
  private def square(square: Int): Array[Int] = {
    //take groups of 3 rows from the matrix
    val rowBlocks = grid.grouped(SudokuGrid.squareDimension).toArray

    val horizontalGroup: Int = square / SudokuGrid.squareDimension
    val verticalGroup: Int = square % SudokuGrid.squareDimension

    val arrMatrices = rowBlocks(horizontalGroup).map(row => row.grouped(SudokuGrid.squareDimension).toArray)

    arrMatrices.map(matrix => matrix(verticalGroup)).flatten.map(textField => if(!textField.text.isEmpty) textField.text.toInt else 0)
  }

  //method which returns a square within the sudoku table that the coordinates belong to
  //square is returned as Array[Int]
  //@param coord -> coordinates of the field whose square is returned
  private def square(coord: (Int, Int)): Array[Int] = {
    val row = coord._1
    val col = coord._2
    val squareInd = (col / SudokuGrid.squareDimension) + row - (row % SudokuGrid.squareDimension)
    square(squareInd)
  }


  //method which checks whether the row/col/square is complete
  //according to sudoku rules
  //@ param arr -> array which contains the elements of a row/col/square
  private def isComplete(arr: Array[Int]): Boolean = {
    //for each number from 1 to 9 check whether it is in the array
    (1 to 9).forall(x => arr.contains(x))
  }

  //method which checks whether the sudoku is solved or not
  def isSudokuSolved: Boolean = {
    //forall -> takes a function p as parameter, returns true if p(x) is true for every x in the collection
    //return true if every row, col and square is complete
    (0 to 8).forall( x => isComplete(row(x)) && isComplete(col(x)) && isComplete(square(x)))
  }


  //method which checks the current sudoku solution and notifies the user
  def checkSolution: Unit = {
    isSudokuSolved match {
      case true => {
        Dialog.showMessage(null, "Rjesenje sudoku igre je ispravno, igra je gotova!", title = "Rjesenje ispravno")
        SudokuFrame.sudokuLoaded = false
        clearAll(0,0)
      }
      case false => Dialog.showMessage(null, "Rjesenje sudoku igre je neispravno!", title = "Rjesenje neispravno")
    }
  }

  //method which resets the board
  //@param coord -> coordinates of the field to be reset
  @scala.annotation.tailrec
  private def clearAll(coord: (Int,Int)): Unit = {
      coord match {
        case (-1,-1) => //end of table
          penField = grid(0)(0); penField.background = SudokuGrid.penFieldUnoriginalColor; penField.requestFocus
        case (x, y) =>
          clearField(grid(x)(y)); clearAll(nextField(coord))
      }
  }


  //method which checks whether the row/col/square is valid
  //according to sudoku rules (has no duplicates)
  //@ param arr -> array which contains the elements of a row/col/square
  private def isValid(arr: Array[Int]): Boolean = {
    //first create an array without 0' (empty fields) (bcs they are duplicates but do not count)
    val filteredArray: Array[Int] = arr.filter(e => e != 0)
    //check whether array of distinct values has the same length as this array
    filteredArray.distinct.length == filteredArray.length
  }



  //method which checks if the current sudoku board is solvable
  //i.e. checks whether all rows/columns/squares are currently valid (without duplicates)
  def isSudokuSolvable: Boolean = {
    //forall -> takes a function p as parameter, returns true if p(x) is true for every x in the collection
    //return true if every row, col and square is valid
    (0 to 8).forall( x => isValid(row(x)) && isValid(col(x)) && isValid(square(x)))
  }



  //method which outputs the sudoku board into a file
  //@param file -> fajl u koji se vrsi upis
  def outputToFile(file: String): Unit = {
    //adding output directory path to file
    val fileFullPath = SudokuGrid.outputDir + file
    // PrintWriter from Java
    val pw = new PrintWriter(new File(fileFullPath ))

    for (row <- grid){
      for(field <- row){
        val cell = gridMap(field)

        if(field == penField){
          pw.write('P')
        }else{
          pw.write(cell.value)
        }

      }
      pw.write("\n")
    }
    pw.close
    Dialog.showMessage(null, "Sudoku tabela uspjesno sacuvana!", title = "Tabela sacuvana")
  }

  //method which maps a function to all fields in the same row of a given field
  //excluding the given field
  //@param field -> field selected by the pen
  //@param f -> function applied to a field
  private def mapToRow(field: TextField)(f: TextField => Unit): Unit = {
      //extract the corresponding cell
    val cell: Cell = gridMap(field)
    for (c <- 0 to 8) if (c != cell.col && grid(cell.row)(c).text == field.text) f(grid(cell.row)(c))
  }

  //method which maps a function to all fields in the same column of a given field
  //excluding the field
  //@param field -> field selected by the pen
  //@param f -> function applied to a field
  private def mapToCol(field: TextField)(f: TextField => Unit): Unit = {
    val cell: Cell = gridMap(field)
    for (r <- 0 to 8) if (r != cell.row && grid(r)(cell.col).text == field.text) f(grid(r)(cell.col))
  }

  //method which maps a function to all fields in the same square of a given field
  //excluding the field
  //@param field -> field selected by the pen
  //@param f -> function applied to a field
  private def mapToSquare(field: TextField)(f: TextField => Unit): Unit = {
    val cell: Cell = gridMap(field)
    val lr = (cell.row / 3) * 3; val rr = lr until lr + 3
    val lc = (cell.col / 3) * 3; val cr = lc until lc + 3
    for(r <- rr)
      for(c <- cr){
        if((r != cell.row || c != cell.col) && grid(r)(c).text == field.text) f(grid(r)(c))
      }
  }

  //method which clears a field and its cell
  //@param field -> field to be cleared
  private def clearField(field: TextField): Unit = {
    val cell: Cell = gridMap(field)
    field.text = ""
    field.background = SudokuGrid.unoriginalFieldColor
    cell.original = false
    cell.value = '-'

  }

  //method which filters the row and colum of the selected field (marked by pen)
  def filterRowCol: Unit = {
    if(!penField.text.isEmpty){
      mapToRow(penField)(clearField)
      mapToCol(penField)(clearField)
    }//if filed pointed by the pen is empty, do nothing
  }

  //method which filters the row and colum with the pen being moved to a certain position
  //@param coord -> coordinates of the starting pen position
  def filterRowColOnField(coord: (Int,Int)): Unit = {
    //move pen
    setPen(coord)
    //filter
    filterRowCol
  }

  //method which filters the square of the selected field (marked by pen)
  def filterSquare: Unit = {
    if(!penField.text.isEmpty){
      mapToSquare(penField)(clearField)
    }//if filed pointed by the pen is empty, do nothing
  }

  //method which filters a square with the pen being moved to a certain position
  //@param coord -> coordinates of the starting pen position
  def filterSquareOnFiled(coord: (Int,Int)): Unit = {
    //move pen
    setPen(coord)
    //filter
    filterSquare
  }


  //method which applies a 'change' to a certain field
  //if applicable (field's text is non-empty)
  //@param field -> chosen field
  private def changeField(field: TextField): Unit = {
    if(!field.text.isEmpty){
      field.text = (10 - field.text.toInt).toString
    }
  }


  //method which applies a 'change' to all valid fields
  //@param coord -> coordinates of the current field to be changed
  def changeTable(coord: (Int,Int)): Unit = {

    coord match {
      case (-1,-1) => //nothing
      case (x,y) =>
        changeField(grid(x)(y)); changeTable(nextField(coord))
    }

  }


  //method which writes new values to fields according to the transposed grid
  //@param coord -> coordinates of the field in which the new value will be written
  //@param transposed -> transposed grid as Array[Array[Int]]
  @scala.annotation.tailrec
  private def transposeField(coord: (Int,Int), transposed: Array[Array[Int]]): Unit = {
    coord match {
      case (-1,-1) =>
      case (x, y) =>
        transposed(x)(y) match {
          case 0 => writeDigit('-',grid(x)(y))
          case v if (v >= 1 && v <= 9) => writeDigit(v.toString.charAt(0),grid(x)(y))
        }
        transposeField(nextField(coord),transposed)
    }
  }


  //method which transposes the grid
  def transposeTable: Unit = {
    //get current elements as Int
    val currMatrix: Array[Array[Int]] = grid.map(row => row.map (field => if(field.text.isEmpty) 0 else field.text.toInt))
    //transpose the matrix
    val transposedMatrix: Array[Array[Int]] = currMatrix.transpose

    transposeField((0,0),transposedMatrix)
  }

  //method which checks the validity of a number in a certain place on the sudoku board
  //i.e. checks whether the number is already present in row/col/square
  //@param value -> value to be checked
  //@param coord -> coordinates where the value would potentially be placed
  private def checkValidity(value: Int, coord: (Int,Int)): Boolean = {
    val r = coord._1
    val c = coord._2

    val currentRow: Array[Int] = row(r)
    val currentCol: Array[Int] = col(c)
    val currentSquare: Array[Int] = square(coord)

    (!currentRow.contains(value)) && (!currentCol.contains(value)) && (!currentSquare.contains(value))

  }


  //method which returns the coordinates of the next field
  //if possible, return field to the left
  //else return the first field in the next row
  //@param coord -> coordinates (row,col) of the current field
  private def nextField(coord: (Int,Int)): (Int,Int) = {
    coord match {
      case (8,8) => (-1,-1) //end of table
      case (x, 8) if (x < 8) => (x + 1, 0)
      case (x, y) if (y < 8) => (x, y + 1)
    }
  }


  //method which checks if all fields are set
  //@param coord -> coordinates (row,col) of the current field
  @scala.annotation.tailrec
  private def allFieldsSet(coord: (Int,Int)): Boolean = {
    coord match {
      case (-1, -1) => true
      case (x, y) =>
        if (grid(x)(y).text.isEmpty)
          false
        else
          allFieldsSet(nextField(coord))
    }
  }


  //method which solves the sudoku board (writes valid values on remaining fields)
  //@param coord (always (0,0)) -> starting position from which the sudoku is solved
  def solve(coord: (Int,Int)): Boolean = {
    if(allFieldsSet(0,0))
      true
    else {
      val currField: TextField = grid(coord._1)(coord._2)

      if (!currField.text.isEmpty) {
        solve(nextField(coord))
      }else{
        for(value <- 1 to 9){
          if(checkValidity(value, coord)){
            writeDigit(value.toString.charAt(0),currField)
            if(solve(nextField(coord)))
              return true
            else
              writeDigit('-',currField)
          }
        }
        false
      }
    }
  }

  //method which clears all unoriginal fields in the sudoku (user written)
  //@param coord -> coordinates of the current field being cleared
  @scala.annotation.tailrec
  private def clearAllUnoriginal(coord: (Int,Int)): Unit = {
    coord match {
      case (-1,-1) => //end of table

      case (x, y) =>
        if (!gridMap(grid(x)(y)).original)
          clearField  (grid(x)(y))
        clearAllUnoriginal(nextField(coord))
    }
  }

  //method which returns the next field's coordinates (as tuple)
  //and the char denoting the move from the current field
  //when writing the solution into a file
  //@param coord -> coordinates of the current field
  private def nextFieldSolutionOutput(coord: (Int,Int)):((Int,Int), Char) = {
    coord match {
      case (8,8) => ((-1,-1),'n') //end of table
      case (x, 8) if (x % 2 == 0) => ((x + 1, 8), 'd')
      case (x, 0) if (x % 2 == 1) => ((x + 1, 0), 'd')
      case (x, y)  => if (x % 2 == 0) ((x, y + 1), 'r') else ((x, y - 1), 'l')
    }
  }

  //method which outputs the moves of the current sudoku's solution into a file
  //@param pw -> PrintWriter linked to the output file
  //@param tup -> tuple containing the coordinates of the current field(also a tuple)
  // and move character (move relative to the previos field's coordinates)
  @scala.annotation.tailrec
  private def outputMovesToFile(pw: PrintWriter, tup: ((Int,Int), Char)): Unit = {
    val coord = tup._1
    val chr = tup._2

    chr match {
      case 'n' => //do nothing
      case x =>
        pw.write(x)
        pw.write("\n")
    }

    coord match {
      case (-1,-1) => //end
      case (x,y) => {
        val cell = gridMap(grid(x)(y))
        if(!cell.original){
          pw.write(grid(x)(y).text)
          pw.write("\n")
        }
        outputMovesToFile(pw,nextFieldSolutionOutput(x,y))
      }

    }
  }




  //method which outputs the solution of a sudoku table into a file
  //file contains moves and inputs each in it's own row
  // moves: d, u, l, r
  // inputs: [1,9]
  //@param file -> file to input moves
  private def outputSolutionToFile(file: String): Unit = {

    //file's full path
    val fileFullPath = SudokuGrid.inputDirMoves + file
    // PrintWriter from Java
    val pw = new PrintWriter(new File(fileFullPath ))

    //output moves to file starting from (0,0)
    // 'n' char denotes an invalid move as (0,0) is the starting point
    outputMovesToFile(pw,((0,0),'n'))

    //close file
    pw.close
  }


  //method which solves the sudoku and prompts the user to write the solution in a file
  //if the sudoku can be solved
  def solveSudokuAndWriteSolution: Unit = {
    //first clear all unoriginal fields (that the user has written)
    //TODO: Reconsider this option, maybe the user wants to solve the sudoku along with his/her input?
    clearAllUnoriginal(0,0)
    //second solve the board
   if(solve(0,0)){
     //if solved, prompt the user to name a file where the solution would be written in
     Dialog.showInput(null,message = "Unesi ime fajla za rjesenje",initial = "") match {
       case Some(x) if (!x.isEmpty) => outputSolutionToFile(x)
       case Some(x) if (x.isEmpty) => Dialog.showMessage(null, "Ime fajla ne moze biti prazno!", title = "Nevalidno ime fajla",messageType = Message.Error)
       case None => Dialog.showMessage(null, "Ime fajla ne moze biti prazno!", title = "Nevalidno ime fajla",messageType = Message.Error)
     }
   } else {
     //else notify the user that the sudoku was not solved
     Dialog.showMessage(null, message = "Sudoku nije mogao biti rijesen!", title = "Nerjesiv sudoku",messageType = Message.Error)
   }
  }




  //method which reads the sudoku solution from a file
  //@param file -> file from which the moves will be read
  def importSolutionFromFile(file: String): Unit = {
    //first, place the pen on the starting field(0,0)
    //this is done because sudoku solution's moves are written
    //starting from (0,0)

    resetFieldBackground(penField)
    penField = grid(0)(0)
    colorFieldPenBackground(penField)
    penField.requestFocus


    //call import moves
    importMovesFromFile(file)
  }

  //TESTING PURPOSE ONLY
  //method which gets a field's value as Int
  //@param coord -> coordinates of the field
  def getFieldValueInt(coord:(Int,Int)):Int = {
    val textField: TextField = grid(coord._1)(coord._2)
    textField.text match{
      case x if(x.isEmpty) => 0
      case x if(!x.isEmpty) => x.toInt
    }
  }

  //TESTING PURPOSE ONLY
  //method which gets a field's value as String
  //@param coord -> coordinates of the field
  def getFieldValueString(coord:(Int,Int)):String = {
    val textField: TextField = grid(coord._1)(coord._2)
    textField.text
  }

  //TESTING PURPOSE ONLY
  //method which sets the value of a field
  //@param coord -> coordinates of the field
  //@param value -> value of the field
  def setFieldValue(coord: (Int,Int),value: Int): Unit = {
    val textField: TextField = grid(coord._1)(coord._2)
    val cell: Cell = gridMap(textField)

    value match {
      case 0 =>
        textField.text = ""
        cell.value = '-'
      case x if (x >=1 && x <=9) =>
        textField.text = value.toString
        cell.value = value.toString.charAt(0)
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
  //sudoku board for testing
  val testBoardName = "testboard.txt"

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
  val squareBorder: Border = BorderFactory.createLineBorder(Color.BLACK,3)

  //dimension of table panel
  val preferredBoardSize = new Dimension(600,600)


  //unoriginal field color
  val unoriginalFieldColor: Color = Color.WHITE
  //original field color
  val originalFieldColor: Color = Color.LIGHT_GRAY
  //pen on unoriginal field color
  val penFieldUnoriginalColor: Color = Color.CYAN
  //pen on original field color
  val penFieldOriginalColor: Color = Color.GRAY

}