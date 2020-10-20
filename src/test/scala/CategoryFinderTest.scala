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

class CategoryFinderTest extends AnyFunSuite{
  test("CategoryFinder.findQueryCategory"){
    var query="CREATE TABLE hbase_table_1[\\\\;](key int, value string)\nSTORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'\nWITH SERDEPROPERTIES (\n\"hbase.columns.mapping\" = \"cf:string\",\n\"hbase.table.name\" = \"hbase_table_0\");"
    var tableName="`hbase_table_1`"
    assertResult(4)(CategoryFinder.findQueryCategory(query,tableName))
    query="CREATE TABLE ${hivevar:targetSchema}.`hub_bi`\n( `level1` string,\n`record`  string\n)\nPARTITIONED BY (`ingest_date` STRING,`ingest_time` STRING)\nROW FORMAT DELIMITED\nFIELDS TERMINATED BY '\\\\u001F'\nSTORED AS PARQUET\n;"
    tableName="`hub_bi`"
    assertResult(3)(CategoryFinder.findQueryCategory(query,tableName))
    query="CREATE TABLE `order`(\n   `id` bigint,\n   `source` string,\n   `number` decimal(38,18),\n   `code` int,\n   `etime` string)\n CLUSTERED BY (\n   header_id)\n INTO 32 BUCKETS\n ROW FORMAT SERDE\n   'org.apache.hadoop.hive.ql.io.orc.OrcSerde'\n STORED AS INPUTFORMAT\n   'org.apache.hadoop.hive.ql.io.orc.OrcInputFormat'\n OUTPUTFORMAT\n   'org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat'\n LOCATION\n   'hdfs://PRD01/lake/hub/order'\n TBLPROPERTIES (\n   'transactional'='true'\n   );"
    tableName="`order`"
    assertResult(2)(CategoryFinder.findQueryCategory(query,tableName))


  }
}
