#!/bin/sh

java_home=$1
artifact_id=$2
version=$3

#alias mvn='/opt/data/dev/tools/maven/bin/mvn'
#mvn clean
#mvn package

proguard_jar_path='/opt/data/dev/tools/proguard/proguard-base-5.2.1.jar'
webapp_path='target/'$artifact_id'-'$version
webapp_classes_path=$webapp_path'/WEB-INF/classes'
webapp_lib_path=$webapp_path'/WEB-INF/lib'
obf_in_path='target/_obfIn'
obf_out_path='target/_obfOut'


# move jars to obf in
mkdir -p $obf_in_path
mkdir -p $obf_out_path
find $webapp_lib_path -type f -name 'mozaik-*.jar' -exec mv {} $obf_in_path \;

java -jar $proguard_jar_path \
	-injars $webapp_classes_path \
    -injars $obf_in_path/mozaik-bknd-api-local-$version.jar'(!**/compiler/**)' \
    -injars $obf_in_path/mozaik-frnd-plus-$version.jar \
    -outjars $obf_out_path/$artifact_id.jar'(**.class)' \
    -libraryjars $java_home/lib/rt.jar \
    -libraryjars $webapp_lib_path \
    -keepattributes *Annotation* \
    -keepattributes Signature \
    -keep public class top.mozaik.**

: <<'comment'
java -jar $proguard_jar_path \
    -injars $obf_in_path/mozaik-frnd-api-$version.jar \
    -outjars $obf_out_path/mozaik-frnd-api.jar \
    -libraryjars $java_home/lib/rt.jar \
    -libraryjars $webapp_lib_path \
    -keep class top.mozaik.**

java -jar $proguard_jar_path \
    -injars $obf_in_path/mozaik-frnd-common-$version.jar \
    -outjars $obf_out_path/mozaik-frnd-common.jar \
    -libraryjars $java_home/lib/rt.jar \
    -libraryjars $obf_in_path \
    -libraryjars $webapp_lib_path \
    -keep class top.mozaik.**
comment

# clear classes
rm -rf $webapp_classes_path/*

# copy obf out to webapp lib
find $obf_out_path -type f -name 'mozaik-*.jar' -exec mv {} $webapp_lib_path \;

# pack to zip
cd $webapp_path
zip -r9 ../$artifact_id-$version.obf.war *
