spark-submit --class com.github.xubo245.gcdss.adam.postProcessing.ReadPostProcessing \
 --master spark://219.219.220.149:7077 \
 --conf "spark.executor.extraJavaOptions=-Djava.library.path=/home/hadoop/disk2/xubo/lib/" \
  --jars /home/hadoop/cloud/adam/lib/adam_2.10-0.23.0-SNAPSHOT.jar \
  --executor-memory 20G \
  --total-executor-cores $1 \
GCDSS.jar \
$2 $3 $4 $5

 #--jars /home/hadoop/cloud/adam/lib/adam_2.10-0.21.1-SNAPSHOT.jar \
#/xubo/callVariant/vcf/All_20160407.vcf \
#/xubo/callVariant/vcf/All_20160407L10000.vcf \
