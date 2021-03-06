/**
 * @author xubo
 *         more code:https://github.com/xubo245/SparkLearning
 *         more blog:http://blog.csdn.net/xubo245
 */
package com.github.xubo245.gcdss.disease

import java.text.SimpleDateFormat
import java.util.Date

import com.github.xubo245.gcdss.utils.Constants
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.bdgenomics.adam.models.SequenceDictionary
import org.bdgenomics.adam.rdd.variant.{VariantContextRDD, GenotypeRDD}
import org.bdgenomics.adam.rdd.ADAMContext._
/**
 * Created by xubo on 2016/5/23
 * 作用：根据vcf，参考DataProcesing处理出来的数据，得到vcf的omim信息.
 */

class CallDisease(variantContextRDD: VariantContextRDD) {
 
  def compute(): RDD[((String, String, String, String), Float)] = {
    //    val conf = new SparkConf().setAppName(this.getClass().getSimpleName().filter(!_.equals('$'))).setMaster("local[4]")
    //    val conf = new SparkConf().setAppName(this.getClass().getSimpleName().filter(!_.equals('$')))
    //    val sc = new SparkContext(conf)
    if (  Constants.debug  ){
      println("start compute:")
    }
    //    println(vcfArr.length)
    if ( Constants.debug  ){
      println("callDisease")
    }
    //    vcfRDD.map(_.variant.variant).foreach(println)
    val vcfArrRDD = variantContextRDD.rdd.map(_.variant.variant).map { each =>
//      ((each.getContigName, each.getStart.toString, each.getReferenceAllele, each.getAlternateAllele), each.getAnnotation.getAlleleFrequency.toFloat)
      var floatNum:Float=0
      if(each.getAnnotation!=null){
        floatNum=each.getAnnotation.getAlleleFrequency.toFloat
      }
      ((each.getContigName, each.getStart.toString, each.getReferenceAllele, each.getAlternateAllele), floatNum)
    }.map { each =>
      /**
       * 训练得出的数据库染色体是以数字表示的，没有chr开头
       */
      if (each._1._1.startsWith("chr")) {
        (((each._1._1.split("r"))(1).toString, each._1._2, each._1._3, each._1._4), each._2)
      } else {
        each
      }
    }

    if ( Constants.debug ){
      println("vcfArrRDD:")
    }
    //    vcfArrRDD.foreach(println)
    if ( Constants.debug ){
      println("compute end")
      vcfArrRDD.take(10).foreach(println)
    }
    vcfArrRDD
  }

  def runSimple(sc: SparkContext, file: String): RDD[(String, String, String, String)] = {
    val vcfRDD = compute()
    if ( Constants.debug ){
      println("start runSimple")
      println("vcfRDD:" + vcfRDD.count())
    }
    val callDiseaseRDD = loadDataProcessing.simple(sc, file)
    if (vcfRDD.count() < 1) {
      return null
    } else {
      if ( Constants.debug ) {
        println("vcfRDD:")
        vcfRDD.take(10).foreach(println)
      }
      val vcfRDDJoin = vcfRDD.map { each =>
        ((each._1._1, ((each._1._2).toInt + 1).toString(), each._1._3, each._1._4), each._2)
      }
        .map { each =>
          if (each._2 == null) {
            (each._1.toString(), (0.toString))
          } else {
            (each._1.toString(), (each._2.toString))
          }
        }
      val callDiseaseRDDJoin = callDiseaseRDD.map { each =>
        (each._1.toString(), (each._2, each._3))
      }
      if ( Constants.debug ) {
        println("vcfRDDJoin:" + vcfRDDJoin.count())
        println("callDiseaseRDDJoin:" + vcfRDDJoin.count())
      }
      //      vcfRDDJoin.foreach(println)
      //      callDiseaseRDDJoin.foreach(println)

      val joinRDD = vcfRDDJoin.join(callDiseaseRDDJoin)
      val outputRDD = joinRDD.map { each =>
        (each._1, each._2._1, each._2._2._1, each._2._2._2)
      }
      outputRDD
    }
  }

  def runComplex(sc: SparkContext, file: String): RDD[(String, String, String, String, String, String, String, String, String, String, String, String, String, String, String)] = {
    val vcfRDD = compute()
    if ( Constants.debug ) {
      println("start runComplex")
      println("vcfRDD:" + vcfRDD.count())
    }
    val callDiseaseRDD = loadDataProcessing.complex(sc, file)
    if (vcfRDD.count() < 1) {
      return null
    } else {
      if ( Constants.debug ) {
        println("vcfRDD:")
        vcfRDD.take(10).foreach(println)
      }
      val vcfRDDJoin = vcfRDD.map { each =>
        ((each._1._1, ((each._1._2).toInt + 1).toString(), each._1._3, each._1._4), each._2)
      }
        .map { each =>
          if (each._2 == null) {
            (each._1.toString(), (0.toString))
          } else {
            (each._1.toString(), (each._2.toString))
          }
        }
      val callDiseaseRDDJoin = callDiseaseRDD.map { each =>
        (each._1.toString(), (each._2._1, each._2._2, each._2._3, each._2._4, each._2._5, each._2._6, each._2._7, each._2._8, each._2._9, each._2._10, each._2._11, each._2._12, each._2._13))
      }
      if ( Constants.debug ) {
        println("vcfRDDJoin:" + vcfRDDJoin.count())
        println("callDiseaseRDDJoin:" + vcfRDDJoin.count())
      }
      //      vcfRDDJoin.foreach(println)
      //      CallDiseaseRDDJoin.foreach(println)

      val joinRDD = vcfRDDJoin.join(callDiseaseRDDJoin)
      val outputRDD = joinRDD.map { each =>
        (each._1, each._2._1, each._2._2._1, each._2._2._2, each._2._2._3, each._2._2._4, each._2._2._5, each._2._2._6, each._2._2._7, each._2._2._8, each._2._2._9, each._2._2._10, each._2._2._11, each._2._2._12, each._2._2._13)
      }
      outputRDD
    }
  }

}


