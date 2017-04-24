package dd.application

/**
  * Created by Sergey on 24.04.2017.
  */
object Utils {

  def classify(operationDate: Long, analysisDate: Long): Long = {
    val diff = ((analysisDate - operationDate) / 8.64e+7)
    if (diff < 0) {
      if (diff >= -3) {
        -3
      } else if (diff >= -7) {
        -7
      }
      else if (diff >= -14) {
        -14
      } else {
        999
      }
    } else {
      if (diff <= 7) {
        diff.asInstanceOf[Long]
      } else if (diff <= 9) {
        9
      } else if (diff <= 14) {
        14
      } else {
        999
      }
    }
  }
}
