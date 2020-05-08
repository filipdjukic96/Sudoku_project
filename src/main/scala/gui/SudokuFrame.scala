package gui

import java.io.File

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
  //TODO: Add advanced manu
  //val advancedMenu: Menu = new Menu("Napredne opcije")

  //builds the basic menu
  buildBasicMenu

  //TODO: Add advanced menu method call



  //method which builds the basic menu
  def buildBasicMenu: Unit = {

    bar.contents += basicMenu

    //menu contents
    //while creating menu contents listeners (actions) are created
    val importFile: MenuItem = new MenuItem(new Action("Ucitaj iz fajla"){
      def apply: Unit = {
        val file: File = new File(SudokuGrid.inputDir)
        val fileChooser: FileChooser = new FileChooser(file)
        //ako je kliknuto open, a ne cancel
        if (fileChooser.showOpenDialog(null) == FileChooser.Result.Approve){
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
    //TODO: Add listeners to other menu items
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



  //add menus and gridPanel into the framePanel
  framePanel.contents += bar
  //TODO: Add the advanced menu
  //framePanel.contents += advancedMenu
  framePanel.contents += sudokuGrid

  //framePanel kao dio main frame-a
  contents = framePanel



  //set visibility to true
  visible = true


}