object CallDisease {

  def main(args: Array[String]) {
    if (args.length < 3) {
      System.err.println("at least three argument required, e.g.1. vcfFile 2.CallDiseaseSimple 3.output")
      System.exit(1)
    }
    val conf = new SparkConf().setAppName(this.getClass().getSimpleName().filter(!_.equals('$')))
    val sc = new SparkContext(conf)
    //    var vcfFile = "D:/all/idea/GCDSS/file/callDisease/input/small.vcf"
    //    var output = "D:/all/idea/GCDSS/file/callDisease/output/CallDisease/test1"
    //    val CallDiseaseSimpleFile = "D:/all/idea/GCDSS/file/callDisease/input/CallDiseaseSimple.txt"
    var vcfFile = args(0)
    val CallDiseaseSimpleFile = args(1)
    val sd: Option[SequenceDictionary] = None
    val rdd = sc.loadGenotypes(vcfFile).toVariantContextRDD
    //    rdd.map(_.variant.variant).foreach(println)

    //        val vcfRDD = sc.loadGenotypes(vcfFile).toVariantContext.collect.sortBy(_.position)
    //        println("vcfRDD.head:")
    //        println(vcfRDD.head.genotypes.size)
    if ( Constants.debug ) {
      println("start call CallDisease")
    }
    val CallDisease = new CallDisease(rdd)
    val returnRDD = CallDisease.runSimple(sc, CallDiseaseSimpleFile)
    if ( Constants.debug ) {
      println("returnRDD.count:" + returnRDD.count())
      returnRDD.foreach(println)
    }
    val iString = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())
    val output = args(2) + "/simpleT" + iString
    if ( Constants.debug ) {
      println("output:" + output)
    }

    /**
      * add by xubo 20160611
      * 主要是由于存储的是逗号，数据里面原先可能有逗号，所以需要区分，改为‘|’为分隔符
      */
    val saveRDD = returnRDD.map { each =>
      val str1 = each._1.split(Array(',', '(', ')'))
      val str = str1(1) + '|' + str1(2) + '|' + str1(3) + '|' + str1(4) + '|' + each._2 + '|' + each._3 + '|' + each._4
      str
    }
    //    returnRDD.repartition(1).saveAsTextFile(output)
    saveRDD.repartition(1).saveAsTextFile(output)

    /** ***********end ***************/
    sc.stop()
  }
}

object CallDiseaseComplex {
  def main(args: Array[String]) {
    if (args.length < 3) {
      System.err.println("at least three argument required, e.g.1. vcfFile 2.CallDisease 3.output")
      System.exit(1)
    }
    val conf = new SparkConf().setAppName(this.getClass().getSimpleName().filter(!_.equals('$')))
    val sc = new SparkContext(conf)
    //    var vcfFile = "D:/all/idea/GCDSS/file/callDisease/input/small.vcf"
    //    var output = "D:/all/idea/GCDSS/file/callDisease/output/CallDisease/test1"
    //    val CallDiseaseSimpleFile = "D:/all/idea/GCDSS/file/callDisease/input/CallDiseaseSimple.txt"
    var vcfFile = args(0)
    val CallDiseaseFile = args(1)
    val sd: Option[SequenceDictionary] = None
    val rdd = sc.loadGenotypes(vcfFile).toVariantContextRDD
    //    rdd.map(_.variant.variant).foreach(println)

    //        val vcfRDD = sc.loadGenotypes(vcfFile).toVariantContext.collect.sortBy(_.position)
    //        println("vcfRDD.head:")
    //        println(vcfRDD.head.genotypes.size)
    if ( Constants.debug ) {
      println("start call CallDisease")
    }
    val CallDisease = new CallDisease(rdd)
    val returnRDD = CallDisease.runComplex(sc, CallDiseaseFile)
    if ( Constants.debug ) {
      println("returnRDD.count:" + returnRDD.count())
      returnRDD.foreach(println)
    }
    val iString = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())
    val output = args(2) + "/complexT" + iString
    if ( Constants.debug ) {
      println("output:" + output)
    }
    //    returnRDD.repartition(1).saveAsTextFile(output)

    /**
      * add by xubo 20160611
      * 主要是由于存储的是逗号，数据里面原先可能有逗号，所以需要区分，改为‘|’为分隔符
      */
    val saveRDD = returnRDD.map { each =>
      val str1 = each._1.split(Array(',', '(', ')'))
      val str = str1(1) + '|' + str1(2) + '|' + str1(3) + '|' + str1(4) + '|' + each._2 + '|' +
        each._3 + '|' + each._4 + '|' + each._5 + '|' + each._6 + '|' + each._7 + '|' +
        each._8 + '|' + each._9 + '|' + each._10 + '|' + each._11 + '|' + each._12 + '|' +
        each._13 + '|' + each._14 + '|' + each._15
      str
    }
    //    returnRDD.repartition(1).saveAsTextFile(output)
    saveRDD.repartition(1).saveAsTextFile(output)

    /** ***********end ***************/
    sc.stop()
  }
}







