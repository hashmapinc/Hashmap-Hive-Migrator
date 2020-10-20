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
import org.scalatest.funsuite.AnyFunSuite
class QueryModifierTest extends AnyFunSuite {
  test("QueryModifier.getModifiedQuery"){
    val filePath="temp.txt"
    val likeLogFilePath="like_log.txt"
    val ctasLogFilePath="ctas_log.txt"
    val likeLogFile=new LogHandler(likeLogFilePath)
    val ctasLogFile=new LogHandler(ctasLogFilePath)
    var query="create table if not exists HUB_DIM_SUPPLY\n(\nSUPPLY_TYPE_CODE string,\nedl_surr_supply_id string,\nSUPPLY_SOURCE_ID decimal(38,15)\n)\nclustered by (edl_surr_supply_id) INTO 23 BUCKETS\nstored as orc\nTBLPROPERTIES ('transactional'='true');"
    var category=2
    var result="create table if not exists HUB_DIM_SUPPLY\n(\nSUPPLY_TYPE_CODE string,\nedl_surr_supply_id string,\nSUPPLY_SOURCE_ID decimal(38,15)\n)\nclustered by (edl_surr_supply_id) INTO 23 BUCKETS\nstored as orc\nTBLPROPERTIES ('transactional'='true');"
    assertResult(result)(QueryModifier.getModifiedQuery(query,category,filePath,likeLogFile,ctasLogFile))


  }

}
