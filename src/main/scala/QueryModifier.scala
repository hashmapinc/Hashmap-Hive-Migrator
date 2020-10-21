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
import java.util.Calendar

object QueryModifier {

  def getModifiedQuery(query:String,category:Int,filePath:String,likeLogFile:LogHandler,ctasLogFile:LogHandler): String ={
    var newQuery:String=""
    val ctasFound:Boolean=query.toLowerCase().replaceAll("""\s"""," ").matches(""".* as select .*""")
    if(category==3){
      val tableIndex=query.toLowerCase.indexOf("table")
      var tblpropertyindex:Int= -1
      var suffix=""
      if(query.toLowerCase().contains("tblproperties")){
        tblpropertyindex=query.toLowerCase().indexOf("tblproperties")
        val in=query.toLowerCase().indexOf("(",tblpropertyindex)+1
        suffix=" TBLPROPERTIES ('external.table.purge'='true', "+query.substring(in)
      }
      else if(query.toLowerCase().replaceAll("""\s"""," ").matches(""".* as select .*""")) {
        tblpropertyindex=query.toLowerCase().replaceAll("""as\sselect""","as select").indexOf("as select")
        suffix=" \nTBLPROPERTIES ('external.table.purge'='true') "+query.substring(tblpropertyindex)
      }
      else{
        tblpropertyindex=query.lastIndexOf(";")
        suffix=" \nTBLPROPERTIES ('external.table.purge'='true');"
      }
      newQuery+="-- Hashmap : Line below changed (added EXTERNAL) at "+Calendar.getInstance().getTime+"\n"
      newQuery+=query.substring(0,tableIndex)+"EXTERNAL "+query.substring(tableIndex,tblpropertyindex)+suffix
      newQuery+="\n-- Hashmap : Line above changed  (added TBLPROPERTIES ('external.table.purge'='true')) at "+Calendar.getInstance().getTime+"\n"
      if (query.toLowerCase().replaceAll("""\s"""," ").matches(""".* like .*""")){
        likeLogFile.log(Calendar.getInstance().getTime+" , "+filePath+" , "+category+" , "+query+" , "+newQuery)
      }
      else if(ctasFound){
        ctasLogFile.log(Calendar.getInstance().getTime+" , "+filePath+" , "+category+" , "+query+" , "+newQuery)
      }
      newQuery
    }
    else if(category==4){
      val tableIndex=query.toLowerCase.indexOf("table")
      newQuery+="-- Hashmap : Line below changed (added EXTERNAL) at "+Calendar.getInstance().getTime+"\n"
      newQuery+=query.substring(0,tableIndex)+"EXTERNAL "+query.substring(tableIndex)
      if (query.toLowerCase().replaceAll("""\s"""," ").matches(""".* like .*""")){
        likeLogFile.log(Calendar.getInstance().getTime+" , "+filePath+" , "+category+" , "+query+" , "+newQuery)
      }
      else if(ctasFound){
        ctasLogFile.log(Calendar.getInstance().getTime+" , "+filePath+" , "+category+" , "+query+" , "+newQuery)
      }
      newQuery
    }
    else{
      if(query.replaceAll("""\s""","").matches(""".*(?i)LOCATION\s*('.*?'|".*?")\s*.*""")){
        "--Hashmap : Location clause removed from query mentioned below "+Calendar.getInstance().getTime+"\n"+query.replaceAll("""(?i)LOCATION\s*('.*?'|".*?")\s*""","")
      }
      else{
        query.replaceAll("""(?i)LOCATION\s*('.*?'|".*?")\s*""","")
      }
      //query
    }
  }


}
