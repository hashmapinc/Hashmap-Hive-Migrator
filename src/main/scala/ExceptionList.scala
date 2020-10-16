/**
 * Modifications © 2020 Hashmap, Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
