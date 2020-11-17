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
import java.util.logging.Logger

object AutoScriptModifier{

  private val logger = Logger.getLogger(AutoScriptModifier.getClass.getName)

  def main(args: Array[String]): Unit = {

    val repoPath=args(0)
    val logDirPath=args(1)
    var i:Int=0
    var clusterNames:List[String]= List.empty
    for (i<-2 to args.length-1){


        clusterNames=clusterNames:+args(i)
    }
    val logFilePath=logDirPath+"\\log.txt"
    val propertyChangeLogFilePath=logDirPath+"\\mr_log.txt"
    val likeLogFilePath=logDirPath+"\\like_log.txt"
    val ctasLogFilePath=logDirPath+"\\ctas_log.txt"
    val locationLogFilePath=logDirPath+"\\location_log.txt"

    logger.info("Repo path: " + repoPath)
    val listOfAllFilePath = DirUtil.getFilePathsOfExtension(".hql", repoPath)
    logger.info("Directory contains " + listOfAllFilePath.length + " files with extension .hql\n")


    val logFile=new LogHandler(logFilePath)
    logger.info("log file set\n")
    val changeLogFile=new LogHandler(propertyChangeLogFilePath)
    logger.info(" change log file set\n")
    val likeLogFile=new LogHandler(likeLogFilePath)
    logger.info("like log file set\n")
    val ctasLogFile=new LogHandler(ctasLogFilePath)
    logger.info(" ctaslog file set\n")
    val locationLogFile=new LogHandler(locationLogFilePath)
    logger.info("location log file set")

    logger.info("Sending files for processing\n")
    listOfAllFilePath.map(filePath=>ScriptChangeHandler.processFile(filePath,logFile,changeLogFile,likeLogFile,ctasLogFile,locationLogFile,clusterNames))


  }

}
