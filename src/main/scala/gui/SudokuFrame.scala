package gui

import swing._


/*
* klasa koja predstavlja glavni okvir aplikacije
*
* */
object SudokuFrame extends MainFrame {

  title = "Sudoku"

  //gridpanel koji sadrzi sve ostale panele
  val framePanel: BoxPanel = new BoxPanel(Orientation.Vertical)

  //sudoku tabela
  val sudokuGrid: SudokuGrid = new SudokuGrid

  //meni bar
  val bar: MenuBar = new MenuBar
  bar.preferredSize = new Dimension(600,20)

  //meni osnovnih opcija
  val basicMenu: Menu = new Menu("Osnovne opcije")

  //meni naprednih opcija
  //TODO: Odraditi napredni meni
  //val advancedMenu: Menu = new Menu("Napredne opcije")

  //napravi basic meni
  buildBasicMenu

  //TODO: Dodati poziv za advanced menu build



  def buildBasicMenu: Unit = {

    bar.contents += basicMenu

    //stavke osnovnog menija
    val importFile: MenuItem = new MenuItem("Ucitaj iz fajla")
    val chooseTable: MenuItem = new MenuItem("Izaberi iz ponudjenih tabela")
    val chooseSequence: MenuItem = new MenuItem("Izaberi sekvencu poteza iz fajl")
    val checkSolution: MenuItem = new MenuItem("Provjeri rjesenje igre")

    //dodavanje stavki
    basicMenu.contents += importFile
    basicMenu.contents += chooseTable
    basicMenu.contents += chooseSequence
    basicMenu.contents += checkSolution

    //TODO: Dodati listener-e za menije
  }



  //dodati u framePanel menije i gridPanel
  framePanel.contents += bar
  //TODO: Dodati napredni meni
  //framePanel.contents += advancedMenu
  framePanel.contents += sudokuGrid

  //framePanel kao dio main frame-a
  contents = framePanel



  //na kraju, postaviti visible na true
  visible = true
}
