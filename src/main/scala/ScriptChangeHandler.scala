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
      if(line.matches(""".*[^\\];.*""")){
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



  def processFile(filePath:String, logFile:LogHandler,changeLogfile:LogHandler,likeLogFile:LogHandler,ctasLogFile:LogHandler):Unit={

    logger.info("Received file at "+filePath+" for processing.")
    putQueriesToNewLine(filePath)

    val originalFile=new File(filePath)
    val tempFile=new File("temp.txt")
    val writer = new PrintWriter(tempFile)
    val reader=Source.fromFile(filePath)
    QueryExtractorAndChangeHandler.extractQuery(reader,writer,logFile,changeLogfile,likeLogFile,ctasLogFile,filePath)
    reader.close()
    writer.close()
    val path=moveAndRenameFile(tempFile.getCanonicalPath,originalFile.getCanonicalPath)

    logger.info("Changed file moved to "+ path)

  }

}
