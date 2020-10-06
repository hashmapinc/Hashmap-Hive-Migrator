import java.util.logging.Logger

import scala.io.BufferedSource

object ExceptionList {

  private var transactionalTableNames:List[String]=List.empty[String]
  private val logger=Logger.getLogger(ExceptionList.getClass.getName)

  def putToExceptionList(reader:BufferedSource)={
    transactionalTableNames=List.empty[String]
    var alterQueryCompletionInProgress=false
    var query=""


    for(line<-reader.getLines()){
      if(line.toLowerCase.matches("(\t| *)alter  *table.*") ||alterQueryCompletionInProgress==true ) {
        if(line.contains(";")){
          query+="\n"+line
          alterQueryCompletionInProgress=false
          if(query.toLowerCase.replaceAll("""\s""","").matches(""".*("|')transactional("|')=("|')true("|').*""")){
            transactionalTableNames=transactionalTableNames:+TableNameExtractor.getTableName(query.trim,2)
          }
          query=""
        }
        else{
          query+="\n"+line
          alterQueryCompletionInProgress=true
        }
      }
    }
    logger.info("exception table list : "+transactionalTableNames)
  }

  def isPresentInExceptionList(tableName:String):Boolean={
    transactionalTableNames.contains(tableName)
  }

}
