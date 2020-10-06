object TableNameExtractor {

  def getTableName(query:String,category:Int):String={

    category match {
      case 1=>getTableNameFromCreateDDL(query)
      case 2=>getTableNameFromAlterDDL(query)
    }
  }

  private def getTableNameFromCreateDDL(query:String)={
    var index:Int= -1
    val words:List[String]=query.split("\n|\t|  *").toList
    if(words.length>5 && ((words(0)+" "+words(1)+" "+words(2)+" "+words(3)+" "+words(4)).toLowerCase=="create table if not exists")){
      filterTableName(words(5))
    }
    else{
      filterTableName(words(2))
    }

  }

  private def getTableNameFromAlterDDL(query:String)={
    val words=query.split("\n|\t|  *").toList
    filterTableName(words(2)) //alter table <table_name>
  }

  private def filterTableName(tableName:String):String={
    val arr:List[String]=tableName.split("\\(").toList
    var tbName=""
    if(arr(0).contains(".")){
      tbName=arr(0).split("\\.").toList(1)
    }
    else{
      tbName=arr(0)
    }
    tbName=tbName.replace("`","")
    tbName="`"+tbName+"`"
    tbName
  }

}
