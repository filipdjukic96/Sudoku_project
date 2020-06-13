package gui

import org.junit._

class SudokuTest {

  val sudokuGrid: SudokuGrid = new SudokuGrid


  //test table
  //7 3 5   6 1 4   8 9 2
  //8 4 2   9 7 3   5 6 1
  //9 6 1   2 8 5   3 7 4

  //2 8 6   3 4 9   1 5 7
  //4 1 3   8 5 7   9 2 6
  //5 7 9   1 2 6   4 3 8

  //1 5 7   4 9 2   6 8 3
  //6 9 4   7 3 8   2 1 5
  //3 2 8   5 6 1   7 4 P

  @Test
  def transposeTableTest(): Unit = {
    sudokuGrid.importFromFile(SudokuGrid.testBoardName, true)
    sudokuGrid.transposeTable

    //central square
    // 3 4 9    3 8 1
    // 8 5 7 => 4 5 2
    // 1 2 6    9 7 6

    Assert.assertEquals(sudokuGrid.getFieldValueInt(3,3),3)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(3,4),8)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(3,5),1)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(4,3),4)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(4,4),5)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(4,5),2)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(5,3),9)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(5,4),7)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(5,5),6)

  }

  @Test
  def exchangeTableTest(): Unit = {
    sudokuGrid.importFromFile(SudokuGrid.testBoardName, true)
    sudokuGrid.changeTable(0,0)

    //central square
    // 3 4 9    7 6 1
    // 8 5 7 => 2 5 3
    // 1 2 6    9 8 4



    Assert.assertEquals(sudokuGrid.getFieldValueInt(3,3),7)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(3,4),6)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(3,5),1)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(4,3),2)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(4,4),5)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(4,5),3)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(5,3),9)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(5,4),8)
    Assert.assertEquals(sudokuGrid.getFieldValueInt(5,5),4)

  }

  @Test
  def filterRowColTest(): Unit = {
    sudokuGrid.importFromFile(SudokuGrid.testBoardName, true)

    sudokuGrid.setFieldValue((1,4),5)
    sudokuGrid.setFieldValue((7,4),5)
    sudokuGrid.setFieldValue((4,2),5)
    sudokuGrid.setFieldValue((4,8),5)

    sudokuGrid.filterRowColOnField(4,4)

    //7 3 5   6 1 4   8 9 2           //7 3 5   6 1 4   8 9 2
    //8 4 2   9 5 3   5 6 1           //8 4 2   9 - 3   5 6 1
    //9 6 1   2 8 5   3 7 4           //9 6 1   2 8 5   3 7 4

    //2 8 6   3 4 9   1 5 7           //2 8 6   3 4 9   1 5 7
    //4 1 5   8 5 7   9 2 5   ->      //4 1 -   8 5 7   9 2 -
    //5 7 9   1 2 6   4 3 8           //5 7 9   1 2 6   4 3 8

    //1 5 7   4 9 2   6 8 3           //1 5 7   4 9 2   6 8 3
    //6 9 4   7 5 8   2 1 5           //6 9 4   7 - 8   2 1 5
    //3 2 8   5 6 1   7 4 P           //3 2 8   5 6 1   7 4 P

    Assert.assertEquals(sudokuGrid.getFieldValueString(4,4),"5")
    Assert.assertEquals(sudokuGrid.getFieldValueString(1,4),"")
    Assert.assertEquals(sudokuGrid.getFieldValueString(7,4),"")
    Assert.assertEquals(sudokuGrid.getFieldValueString(4,2),"")
    Assert.assertEquals(sudokuGrid.getFieldValueString(4,8),"")

  }

  @Test
  def filterSquareTest(): Unit = {
    sudokuGrid.importFromFile(SudokuGrid.testBoardName, true)

    sudokuGrid.setFieldValue((3,5),5)
    sudokuGrid.setFieldValue((4,5),5)
    sudokuGrid.setFieldValue((5,5),5)
    sudokuGrid.setFieldValue((5,3),5)

    sudokuGrid.filterSquareOnFiled(4,4)

    //7 3 5   6 1 4   8 9 2           //7 3 5   6 1 4   8 9 2
    //8 4 2   9 7 3   5 6 1           //8 4 2   9 7 3   5 6 1
    //9 6 1   2 8 5   3 7 4           //9 6 1   2 8 5   3 7 4

    //2 8 6   3 4 5   1 5 7           //2 8 6   3 4 -   1 5 7
    //4 1 3   8 5 5   9 2 6   ->      //4 1 3   8 5 -   9 2 6
    //5 7 9   5 2 5   4 3 8           //5 7 9   - 2 -   4 3 8

    //1 5 7   4 9 2   6 8 3           //1 5 7   4 9 2   6 8 3
    //6 9 4   7 3 8   2 1 5           //6 9 4   7 3 8   2 1 5
    //3 2 8   5 6 1   7 4 P           //3 2 8   5 6 1   7 4 P

    Assert.assertEquals(sudokuGrid.getFieldValueString(3,3),"3")
    Assert.assertEquals(sudokuGrid.getFieldValueString(3,4),"4")
    Assert.assertEquals(sudokuGrid.getFieldValueString(3,5),"")
    Assert.assertEquals(sudokuGrid.getFieldValueString(4,3),"8")
    Assert.assertEquals(sudokuGrid.getFieldValueString(4,4),"5")
    Assert.assertEquals(sudokuGrid.getFieldValueString(4,5),"")
    Assert.assertEquals(sudokuGrid.getFieldValueString(5,3),"")
    Assert.assertEquals(sudokuGrid.getFieldValueString(5,4),"2")
    Assert.assertEquals(sudokuGrid.getFieldValueString(5,5),"")

  }

  @Test
  def isSudokuSolvableTest(): Unit = {
    sudokuGrid.importFromFile("board1.txt", true)

    sudokuGrid.setFieldValue((7,3),3)

    //4 - 1   2 9 -   - 7 5
    //2 - -   3 - -   8 - P
    //- 7 -   - 8 -   - - 6

    //- - -   1 - 3   - 6 2
    //1 - 5   - - -   4 - 3
    //7 3 -   6 - 8   - - -

    //6 - -   - 2 -   - 3 -
    //- - 7   3 - 1   - - 4
    //8 9 -   - 6 5   1 - 7

    Assert.assertFalse(sudokuGrid.isSudokuSolvable)

    sudokuGrid.setFieldValue((7,3),0)

    Assert.assertTrue(sudokuGrid.isSudokuSolvable)

  }

  @Test
  def sudokuSolveTest(): Unit = {

    sudokuGrid.importFromFile("board1.txt",true)
    sudokuGrid.solve(0,0)
    Assert.assertTrue(sudokuGrid.isSudokuSolved)
  }

  @Test
  def sequenceTest(): Unit = {
    sudokuGrid.importFromFile("board1.txt", true)
    sudokuGrid.setFieldValue((3,0),2)
    sudokuGrid.setFieldValue((1,4),2)

    val operations: List[String] = List("filterRowCol","transpose","exchange")
    val params: List[(Int,Int)] = List((1,0))
    val sequence: Sequence = new Sequence(sudokuGrid,"seq1",operations,params)

    sequence.execute

    //4 - 1   2 9 -   - 7 5         6 8 - 	- 9 3 	4 - 2
    //2 - -   3 2 -   8 - P         - - 3 	- - 7 	- - 1
    //- 7 -   - 8 -   - - 6         9 - - 	- 5 - 	- 3 -

    //2 - -   1 - 3   - 6 2         8 7 - 	9 - 4 	- - -
    //1 - 5   - - -   4 - 3   ->    1 - 2 	- - - 	8 - 4
    //7 3 -   6 - 8   - - -         - - - 	7 - 2 	- 9 5

    //6 - -   - 2 -   - 3 -         - 2 - 	- 6 - 	- - 9
    //- - 7   - - 1   - - 4         3 - - 	4 - - 	7 - -
    //8 9 -   - 6 5   1 - 7         5 - 4 	8 7 - 	- 6 3


    Assert.assertEquals(sudokuGrid.getFieldValueString(0,0),"6")
    Assert.assertEquals(sudokuGrid.getFieldValueString(0,1),"8")
    Assert.assertEquals(sudokuGrid.getFieldValueString(0,2),"")
    Assert.assertEquals(sudokuGrid.getFieldValueString(1,0),"")
    Assert.assertEquals(sudokuGrid.getFieldValueString(1,1),"")
    Assert.assertEquals(sudokuGrid.getFieldValueString(1,2),"3")
    Assert.assertEquals(sudokuGrid.getFieldValueString(2,0),"9")
    Assert.assertEquals(sudokuGrid.getFieldValueString(2,1),"")
    Assert.assertEquals(sudokuGrid.getFieldValueString(2,2),"")

  }

  @Test
  def compositionTest(): Unit = {
    sudokuGrid.importFromFile("board1.txt", true)
    sudokuGrid.setFieldValue((1,1),2)
    sudokuGrid.setFieldValue((2,2),2)
    sudokuGrid.setFieldValue((3,0),2)

    val operations: List[String] = List("filterRowCol","filterSquare","transpose","exchange")
    val param: (Int,Int) = (1,0)
    val composition: Composition = new Composition(sudokuGrid,"comp1",operations,param)

    composition.execute

    //4 - 1   2 9 -   - 7 5         6 8 - 	- 9 3 	4 - 2
    //2 2 -   3 - -   8 - P         - - 3 	- - 7 	- - 1
    //- 7 2   - 8 -   - - 6         9 - - 	- 5 - 	- 3 -

    //2 - -   1 - 3   - 6 2         8 7 - 	9 - 4 	- - -
    //1 - 5   - - -   4 - 3   ->    1 - 2 	- - - 	8 - 4
    //7 3 -   6 - 8   - - -         - - - 	7 - 2 	- 9 5

    //6 - -   - 2 -   - 3 -         - 2 - 	- 6 - 	- - 9
    //- - 7   - - 1   - - 4         3 - - 	4 - - 	7 - -
    //8 9 -   - 6 5   1 - 7         5 - 4 	8 7 - 	- 6 3


    Assert.assertEquals(sudokuGrid.getFieldValueString(0,0),"6")
    Assert.assertEquals(sudokuGrid.getFieldValueString(0,1),"8")
    Assert.assertEquals(sudokuGrid.getFieldValueString(0,2),"")
    Assert.assertEquals(sudokuGrid.getFieldValueString(1,0),"")
    Assert.assertEquals(sudokuGrid.getFieldValueString(1,1),"")
    Assert.assertEquals(sudokuGrid.getFieldValueString(1,2),"3")
    Assert.assertEquals(sudokuGrid.getFieldValueString(2,0),"9")
    Assert.assertEquals(sudokuGrid.getFieldValueString(2,1),"")
    Assert.assertEquals(sudokuGrid.getFieldValueString(2,2),"")

  }



}
