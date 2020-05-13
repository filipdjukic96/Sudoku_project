val arr = Array(1,2,3,4,5,6,5,0,0,0,0)

val filteredArray = arr.filter(x => x != 0)

filteredArray.distinct.length == filteredArray.length