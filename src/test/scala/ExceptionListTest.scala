///**
// * Copyright © 2020 Hashmap, Inc
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//import java.io.ByteArrayInputStream
//
//import org.scalatest.funsuite.AnyFunSuite
//
//import scala.io.{BufferedSource, Source}
//class ExceptionListTest extends AnyFunSuite {
//  test("ExceptionList.putToExceptionList"){
//    val queriesString="\ncreate table hub_dim(id int, work String); \ncreate table hub_dim_incident_worklog like hub_service_incident.hub_dim_incident_workload;\nalter table hub_dim_incident_worklog set tblproperties('transactional'='true');\n\n\ncreate table hub_dim_incident_assignment like hub_service_incident.hub_dim_incident_assignment ;\nalter table hub_dim_incident_assignment set tblproperties('transactional'='true');\n\n\ncreate table hub_dim_incident like hub_service_incident.hub_dim_incident_assignment ;\nalter table hub_dim_incident set tblproperties('transactional'='true');"
//    val reader:BufferedSource=new BufferedSource(new ByteArrayInputStream(queriesString.getBytes()))
//
//    ExceptionList.putToExceptionList(reader)
//    assertResult(true)(ExceptionList.isPresentInExceptionList("`hub_dim_incident_worklog`"))
//    assertResult(true)(ExceptionList.isPresentInExceptionList("`hub_dim_incident_assignment`"))
//    assertResult(true)(ExceptionList.isPresentInExceptionList("`hub_dim_incident`"))
//    assertResult(false)(ExceptionList.isPresentInExceptionList("`hub_dim`"))
//
//  }
//
//}
