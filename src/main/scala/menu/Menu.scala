package menu

import java.io.File

import sudoku.Board

import scala.io.StdIn


object Menu {
  val stdin = StdIn //za citanje sa standardnog ulaza
  var exit: Boolean = false //flag koji oznacava kada je kraj aplikacije

  var board: Board = null //trenutna tabela s kojom se upravlja

  //funkcija koja ispisuje header
  def printHeader: Unit = {
    println("-----------------------------------")
    println("|            SUDOKU               |")
    println("-----------------------------------")
  }

  //funkcija koja ispisuje ponudjene opcije
  def printBasicMenu: Unit = {
    //selekcije
    println("1. Ucitavanje sudoku tabele iz fajla")
    println("2. Zapocinjanje igre izborom neke od tabela")
    println("3. Pomjeranje olovke")
    println("4. Unos cifre u prazno polje")
    println("5. Odigravanje sekvence poteza ucitavanjem iz fajla")
    println("6. Provjera ispravnosti rjesenja")

  }

  //TODO: Napredne opcije u projektu
  def printAdvancedMenu: Unit = ???


  //funkcija koja ucitava unos korisnika sa tastature
  def getInput: Int = {
    //citanje user input-a
    try{
      stdin.readLine.toInt
    }catch {
      case e: Exception => -1 //ako se desi izuzetak, vraca se -1
    }
  }

  /*
  * funkcija koja vrsi akciju zavisno od korisnikovog unosa
  * @param choice -> izbor korisnika
  * */
  def performAction(choice: Int): Unit = {
    choice match {
      case 0 => exit = true
      case 1 => pickFileForInput
      case 2 => pickAvailableTabels
      case 3 => movePencil
      case 4 => println("Stavka 4")//TODO
      case 5 => println("Stavka 5")//TODO
      case 6 => println("Stavka 6")//TODO
      case _ => println("Stavka pogresna")
    }
  }

  //funkcija za prikazivanje menija
  def showMenu: Unit = {
    while(!exit){
      printHeader
      printBasicMenu
      //printAdvancedMenu
      println("0. Izlaz") //0 za napustanje aplikacije

      val choice: Int = getInput
      performAction(choice)
    }
  }

  //FUNKCIJE KOJE SE ODNOSE NA KONKRETNE AKCIJE
  //-------------------------------------------

  //STAVKA 1
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /*
  * pravljenje tabele od fajl
  * @param file -> fajl iz kog se cita tabela
  * */
  def createBoard(file: String) = {
    board = Board(file)
    println(board)//TODO: Ostavljeno zbog debagovanja, izbaciti kasnije, mozda ostaviti???

  }

  //za ucitavanje sudoku tabele iz fajla
  def pickFileForInput: Unit = {
    print("Unesite ime fajla: ")
    val file = stdin.readLine
    createBoard(file)
  }

  //STAVKA 2
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /*
  * dohvata listu fajlova iz direktorijuma
  * @param dir -> direktorijum iz kog se ucitava
  * */
  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  /*
  * izlistava fajlove na std ulazu
  * @param lst -> lista fajlova za izlistavanje
  * */
  def printListOfFiles(lst: List[File]): Unit = {
    for (e <- lst.zipWithIndex){
      println(e._2 + 1 + ". "+ e._1.getName)
    }
  }

  //metoda koja dozvoljava izbor i ucitavanje tabele iz nekog od postojecih fajlova
  def pickAvailableTabels: Unit = {
    //izlistati sve fajlove za unos
    println("Izaberite tabelu iz odgovarajuceg fajla:")
    val lstFiles = getListOfFiles(Board.inputDir)
    printListOfFiles(lstFiles)
    val choice = getInput
    //provjera
    require(choice >= 1 && choice < lstFiles.length + 1, "Izabran nepostojeci fajl!")
    //kreiranje tabele na osnovu izabranog fajla
    createBoard(lstFiles(choice - 1).getName)
  }

  //STAVKA 3
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  //funkcija koja ispisuje opcije za pomjeranje olovke
  def printMovePencilOptions: Unit = {
    println("1. Dole")
    println("2. Gore")
    println("3. Lijevo")
    println("4. Desno")
  }

  //funkcija koja vrsti pomjeranje olovke
  def movePencil: Unit = {
    //tabela mora biti ucitana
    require(board != null, "Tabela nije ucitana!")
    //izlistavanje opcija pomjeranja
    printMovePencilOptions
    val choice: Int = getInput

    choice match {
      case 1 => board.movePencil('D')//down
      case 2 => board.movePencil('U')//up
      case 3 => board.movePencil('L')//left
      case 4 => board.movePencil('R')//right
      case _ => println("Stavka pogresna")
    }
    println(board)
  }

}
