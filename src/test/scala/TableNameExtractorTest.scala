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
class TableNameExtractorTest extends AnyFunSuite {
  test("TableNameExtractor.getTableName"){

    var query="create table hub_dim(id int, work String);"
    assertResult("`hub_dim`")(TableNameExtractor.getTableName(query,1))
    query="create table `hub_dim`(id int, work String);"
    assertResult("`hub_dim`")(TableNameExtractor.getTableName(query,1))
    query="create table db.`hub_dim`(id int, work String);"
    assertResult("`hub_dim`")(TableNameExtractor.getTableName(query,1))
    query="create table if not exists db.hub_dim(id int, work String);"
    assertResult("`hub_dim`")(TableNameExtractor.getTableName(query,1))

    query="Alter table test set tblproperties('transactional'='true');"
    assertResult("`test`")(TableNameExtractor.getTableName(query,2))
    query="Alter table db.test set tblproperties('transactional'='true');"
    assertResult("`test`")(TableNameExtractor.getTableName(query,2))
    query="Alter table `db`.`test` set tblproperties('transactional'='true');"
    assertResult("`test`")(TableNameExtractor.getTableName(query,2))




  }
}
