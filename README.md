# Hashmap Hive Migrator
Scala application built as a maven project, that does the in-place changes to a directory containing HDP hive script files to make them compatible to migrate to the CDP environment. Also it will generate log files for tracking all the changes made to files for different kinds of queries, inline comments are also added for tracking changes.

## Prerequisites:
1. You should have java installed on system.
2. Create jar from project.
3. Go to command line and run following command:
```
java -jar <jarfile_path> <arg0_as_repoPath_in_which_you_want_in_place_changes> <arg1_as_logDirPath_ where_you_want_to_save_all_your_log_files_to_track_changes> <arg2> <arg3> … <argN> for mentioning all cluster names present in hive script files which needs to replaced with Oozie variable
```
## Output:
After running above command you will get inplace changes in your directory as well as four log files as following:

+ log.txt = (timestamp, filepath, tablename, category) for modified DDLs.

+ ctas_log.txt  = (timestamp, filepath, category, old DDL, modified DDL) for modified ctas.

+ like_log.txt =  (timestamp, filepath, category, old DDL, modified DDL) for modified like.

+ mr_log.txt =  (timestamp, filepath,changed property) for mr to tez property change.

+ location_log = (timestamp, filepath, tablename, category) for location clause deletion from managed transactional table queries.



Note: For running source code AutoScriptModifier is the main object.

