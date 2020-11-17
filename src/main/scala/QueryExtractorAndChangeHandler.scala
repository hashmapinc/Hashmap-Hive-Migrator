/**
 * Copyright © 2020 Hashmap, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.PrintWriter
import java.util.Calendar
import java.util.logging.Logger
import java.util.regex.Matcher

import sun.rmi.log.ReliableLog.LogFile

import scala.io.{BufferedSource, Source}

object QueryExtractorAndChangeHandler {
  private val logger=Logger.getLogger(QueryExtractorAndChangeHandler.getClass.getName)
  //extract ddl Queries
  def extractQuery(reader:BufferedSource,writer:PrintWriter,logFile: LogHandler,changeLogFile:LogHandler,likeLogFile:LogHandler,ctasLogHandler:LogHandler,locationLogFile:LogHandler,filePath:String,clusterNames:List[String]): Unit ={
    //to create exception list of all table name which later on were changed to transactional
    ExceptionList.putToExceptionList(reader)
    val reader1=Source.fromFile(filePath)
    ExceptionList.putToAlterDatabaseList(reader1)
    reader1.close()
    var queryCompletionInProgress=false
    var alterQueryCompletionInProgress=false
    var dbQueryCompletionInProgress=false
    var query=""
    val reader2=Source.fromFile(filePath)
    for (line <- reader2.getLines) {
      if(line.startsWith("//") || line.startsWith("--")){//if line is comment write as it is
        writer.println(line)
      }
      else if(line.toLowerCase.matches("""(\s)*.*hive.execution.engine *= *mr.*""")){
        writer.println("--Hashmap : property hive.execution.engine=mr changed to hive.execution.engine=tez, as there is no map reduce engine in CDP 7 "+Calendar.getInstance().getTime)
        writer.println(line.replaceAll("""hive.execution.engine *= *mr""","hive.execution.engine = tez"))
        changeLogFile.log(Calendar.getInstance().getTime+" , "+filePath+" , "+line+" changed to "+line.replaceAll("""hive.execution.engine *= *mr""","hive.execution.engine = tez"))

      }
      else if(line.toLowerCase.matches("(\t| *)create  *table.*") || queryCompletionInProgress==true) {

        if(line.matches(""".*[^\\]*;""")){
          query+="\n"+line
          queryCompletionInProgress=false
          writeAndChangeQuery(query.trim,writer,logFile,likeLogFile,ctasLogHandler,locationLogFile ,filePath)
          query=""
        }
        else{
          query+="\n"+line
          queryCompletionInProgress=true
        }
      }
      else if(line.toLowerCase.matches("(\t| *)alter  *table.*") ||alterQueryCompletionInProgress==true ) {
        if(line.contains(";")){
          query+="\n"+line
          alterQueryCompletionInProgress=false
          if(query.toLowerCase.replaceAll("""\s""","").matches(""".*("|')transactional("|')=("|')true("|').*""")){
            if(query.replaceAll("""\s""","").matches(""".*(?i)LOCATION\s*('.*?'|".*?")\s*.*""")){
              writer.println("--Hashmap : Location clause removed from alter query mentioned below "+Calendar.getInstance().getTime)
            }
            writer.println(query.replaceAll("""(?i)LOCATION\s*('.*?'|".*?")\s*""",""))
          }
          else{
            writer.println(query)
          }
          query=""
        }
        else{
          query+="\n"+line
          alterQueryCompletionInProgress=true
        }
      }
      else if(line.toLowerCase.matches("(\t| *)(create|alter) *database.*") ||dbQueryCompletionInProgress==true){
        if(line.contains(";")){
          query+="\n"+line
          dbQueryCompletionInProgress=false
          if(query.toLowerCase.replaceAll("""\s"""," ").matches(""".* location .*""") && !(ExceptionList.isPresentInAlteredDbList(TableNameExtractor.getTableName(query,3)))){
            var loc=""
            val words:Array[String]=query.split("""\s""")
            val locValIndex= words.map(word=>word.toLowerCase).toList.indexOf("location")+1
            val locValue=words(locValIndex).replaceAll("""("|'|;)""","").trim
            val dirs=locValue.split("""(/|//|///)""")

            if(dirs(0).toLowerCase=="hdfs:"){
              if(dirs.length>=3){
                if(clusterNames.contains(dirs(2))) {
                  loc=locValue.replaceAll(dirs(0)+"//"+dirs(2),Matcher.quoteReplacement("\\${hivevar:nameNode}"))
                } else{
                  loc=locValue.replaceAll("hdfs://",Matcher.quoteReplacement("\\${hivevar:nameNode}"))
                }
              }
              else{
                loc=locValue.replaceAll("hdfs://",Matcher.quoteReplacement("\\${hivevar:nameNode}"))
              }
            }
            else{
              loc="\\${hivevar:nameNode}"+locValue
            }

            writer.println("\n--Hashmap : Location value "+locValue+" is changed to "+loc.substring(1))
            writer.println(query.replaceAll("""(?i)LOCATION\s*('.*?'|".*?")\s*""","LOCATION '"+loc+"' ").trim)
            writer.println("--Hashmap : Alter statement is added to set managedlocation property for database")
            writer.println("ALTER DATABASE "+TableNameExtractor.getTableName(query,3)+" SET managedlocation '"+loc.substring(1)+"' ;")

          }
          else{
            writer.println(query)
          }
          query=""
        }
        else{
          query+="\n"+line
          dbQueryCompletionInProgress=true
        }
      }
      else{
        writer.println(line)
      }
    }

    reader2.close

  }

  private def writeAndChangeQuery(query:String,writer: PrintWriter,logFile: LogHandler,likeLogFile:LogHandler,ctasLogFile:LogHandler,locationLogFile:LogHandler,filePath:String)={

    val tableName=TableNameExtractor.getTableName(query,1)
    val category:Int=CategoryFinder.findQueryCategory(query,tableName)
    val newQueryWithComments:String=QueryModifier.getModifiedQuery(query,category,filePath,likeLogFile,ctasLogFile)
    writer.println(newQueryWithComments)
    if(category==2) {
      if(newQueryWithComments.contains("--Hashmap :")){
        locationLogFile.log(Calendar.getInstance().getTime + " , " + filePath + " , " + tableName + " , " + category)
      }
      else {
        logger.info(Calendar.getInstance().getTime + " , " + filePath + " , " + tableName + " , " + category)
      }
    }
    else {
      logFile.log(Calendar.getInstance().getTime + " , " + filePath + " , " + tableName + " , " + category)
    }
  }
}
