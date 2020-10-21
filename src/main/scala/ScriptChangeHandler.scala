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
import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths, StandardCopyOption}
import java.util.logging.Logger

import scala.io.Source

object ScriptChangeHandler {

  private val logger=Logger.getLogger(ScriptChangeHandler.getClass.getName)

  private def moveAndRenameFile(source: String, destination: String): String= {
    val path = Files.move(
      Paths.get(source),
      Paths.get(destination),
      StandardCopyOption.REPLACE_EXISTING)
    path.toString
  }

  private def putQueriesToNewLine(filePath: String)={
    val originalFile=new File(filePath)
    val tempFile=new File("temp1.txt")
    val writer = new PrintWriter(tempFile)
    val reader=Source.fromFile(filePath)
    for(line<-reader.getLines()){
      if(line.matches(""".*[^\\];[^"]*""")){
        val lineparts=line.split("""[^\\];""").toList
        if(lineparts.size<=1){
          writer.println(line)
        }
        else{
          var i:Int=0
          var missingIndex=0
          while(i<lineparts.size-1){
            missingIndex+=lineparts(i).size
            writer.println(lineparts(i)+line.charAt(missingIndex)+";")
            missingIndex+=2
            i+=1
          }
          missingIndex+=lineparts(i).size
          if(missingIndex<line.size){
            writer.println(lineparts(lineparts.size-1)+line.charAt(missingIndex)+";")
          }
          else{
            writer.println(lineparts(lineparts.size-1))
          }
        }
      }
      else{
        writer.println(line)
      }
    }
    reader.close()
    writer.close()
    moveAndRenameFile(tempFile.getCanonicalPath,originalFile.getCanonicalPath)

  }



  def processFile(filePath:String, logFile:LogHandler,changeLogfile:LogHandler,likeLogFile:LogHandler,ctasLogFile:LogHandler,locationLogFile:LogHandler):Unit={

    logger.info("Received file at "+filePath+" for processing.")
    putQueriesToNewLine(filePath)

    val originalFile=new File(filePath)
    val tempFile=new File("temp.txt")
    val writer = new PrintWriter(tempFile)
    val reader=Source.fromFile(filePath)
    QueryExtractorAndChangeHandler.extractQuery(reader,writer,logFile,changeLogfile,likeLogFile,ctasLogFile,locationLogFile,filePath)
    reader.close()
    writer.close()
    val path=moveAndRenameFile(tempFile.getCanonicalPath,originalFile.getCanonicalPath)

    logger.info("Changed file moved to "+ path)

  }

}
