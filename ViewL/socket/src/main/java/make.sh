
#SHELL_FOLDER=$(cd "$(dirname "$0")";pwd)
#echo $SHELL_FOLDER
#/home/liqing/work/tool/protoc/bin/protoc --java_out=$SHELL_FOLDER/model/ $SHELL_FOLDER/proto/model.proto

javac -d bin/ -cp ../../../libs/protobuf-java-3.5.1.jar @sourcelist.txt