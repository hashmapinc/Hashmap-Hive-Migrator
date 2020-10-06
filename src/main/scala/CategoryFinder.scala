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
