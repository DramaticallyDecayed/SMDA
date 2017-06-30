package dd.application

import java.nio.file.Paths

import dd.classification.{LeastSquaresSignificanceSearcher, SignificanceSearchResult}
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation
import org.apache.spark.rdd.RDD
import org.json4s.jackson.JsonMethods.{pretty, render}
import org.json4s.jackson.Serialization
import org.json4s.{Extraction, NoTypeHints}

/**
  * Created by Sergey on 22.04.2017.
  */
object FunctionalConstructorSpark {

  import org.apache.spark.sql.SparkSession

  //import spark.implicits._
  implicit val serializationFormats = {
    Serialization.formats(NoTypeHints)
  }

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


    val columns = spark.sparkContext.textFile(fsPath("/analysis/ABH_Vein_Columns")).collect().toList
    val analysisLines = spark.sparkContext.textFile(fsPath("/analysis/ABH.csv")).cache()
    val operationLines = spark.sparkContext.textFile(fsPath("/analysis/OperationReportReduced.csv")).cache()

    val analysisHeaders = analysisLines.first().split(",")

    val values =
      constructData(analysisLines, columns, analysisHeaders)
        .mapValues(_.map(x => if (x == "") null else x.toDouble))
        .cache()

    val interpretedValues = interpretValues(values, columns, analysisHeaders)

    val notNullValues =
      interpretedValues.filter(!_._2.contains(null))
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

    val result = pairs
      .join(notNullValues)
      .map(_._2)
      .groupByKey()
      .map(x => ((x._1, x._2.size), x._2))
      .mapValues(lists2Array(_))
      .mapValues(correlation(_))
      .mapValues(functional(_))
      .sortBy(_._1)
      .mapValues(prepareResult(_, columns))
      .map(x => structure2Result(x))
      .cache()

    val json = Extraction.decompose(result.collect().toList)
    println(pretty(render(json)))
    printAsPaths(result.collect().toList).foreach(x => println(x + ","))
  }

  def printAsPaths(result: List[Result]): List[String] = {
    val analysis = "analysis"
    analysis :: result.flatMap(_.printPaths()).map(x => (analysis + ".") ++ x)
  }

  def structure2Result(record: ((Long, Int), (Double, Double, Map[Int, Set[String]]))): Result = {
    new Result(
      record._1._1,
      record._1._2,
      BigDecimal(record._2._1).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble,
      BigDecimal(record._2._2).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble,
      record._2._3.size,
      map2List(record._2._3)
    )
  }

  def map2List(map: Map[Int, Set[String]]): List[Classes] = {
    map.map(x => new Classes(x._1 + 1, x._2.toList)).toList
  }

  def prepareResult(result: SignificanceSearchResult, columns: List[String]): (Double, Double, Map[Int, Set[String]]) = {
    import scala.collection.JavaConverters._
    val parameters = columns.tail
    val scalaMap = result.getClassificationResult.getResult.asScala.mapValues(_.asScala.toSet).toMap
    val stringedList = scalaMap.map(x => (x._1.toInt, x._2.map(parameters(_))))
    (result.getClassificationResult.getFunctional, result.getSignificance, stringedList)
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

  def interpretValues(values: RDD[(String, List[Any])], columns: List[String], headers: Array[String]): RDD[(String, List[Any])] = {
    val list = Map(
      ("Базофилы%" -> "WBC-Лейк-ты"),
      ("Эозинофил%" -> "Нейтрофилы%"),
      ("П/я нейтрофил%_ручн" -> "Нейтрофилы%"),
      ("С/я_нейтрофил%_ручн" -> "Нейтрофилы%")
    )
    val indexes = list.map(x => (columns.tail.indexOf(x._1) -> columns.tail.indexOf(x._2)))
    val newvals = values.mapValues(
      z => z.zipWithIndex.map(x => if (indexes.contains(x._2) && x._1 == null && z(indexes(x._2)) != null) 0.0 else x._1)
    )
    newvals
  }

  def functional(data: Array[Array[Double]]): SignificanceSearchResult = {
    val searcher = new LeastSquaresSignificanceSearcher
    searcher.searchSignificance(0.0, data) //.getClassificationResult.getFunctional
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


  case class Classes(num: Int, list: List[String]) {
    def printPaths(): List[String] = {
      (num.toString) :: list.sortBy(x => x).map(num + "." + _)
    }
  }

  case class Result(
                     days: Long,
                     size: Int,
                     functional: Double,
                     significance: Double,
                     numOfClasses: Int,
                     classes: List[Classes]
                   ) {

    def printPaths(): List[String] = {
      val s = classes.sortBy(_.num).flatMap(y => y.printPaths().map(x => days.toString + "." + x))
      (days.toString) :: s
    }
  }

}
