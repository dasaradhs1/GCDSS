for num in 2000000 10000000 20000000
do
for i in {1..10}
do
fq='/xubo/project/alignment/CloudBWA/g38/time/cloudBWAnewg38L50c'$num'Nhs20Paired12time10num16k1.adam'
out='alluxio://Master:19998/xubo/project/alignment/CloudBWA/g38/time/cloudBWAnewg38L50c'$num'Nhs20Paired12time10num16k1DiscoverVariantI'$i'.adam'

#hadoop fs -rm -R -f $out
~/cloud/alluxio-1.3.0/bin/alluxio fs rm -R $out
sh testDiscoverVariant.sh $fq $out
~/cloud/alluxio-1.3.0/bin/alluxio fs rm -R $out
done
done
