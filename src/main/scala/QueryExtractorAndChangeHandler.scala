import java.io.PrintWriter
import java.util.Calendar
import java.util.logging.Logger

import sun.rmi.log.ReliableLog.LogFile

import scala.io.{BufferedSource, Source}

object QueryExtractorAndChangeHandler {
  private val logger=Logger.getLogger(QueryExtractorAndChangeHandler.getClass.getName)
  //extract ddl Queries
  def extractQuery(reader:BufferedSource,writer:PrintWriter,logFile: LogHandler,changeLogFile:LogHandler,likeLogFile:LogHandler,ctasLogHandler:LogHandler,filePath:String): Unit ={
    //to create exception list of all table name which later on were changed to transactional
    ExceptionList.putToExceptionList(reader)
    var queryCompletionInProgress=false
    var query=""
    val reader1=Source.fromFile(filePath)
    for (line <- reader1.getLines) {
      if(line.startsWith("//") || line.startsWith("--")){//if line is comment write as it is
        writer.println(line)
      }
      else if(line.toLowerCase.matches("""(\s)*.*hive.execution.engine *= *mr.*""")){
        writer.println("--Hashmap : property hive.execution.engine=mr changed to hive.execution.engine=tez, as there is no map reduce engine in CDP 7 "+Calendar.getInstance().getTime)
        writer.println(line.replaceAll("""hive.execution.engine *= *mr""","hive.execution.engine = tez"))
        changeLogFile.log(Calendar.getInstance().getTime+" , "+filePath+" , "+line+" changed to "+line.replaceAll("""hive.execution.engine *= *mr""","hive.execution.engine = tez"))

      }
      else if(line.toLowerCase.matches("(\t| *)create  *table.*") || queryCompletionInProgress==true) {
        if(line.matches(""".*[^\\];.*""")){
          query+="\n"+line
          queryCompletionInProgress=false
          writeAndChangeQuery(query.trim,writer,logFile,likeLogFile,ctasLogHandler,filePath)
          query=""
        }
        else{
          query+="\n"+line
          queryCompletionInProgress=true
        }
      }
      else{
        writer.println(line)
      }
    }

    reader1.close

  }
  private def writeAndChangeQuery(query:String,writer: PrintWriter,logFile: LogHandler,likeLogFile:LogHandler,ctasLogFile:LogHandler,filePath:String)={

    val tableName=TableNameExtractor.getTableName(query,1)
    val category:Int=CategoryFinder.findQueryCategory(query,tableName)
    val newQueryWithComments:String=QueryModifier.getModifiedQuery(query,category,filePath,likeLogFile,ctasLogFile)
    writer.println(newQueryWithComments)
    if(category==2) {
      logger.info(Calendar.getInstance().getTime + " , " + filePath + " , " + tableName + " , " + category)
    }
    else {
      logFile.log(Calendar.getInstance().getTime + " , " + filePath + " , " + tableName + " , " + category)
    }
  }
}
