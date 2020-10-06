import java.io.{File, FileNotFoundException}
import java.util.logging.Logger

object DirUtil {
  private val logger = Logger.getLogger(DirUtil.getClass.getName)
  //search recursively for file paths for files having extension ".hql"
  @throws[Exception]
  def getFilePathsOfExtension(extension:String,directoryPath: String): List[String] = {
    val pattern=".*"+extension
    var filePaths:List[String]=List.empty
    val file = new File(directoryPath)
    if (file.exists && file.isDirectory) {
      filePaths=file.listFiles.filter(_.isFile).filter(_.getName.matches(pattern)).map(file => file.getAbsolutePath).toList
      filePaths=filePaths.:::(file.listFiles.filter(_.isDirectory).map(dir=>getFilePathsOfExtension(extension,dir.getAbsolutePath())).toList.flatten)
      filePaths
    } else if (file.exists && file.isFile && file.getName.matches(pattern)) {
      List(file.getAbsolutePath)
    } else {
      throw new FileNotFoundException("Directory path does not exist")
    }

  }

}
