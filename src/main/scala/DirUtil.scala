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
