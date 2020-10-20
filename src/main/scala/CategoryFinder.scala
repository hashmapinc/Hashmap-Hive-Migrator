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
object CategoryFinder {

  def findQueryCategory(query:String,tableName:String)={

    if(query.toLowerCase.replaceAll("""\s""","").matches(""".*("|')transactional("|')=("|')true("|').*""") || ExceptionList.isPresentInExceptionList(tableName)){
      2
    }
    else if(query.toLowerCase().replaceAll("""\n|\t""","").matches(""".*[^`]stored by[^`].*""")){

      4
    }
    else{
      3
    }
  }


}
