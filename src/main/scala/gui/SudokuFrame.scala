package gui

import java.io.File

import scala.swing.Dialog.Message
import scala.swing.event.{KeyTyped, MouseClicked}
import swing._


/*
* class representing the main frame of the app
*
* */
object SudokuFrame extends MainFrame {

  title = "Sudoku"

  //gridpanel which contains all other panels
  val framePanel: BoxPanel = new BoxPanel(Orientation.Vertical)

  //sudoku table
  val sudokuGrid: SudokuGrid = new SudokuGrid

  //menu bar
  val bar: MenuBar = new MenuBar
  bar.preferredSize = new Dimension(600,20)

  //basic options menu
  val basicMenu: Menu = new Menu("Osnovne opcije")

  //advanced options menu
  val advancedMenu: Menu = new Menu("Napredne opcije")


  //button panel
  val buttonPanel: BoxPanel = new BoxPanel(Orientation.Horizontal)



  //builds the basic menu
  buildBasicMenu

  //build the advanced menu
  buildAdvancedMenu

  //build the button panel
  buildButtonPanel


  //method which builds the basic menu
  private def buildBasicMenu: Unit = {

    bar.contents += basicMenu

    //menu contents
    //while creating menu contents listeners (actions) are created
    val importFile: MenuItem = new MenuItem(new Action("Ucitaj iz fajla"){
      def apply: Unit = {
        val file: File = new File(SudokuGrid.inputDir)
        val fileChooser: FileChooser = new FileChooser(file)
        //if cancel was not clicked
        if (fileChooser.showOpenDialog(null) == FileChooser.Result.Approve){
          sudokuGrid.editMode = false //no editing allowed
          sudokuGrid.importFromFile(fileChooser.selectedFile.getName)
        }

      }
    })

    val chooseTable: MenuItem = new MenuItem(new Action("Izaberi iz ponudjenih tabela"){
      def apply: Unit = {
        val file: File = new File(SudokuGrid.inputDir)
        val fileChooser: FileChooser = new FileChooser(file)
        //ako je kliknuto open, a ne cancel
        if (fileChooser.showOpenDialog(null) == FileChooser.Result.Approve){
          sudokuGrid.importFromFile(fileChooser.selectedFile.getName)
        }
      }
    })

    val chooseSequence: MenuItem = new MenuItem(new Action("Izaberi sekvencu poteza iz fajla"){
      def apply: Unit = {
        val file: File = new File(SudokuGrid.inputDirMoves)
        val fileChooser: FileChooser = new FileChooser(file)
        //ako je kliknuto open, a ne cancel
        if (fileChooser.showOpenDialog(null) == FileChooser.Result.Approve){
          sudokuGrid.importMovesFromFile(fileChooser.selectedFile.getName)
        }
      }
    })

    val checkSolution: MenuItem = new MenuItem(new Action("Provjeri rjesenje igre"){
      def apply: Unit = {
        sudokuGrid.checkSolution
      }
    })

    //add submenus
    basicMenu.contents += importFile
    basicMenu.contents += chooseTable
    basicMenu.contents += chooseSequence
    basicMenu.contents += checkSolution

  }

  //method which builds the advanced menu
  private def buildAdvancedMenu: Unit = {
    bar.contents += advancedMenu

    //menu contents
    //while creating menu contents listeners (actions) are created

    //to create a new table from existing
    val createSudoku: MenuItem = new MenuItem(new Action("Kreiraj tabelu na osnovu postojece"){
      def apply: Unit = {
        val file: File = new File(SudokuGrid.inputDir)
        val fileChooser: FileChooser = new FileChooser(file)
        //if cancel was not clicked
        if (fileChooser.showOpenDialog(null) == FileChooser.Result.Approve){
          sudokuGrid.editMode = true //editing allowed
          sudokuGrid.importFromFile(fileChooser.selectedFile.getName)
        }

      }
    })


    //filter row and column
    val filterRowCol: MenuItem = new MenuItem(new Action("Filtriranje kolone i vrste"){
      def apply: Unit = {
        sudokuGrid.filterRowCol
      }
    })



    val filterSquare: MenuItem = new MenuItem(new Action("Filtriranje kvadrata"){
      def apply: Unit = {
        sudokuGrid.filterSquare
      }
    })

    val exportToFile: MenuItem = new MenuItem(new Action("Sacuvaj tabelu u fajl"){
      def apply: Unit = {
        if(sudokuGrid.isSolvable){
          Dialog.showInput(null,message = "Unesi fajl",initial = "") match {
            case Some(x) if (!x.isEmpty) => sudokuGrid.outputToFile(x)
            case Some(x) if (x.isEmpty) => Dialog.showMessage(null, "Ime fajla ne moze biti prazno!", title = "Nevalidno ime fajla",messageType = Message.Error)
            case None => Dialog.showMessage(null, "Ime fajla ne moze biti prazno!", title = "Nevalidno ime fajla",messageType = Message.Error)
          }

        }else{
          Dialog.showMessage(null, "Sudoku tabelu nije moguce sacuvati jer je nerjesiva!", title = "Tabela nerjesiva")
        }
      }
    })


    //TODO: Remove after testing, only temporary
    val applyChange: MenuItem = new MenuItem(new Action("Izvrsi zamjenu"){
      def apply: Unit = {
       if(sudokuGrid.editMode){ //only if the grid is in edit mode
         sudokuGrid.changeTable
       }
      }
    })
    //TODO: Remove after testing, only temporary
    val applyTranspose: MenuItem = new MenuItem(new Action("Transponuj"){
      def apply: Unit = {
        if(sudokuGrid.editMode){ //only if the grid is in edit mode
          sudokuGrid.transposeTable
        }
      }
    })


    //TODO: Implement other advanced menu features (operations, complex operations...)



    //add submenus
    //TODO: Add other submenus
    advancedMenu.contents += createSudoku
    advancedMenu.contents += filterRowCol
    advancedMenu.contents += filterSquare
    advancedMenu.contents += exportToFile
    advancedMenu.contents += applyChange
    advancedMenu.contents += applyTranspose

  }


  //method which builds the button panel
  private def buildButtonPanel: Unit = {
    //button to solve sudoku
    val solveButton: Button = new Button(new Action("Rijesi sudoku"){
      def apply: Unit = {
        if(!sudokuGrid.editMode){
          sudokuGrid.solveSudokuAndWriteSolution
          //println("solve")
        }
      }
    })

    buttonPanel.contents += solveButton
  }




  //add menus and gridPanel into the framePanel
  framePanel.contents += bar
  framePanel.contents += sudokuGrid
  framePanel.contents += buttonPanel

  //framePanel kao dio main frame-a
  contents = framePanel



  //set visibility to true
  visible = true


}
