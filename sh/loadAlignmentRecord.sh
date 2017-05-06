spark-submit --class com.github.xubo245.gcdss.load.LoadAlignmentRecord \
 --master spark://219.219.220.149:7077 \
 --conf "spark.executor.extraJavaOptions=-Djava.library.path=/home/hadoop/disk2/xubo/lib/" \
  --jars /home/hadoop/cloud/adam/lib/adam_2.10-0.21.1-SNAPSHOT.jar \
  --executor-memory 20G \
GCDSS.jar \
/xubo/project/alignment/sparkBWA/input/ERR000589.adam
#/xubo/project/alignment/sparkBWA/input/SRR062634.adam

#/xubo/callVariant/vcf/All_20160407.vcf \
#/xubo/callVariant/vcf/All_20160407L10000.vcf \
