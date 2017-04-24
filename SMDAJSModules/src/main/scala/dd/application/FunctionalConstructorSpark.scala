package dd.application

import java.nio.file.Paths

import dd.classification.LeastSquaresSignificanceSearcher
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation
import org.apache.spark.rdd.RDD

/**
  * Created by Sergey on 22.04.2017.
  */
object FunctionalConstructorSpark {

  import org.apache.spark.sql.SparkSession
  //import spark.implicits._

  val spark: SparkSession =
    SparkSession
      .builder()
      .appName("Functional Constructor")
      .config("spark.master", "local")
      .getOrCreate()


  val analysisKeyColumns = List("RegNum", "LabNum", "Date")
  val operationKeyColumns = List("RegNum", "OperationDate")

  /** Main function */
  def main(args: Array[String]): Unit = {

    calculate
  }

  def calculate = {
    import java.text.SimpleDateFormat
    import java.util.Locale


    val columns = spark.sparkContext.textFile(fsPath("/analysis/ABH_Columns")).collect().toList
    val analysisLines = spark.sparkContext.textFile(fsPath("/analysis/ABH.csv")).cache()
    val operationLines = spark.sparkContext.textFile(fsPath("/analysis/OperationReportReduced.csv")).cache()

    val analysisHeaders = analysisLines.first().split(",")

    val values =
      constructData(analysisLines, columns, analysisHeaders)
        .mapValues(_.map(x => if (x == "") null else x.toDouble))
        .cache()

    val notNullValues =
      values.filter(!_._2.contains(null))
        .mapValues(_.map(x => x.asInstanceOf[Double]))

    val format2 = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.ENGLISH)
    val analysisMap = constructData(analysisLines, analysisKeyColumns, analysisHeaders)
      .mapValues(x => (x(0), format2.parse(x(1)).getTime)).groupByKey().cache()

    val operationHeaders = operationLines.first().split(",")
    val format1 = new SimpleDateFormat("dd.MM.yy", Locale.ENGLISH)

    val operationMap =
      constructData(operationLines, operationKeyColumns, operationHeaders)
        .mapValues(x => format1.parse(x(0)).getTime).groupByKey().cache()

    val joined = operationMap.join(analysisMap).cache()

    val pairs = joined
      .flatMap(x => x._2._1.map(y => (y, x._2._2)))
      .flatMap(x => x._2.groupBy(y => Utils.classify(x._1, y._2)))
      .filter(_._1 != 999)
      .map(x => (x._1, x._2.map(_._1)))
      .groupByKey()
      .mapValues(_.flatten)
      .flatMap(x => x._2.map((_, x._1)))
      .cache()

    pairs
      .join(notNullValues)
      .map(_._2)
      .groupByKey()
      .map(x => ((x._1, x._2.size), x._2))
      .mapValues(lists2Array(_))
      .mapValues(correlation(_))
      .mapValues(functional(_))
      .sortBy(_._1)
      .foreach(println(_))
  }

  def constructData(lines: RDD[String], columns: List[String], headers: Array[String]): RDD[(String, List[String])] = {
    val indexes = columns.map(headers.indexOf(_))
    lines
      .mapPartitionsWithIndex((i, it) => if (i == 0) it.drop(1) else it)
      .map(_.split(",", -1).to[List])
      .map(x => x.zipWithIndex.filter(y => indexes.contains(y._2)))
      .map(_.map(_._1))
      .map(x => (x.head, x.tail))
  }


  def functional(data: Array[Array[Double]]): Double = {
    val searcher = new LeastSquaresSignificanceSearcher
    searcher.searchSignificance(0.0, data).getClassificationResult.getFunctional
  }

  def correlation(data: Array[Array[Double]]): Array[Array[Double]] = {
    new PearsonsCorrelation().computeCorrelationMatrix(data).getData
  }

  def lists2Array(lists: Iterable[List[Double]]): Array[Array[Double]] = {
    lists.map(_.toArray).toArray
  }

  /** @return The filesystem path of the given resource */
  def fsPath(resource: String): String =
    Paths.get(getClass.getResource(resource).toURI).toString

}
