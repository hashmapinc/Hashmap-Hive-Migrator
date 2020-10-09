# AutoScriptModifier

1. You should have java installed on system.
2. Create jar from project.
3. Go to command line and run following command:

java -jar <jarfile_path> <arg0_as_repoPath> <arg1_as_logDirPath_ where_you _want_to_save_all_your_log_files>


## Output:
After running above command you will get inplace changes in your directory as well as four log files as following:

+ log.txt = (timestamp, filepath, tablename, category) for modified DDLs.

+ ctas_log.txt  = (timestamp, filepath, category, old DDL, modified DDL) for modified ctas.

+ like_log.txt =  (timestamp, filepath, category, old DDL, modified DDL) for modified like.

+ mr_log.txt =  (timestamp, filepath,changed property) for mr to tez property change.

+ location_log = (timestamp, filepath, tablename, category) for location clause deletion from managed transactional table queries.



Note: For running source code AutoScriptModifier is the main object.

