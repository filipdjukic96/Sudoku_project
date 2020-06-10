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
  val basicMenu: Menu = new Menu("Igraj sudoku")

  //advanced options menu
  val advancedMenu: Menu = new Menu("Napravi sudoku tabelu")


  //button panel
  val buttonPanel: BoxPanel = new BoxPanel(Orientation.Horizontal)


  //menu representing sequences
  val sequencesMenu: Menu = new Menu("Sekvence")

  //menu representing compositions
  val compositionsMenu: Menu = new Menu("Kompozicije")


  //flag which denotes whether the sudoku table has been loaded
  var sudokuLoaded: Boolean = false;


  //builds the basic menu
  buildBasicMenu

  //build the advanced menu
  buildAdvancedMenu

  //build the button panel
  buildButtonPanel

  //build the sequences menu
  buildSequencesMenu

  //build the compositions menu
  buildCompositionsMenu


  //method which builds the basic menu
  private def buildBasicMenu: Unit = {
    bar.contents += basicMenu

    //menu contents
    //while creating menu contents listeners (actions) are created
    val importFile: MenuItem = new MenuItem(new Action("Ucitaj iz fajla"){
      def apply: Unit = {
        val file: File = new File(SudokuGrid.inputDir)
        val fileChooser: FileChooser = new FileChooser(file)
        //if open was clicked
        if (fileChooser.showOpenDialog(null) == FileChooser.Result.Approve){
          sudokuGrid.editMode = false //no editing allowed
          sudokuLoaded = true
          sudokuGrid.importFromFile(fileChooser.selectedFile.getName)
        }

      }
    })

    val chooseSequence: MenuItem = new MenuItem(new Action("Izaberi sekvencu poteza iz fajla"){
      def apply: Unit = {
        //forbidden if no sudoku has been loaded
        if(sudokuLoaded && !sudokuGrid.editMode){
          val file: File = new File(SudokuGrid.inputDirMoves)
          val fileChooser: FileChooser = new FileChooser(file)
          //if open was clicked
          if (fileChooser.showOpenDialog(null) == FileChooser.Result.Approve){
            sudokuGrid.importMovesFromFile(fileChooser.selectedFile.getName)
          }
        }
      }
    })



    val chooseSolution: MenuItem = new MenuItem(new Action("Izaberi rjesenje u potezima iz fajla"){
      def apply: Unit = {
        //forbidden if no sudoku has been loaded
        if(sudokuLoaded && !sudokuGrid.editMode){
          val file: File = new File(SudokuGrid.inputDirMoves)
          val fileChooser: FileChooser = new FileChooser(file)
          //if open was clicked
          if (fileChooser.showOpenDialog(null) == FileChooser.Result.Approve){
            sudokuGrid.importSolutionFromFile(fileChooser.selectedFile.getName)
          }
        }
      }
    })



    val checkSolution: MenuItem = new MenuItem(new Action("Provjeri rjesenje igre"){
      def apply: Unit = {
        //forbidden if no sudoku has been loaded
        if(sudokuLoaded && !sudokuGrid.editMode){
          sudokuGrid.checkSolution
        }
      }
    })

    //add submenus
    basicMenu.contents += importFile
    //basicMenu.contents += chooseTable
    basicMenu.contents += chooseSequence
    basicMenu.contents += chooseSolution
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
        //permitted only if edit mode
        if(sudokuGrid.editMode){
          sudokuGrid.filterRowCol
        }
      }
    })



    val filterSquare: MenuItem = new MenuItem(new Action("Filtriranje kvadrata"){
      def apply: Unit = {
        //permitted only if edit mode
        if(sudokuGrid.editMode){
          sudokuGrid.filterSquare
        }
      }
    })


    val applyChange: MenuItem = new MenuItem(new Action("Izvrsi zamjenu"){
      def apply: Unit = {
       if(sudokuGrid.editMode){ //only if the grid is in edit mode
         sudokuGrid.changeTable(0,0) //begins on the starting field
       }
      }
    })


    val applyTranspose: MenuItem = new MenuItem(new Action("Transponuj"){
      def apply: Unit = {
        if(sudokuGrid.editMode){ //only if the grid is in edit mode
          sudokuGrid.transposeTable
        }
      }
    })


    val exportToFile: MenuItem = new MenuItem(new Action("Sacuvaj tabelu u fajl"){
      def apply: Unit = {
        //permitted only if edit mode
        if(sudokuGrid.editMode){
          if(sudokuGrid.isSudokuSolvable){

            Dialog.showInput(null,message = "Unesi fajl",initial = "") match {
              case Some(x) if (!x.isEmpty) => sudokuGrid.outputToFile(x)
              case Some(x) if (x.isEmpty) => Dialog.showMessage(null, "Ime fajla ne moze biti prazno!", title = "Nevalidno ime fajla",messageType = Message.Error)
              case None => //user clicked Cancel
            }

          }else{
            Dialog.showMessage(null, "Sudoku tabelu nije moguce sacuvati jer je nerjesiva!", title = "Tabela nerjesiva")
          }
        }
      }
    })


    //add submenus
    advancedMenu.contents += createSudoku
    advancedMenu.contents += filterRowCol
    advancedMenu.contents += filterSquare
    advancedMenu.contents += applyChange
    advancedMenu.contents += applyTranspose
    advancedMenu.contents += exportToFile

  }


  //method which builds the button panel
  private def buildButtonPanel: Unit = {
    //button to solve sudoku
    val solveButton: Button = new Button(new Action("Rijesi sudoku"){
      def apply: Unit = {
        //forbidden to click solve if no sudoku has been loaded
        if(sudokuLoaded && !sudokuGrid.editMode){
          sudokuGrid.solveSudokuAndWriteSolution
          //println("solve")
        }
      }
    })

    buttonPanel.contents += solveButton
  }

  //method which adds a new sequence
  //@param seqRaw -> sequence in raw format (operations separated by '-')
  //@param paramsRaw -> sequence parameters in raw format (tuples separated by '-')
  //@param seqName -> sequence name
  private def addSequence(seqRaw: String, paramsRaw: String, seqName: String): Unit = {
    val seqLst: List[String] = seqRaw.split('-').toList.map(op => op.trim)

    val paramsLst: List[(Int,Int)] =
      if(paramsRaw.isEmpty) //if no parameters are required
        List()
      else
        paramsRaw.split('-').toList.map(params => params.trim).map(params => makeTuple(params))



    val sequence: Sequence = new Sequence(sudokuGrid, seqName, seqLst, paramsLst)

    val newSequence: MenuItem = new MenuItem(new Action(seqName){
      def apply: Unit = {
        if(sudokuGrid.editMode){
          sequence.execute
        }
      }
    })

    sequencesMenu.contents += newSequence

  }

  //method which adds a new composition
  //@param compRaw -> composition in raw format (operations separated by '-')
  //@param paramRaw -> composition parameter in raw format
  private def addComposition(compRaw: String, paramRaw: String, compName: String): Unit = {
    val compLst: List[String] = compRaw.split('-').toList.map(op => op.trim)

    val paramTuple: (Int,Int) = if(paramRaw.isEmpty) //if no parameters are required
                                  (-1,-1)
                                else
                                  makeTuple(paramRaw)
    


    val composition: Composition = new Composition(sudokuGrid, compName, compLst, paramTuple)

    val newComposition: MenuItem = new MenuItem(new Action(compName){
      def apply: Unit = {
       if(sudokuGrid.editMode){
         composition.execute
       }
      }
    })

    compositionsMenu.contents += newComposition

  }

  //method which prompts the user to input a new sequence/composition name
  private def inputNewSeqCompName(op: String): String = {
    if(op == "sequence"){
      Dialog.showInput(null, message = "Unesi ime sekvence",initial = "") match {
        case Some(x) if (!x.isEmpty) => x
        case Some(x) if (x.isEmpty) => ""
        case None =>  ""//user clicked Cancel
      }
    }else{
      Dialog.showInput(null, message = "Unesi ime kompozicije",initial = "") match {
        case Some(x) if (!x.isEmpty) => x
        case Some(x) if (x.isEmpty) => ""
        case None =>  ""//user clicked Cancel
      }
    }
  }

  //method which adds a new sequence
  //@param seqRaw -> sequence in raw format (operations separated by '-')
  //@param paramsRaw -> sequence parameters in raw format (tuples separated by '-')
  private def inputSequenceName(seqRaw: String, paramsRaw: String): Unit = {

    //prompt the user to choose a name for his/her sequence
    inputNewSeqCompName("sequence") match {
      case x if (x.isEmpty) => Dialog.showMessage(null, "Ime sekvence ne moze biti prazno!", title = "Prazno ime sekvence",messageType = Message.Error)
      case x if (!x.isEmpty) => addSequence(seqRaw, paramsRaw, x)
    }
  }

  //method which adds a new composition
  //@param seqRaw -> composition in raw format (operations separated by '-')
  //@param paramsRaw -> composition parameter in raw format (tuples separated by '-')
  private def inputCompositionName(compRaw: String, paramRaw: String): Unit = {

    //prompt the user to choose a name for his/her sequence
    inputNewSeqCompName("composition") match {
      case x if (x.isEmpty) => Dialog.showMessage(null, "Ime kompozicije ne moze biti prazno!", title = "Prazno ime kompozicije",messageType = Message.Error)
      case x if (!x.isEmpty) => addComposition(compRaw, paramRaw, x)
    }
  }

  //method which checks whether the sequence/composition is valid
  //@param seq -> sequence/composition of operations
  //each operation is separated by '-'
  private def areOperationsValid(ops: String): Boolean = {
    val opsLstRaw: List[String] = ops.split('-').toList
    val opsLst:List[String] = opsLstRaw.map(op => op.trim)
    //set of valid operations
    val setValid: Set[String] = Set("transpose","filterSquare","filterRowCol","exchange")
    //check if every list member is contained within the valid set
    opsLst.forall(op => setValid.contains(op))
  }


  //method which makes a tuple out of two params separated by ','
  //@param params -> parameters separated by ',' (ex. 1,2)
  private def makeTuple(params: String): (Int,Int) = {
    val lstParams: List[String] = params.split(',').toList.map(par => par.trim)
    val paramOne: String = lstParams(0)
    val paramTwo: String = lstParams(1)
    (paramOne.toInt, paramTwo.toInt)
  }

  //method which checks whether the tuple is valid
  //@param tup -> tuple representing (row,col)
  private def isTupleValid(tup: (Int,Int)): Boolean = {
    val row = tup._1
    val col = tup._2
    (row >= 0 && row <=8 && col >=0 && col <=8)
  }

  //method which checks whether the params are valid for a certain sequence
  //@param seqRaw -> sequence in raw format
  //@param paramsRaw -> parameters in raw format
  private def areParamsValid(seqRaw: String, paramsRaw: String): Boolean = {
    //transform the raw sequence into a list of operations
    val seqLst: List[String] = seqRaw.split('-').toList.map(op => op.trim)
    //set of operations which require parameters
    val setParametrizedOps: Set[String] = Set("filterRowCol", "filterSquare")
    //number of parametrized operations in this list
    val numParametrized = seqLst.filter(op => setParametrizedOps.contains(op)).length

    //list of parameters as strings (ex. 1,2) where the two parameters are separated by ','
    val paramsLstStr: List[String] = paramsRaw.split('-').toList.map(params => params.trim)
    //list of all parameters as tuples of (Int,Int)
    val paramsLst: List[(Int,Int)] = paramsLstStr.map(params => makeTuple(params))
    //if all tuples are valid and the number of parameters is equal to the number of parametrized operations
    paramsLst.forall(tuple => isTupleValid(tuple)) && (numParametrized == paramsLst.length)
  }

  //method which checks whether the param is valid for a certain composition
  //@param compRaw -> composition in raw format
  //@param paramRaw -> parameter in raw format
  private def isParamValid(compRaw: String, paramRaw: String): Boolean = {
    //transform the raw composition into a list of operations
    val compLst: List[String] = compRaw.split('-').toList.map(op => op.trim)
    //set of operations which require parameters
    val setParametrizedOps: Set[String] = Set("filterRowCol", "filterSquare")
    //number of parametrized operations in this list
    val numParametrized = compLst.filter(op => setParametrizedOps.contains(op)).length


    //make parameter tuple
    val paramTuple: (Int,Int) = makeTuple(paramRaw.trim)
    //if the tuple is valid and the number of parametrized operations is greater than zero
    isTupleValid(paramTuple) && (numParametrized > 0)
  }

  //method which checks whether the sequence/composition requires no parameters
  //@param opsRaw -> sequence in raw format
  private def noParamsRequired(opsRaw: String): Boolean = {
    //transform the raw sequence into a list of operations
    val opsLst: List[String] = opsRaw.split('-').toList.map(op => op.trim)
    //set of operations which require parameters
    val setParametrizedOps: Set[String] = Set("filterRowCol", "filterSquare")
    //number of parametrized operations in this list
    val numParametrized = opsLst.filter(op => setParametrizedOps.contains(op)).length
    //if the number of parametrized operations is equal to 0, return true, else return false
    numParametrized == 0
  }


  //method which prompts the user to add sequence params
  //@params seqRaw -> sequence in raw format
  private def inputSequenceParams(seqRaw: String): Unit = {
    Dialog.showInput(null, message = "Unesi parametre sekvence (npr. 1,2-3,4-5,6", initial = "") match {
      case Some(x) if (!x.isEmpty) => {
        if (areParamsValid(seqRaw, x))
          inputSequenceName(seqRaw, x)
        else
          Dialog.showMessage(null, "Parametri su nevalidni!", title = "Nevalidni parametri",messageType = Message.Error)

      }

      case Some(x) if (x.isEmpty) => {
        if(noParamsRequired(seqRaw))
          inputSequenceName(seqRaw, x)
        else
          Dialog.showMessage(null, "Lista parametara prazna!", title = "Prazni parametri",messageType = Message.Error)
      }
      case None => //user clicked Cancel
    }
  }

  //method which prompts the user to add composition params
  //@params compRaw -> composition in raw format
  private def inputCompositionParam(compRaw: String): Unit  = {
    Dialog.showInput(null, message = "Unesi parametar kompozicije (npr. 1,2)", initial = "") match {
      case Some(x) if (!x.isEmpty) => {
        if (isParamValid(compRaw, x))
          inputCompositionName(compRaw, x)
        else
          Dialog.showMessage(null, "Parametar je nevalidan!", title = "Nevalidan parametar",messageType = Message.Error)

      }

      case Some(x) if (x.isEmpty) => {
        if(noParamsRequired(compRaw))
          inputCompositionName(compRaw, x)
        else
          Dialog.showMessage(null, "Lista parametara prazna!", title = "Prazan parametar",messageType = Message.Error)
      }
      case None => //user clicked Cancel
    }
  }


  //method which prompts the user to add a new sequence, operations separated by '-'
  private def inputSequence: Unit = {
    Dialog.showInput(null, message = "Unesi sekvencu, operacije odvojene sa '-'", initial = "") match {
      case Some(x) if (!x.isEmpty) => {
        if (areOperationsValid(x))
          inputSequenceParams(x)
        else
          Dialog.showMessage(null, "Sekvenca je nevalidna!", title = "Nevalidna sekvenca",messageType = Message.Error)

      }
      case Some(x) if (x.isEmpty) => Dialog.showMessage(null, "Sekvenca je prazna!", title = "Prazna sekvenca",messageType = Message.Error)
      case None => //user clicked Cancel
    }
  }



  //method which builds the sequences menu with the first option (add sequence)
  private def buildSequencesMenu: Unit = {
    bar.contents += sequencesMenu

    val addSequenceMenuItem: MenuItem = new MenuItem(new Action("Dodaj sekvencu"){
      def apply: Unit = {
          inputSequence
      }
    })

    sequencesMenu.contents += addSequenceMenuItem
  }

  //method which prompts the user to add a new composition, operations separated by '-'
  private def inputComposition: Unit = {
    Dialog.showInput(null, message = "Unesi kompoziciju, operacije odvojene sa '-'", initial = "") match {
      case Some(x) if (!x.isEmpty) => {
        if (areOperationsValid(x))
          inputCompositionParam(x)
        else
          Dialog.showMessage(null, "Kompozicija je nevalidna!", title = "Nevalidna kompozicija",messageType = Message.Error)

      }
      case Some(x) if (x.isEmpty) => Dialog.showMessage(null, "Kompozicija je prazna!", title = "Prazna kompozicija",messageType = Message.Error)
      case None => //user clicked Cancel
    }
  }

  //method which builds the compositions menu with the first option (add composition)
  private def buildCompositionsMenu: Unit = {
    bar.contents += compositionsMenu

    val addCompositionMenuItem: MenuItem = new MenuItem(new Action("Dodaj kompoziciju"){
      def apply: Unit = {
        inputComposition
      }
    })

    compositionsMenu.contents += addCompositionMenuItem
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
