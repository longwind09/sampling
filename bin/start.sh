#!/bin/bash

if [ $# -ne 3 ];then
    echo "exe     ./cfg    ./sample_in   ./sample_out"
    exit -1
fi


jarfile=../target/ml.sampling-0.0.1-SNAPSHOT-jar-with-dependencies.jar
java -cp $jarfile org.felix.ml.sampling.support.single.Process $@


