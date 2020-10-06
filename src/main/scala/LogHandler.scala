import java.util.logging.{FileHandler, Logger, SimpleFormatter}


class LogHandler(val logFilePath:String) {
  private val logger=Logger.getLogger(logFilePath)
  private var fh=init(logFilePath)

  private def init(logFilePath:String):FileHandler={
    fh=new FileHandler(logFilePath)
    fh.setFormatter(new SimpleFormatter())
    logger.addHandler(fh)
    fh
  }

  def log(line:String)={
    logger.info(line)
  }

}
