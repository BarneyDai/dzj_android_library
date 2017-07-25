#配合gradle构建工具,读取jenkins配置参数，写入Paem.properties文件中
#来自jenkins，还是本地，FROM_JENKINS=true
FROM_JENKINS=$1


#工程信息
PROJ_DIR=$2
JOB_NAME=$3


#jenkins工作空间目录
WORKSPACE=`pwd`
DIR_WORKSPACE=${WORKSPACE//\//\\\/}
echo ${DIR_WORKSPACE}
DIR_PROJECT="${DIR_WORKSPACE}\/tmp"

#============================ path =================================
DIR_PROJ_PATH=${WORKSPACE}/${PROJ_DIR}
DIR_PROJ_BAK_PATH=${WORKSPACE}/tmp
FILE_PAEM_PROTERTIES=${DIR_PROJ_BAK_PATH}/Paem.properties
#============================ function ===============================

#备份android工程代码
function backProj(){
echo "back project from ${DIR_PROJ_PATH} to ${DIR_PROJ_BAK_PATH}..."
rm -rf ${DIR_PROJ_BAK_PATH}
cp -rf ${DIR_PROJ_PATH} ${DIR_PROJ_BAK_PATH}
cd ${DIR_PROJ_BAK_PATH}
find . -type d -name ".svn" |xargs rm -rvf
echo "back project end."
}

#替换脚本文件
function copyScript(){
echo "copyScript from  ${DIR_WORKSPACE}/script to ${DIR_PROJ_BAK_PATH}..."
if [ ! -d ${DIR_PROJ_BAK_PATH} ]; then
echo ${DIR_PROJ_BAK_PATH} not exits
return
fi

#替换local.properties配有sdk
cp -rf /Users/pa_caifubao/Documents/Android/local.properties ${DIR_PROJ_BAK_PATH}

#拷贝专用构建gradle脚本
rm -rf ${DIR_PROJ_BAK_PATH}/build.gradle
rm -rf ${DIR_PROJ_BAK_PATH}/action.gradle
rm -rf ${DIR_PROJ_BAK_PATH}/settings.gradle
rm -rf ${DIR_PROJ_BAK_PATH}/Paem.properties
rm -rf ${DIR_PROJ_BAK_PATH}/output.gradle
rm -rf ${DIR_PROJ_BAK_PATH}/parsexml.gradle


rm -rf ${DIR_PROJ_BAK_PATH}/KeplerDemo/build.gradle
rm -rf ${DIR_PROJ_BAK_PATH}/ILoanLib/build.gradle
rm -rf ${DIR_PROJ_BAK_PATH}/Dependlibs/build.gradle

#插件
rm -rf ${DIR_PROJ_BAK_PATH}/ILoanPlugin/build.gradle

DIR_SCRIPT_FULL=${WORKSPACE}/${SCRIPT_DIR}
#拷贝专用构建gradle脚本
cp -rf ${DIR_SCRIPT_FULL}/project/build.gradle ${DIR_PROJ_BAK_PATH}/build.gradle
cp -rf ${DIR_SCRIPT_FULL}/project/action.gradle ${DIR_PROJ_BAK_PATH}/action.gradle
cp -rf ${DIR_SCRIPT_FULL}/project/settings.gradle ${DIR_PROJ_BAK_PATH}/settings.gradle
cp -rf ${DIR_SCRIPT_FULL}/project/Paem.properties ${DIR_PROJ_BAK_PATH}/Paem.properties
cp -rf ${DIR_SCRIPT_FULL}/project/output.gradle ${DIR_PROJ_BAK_PATH}/output.gradle
cp -rf ${DIR_SCRIPT_FULL}/project/parsexml.gradle ${DIR_PROJ_BAK_PATH}/parsexml.gradle

cp -rf ${DIR_SCRIPT_FULL}/module/KeplerDemo_build.gradle ${DIR_PROJ_BAK_PATH}/KeplerDemo/build.gradle
cp -rf ${DIR_SCRIPT_FULL}/module/ILoanLib_build.gradle ${DIR_PROJ_BAK_PATH}/ILoanLib/build.gradle
cp -rf ${DIR_SCRIPT_FULL}/module/Dependlibs_build.gradle ${DIR_PROJ_BAK_PATH}/Dependlibs/build.gradle

#插件
cp -rf ${DIR_SCRIPT_FULL}/module/ILoanPlugin_build.gradle ${DIR_PROJ_BAK_PATH}/ILoanPlugin/build.gradle

echo "copyScript end."
}

#写入jenkens配置参数
function writePropertes(){
echo "根据jenkins配置，替换关键配置"
#是否来自jenjins的打包，避免本地打包时使用这份配置
sed -i "" "s/FROM_JENKINS=.*/FROM_JENKINS=${FROM_JENKINS}/g" ${FILE_PAEM_PROTERTIES}
#sdk name
sed -i "" "s/SDK_NAME=.*/SDK_NAME=${SDK_NAME}/g" ${FILE_PAEM_PROTERTIES}
#kepler_config.xml配置,混合框架配置文件配置
sed -i "" "s/SDK_VERSION_CODE=.*/SDK_VERSION_CODE=${SDK_VERSION_CODE}/g" ${FILE_PAEM_PROTERTIES}
sed -i "" "s/SDK_VERSION_NAME=.*/SDK_VERSION_NAME=${SDK_VERSION_NAME}/g" ${FILE_PAEM_PROTERTIES}
sed -i "" "s/ILoanLib_Jar_Png_version=.*/ILoanLib_Jar_Png_version=${ILoanLib_Jar_Png_version}/g" ${FILE_PAEM_PROTERTIES}

sed -i "" "s/DIR_WORKSPACE=.*/DIR_WORKSPACE=${DIR_WORKSPACE}/g" ${FILE_PAEM_PROTERTIES}
sed -i "" "s/DIR_PROJECT=.*/DIR_PROJECT=${DIR_PROJECT}/g" ${FILE_PAEM_PROTERTIES}
sed -i "" "s/DIR_DEPLOY=.*/DIR_DEPLOY=${DIR_DEPLOY}/g" ${FILE_PAEM_PROTERTIES}

#jenkins参数，job名和构建时间戳
sed -i "" "s/JOB_NAME=.*/JOB_NAME=${JOB_NAME}/g" ${FILE_PAEM_PROTERTIES}
sed -i "" "s/BUILD_NUMBER=.*/BUILD_NUMBER=${BUILD_NUMBER}/g" ${FILE_PAEM_PROTERTIES}

echo "根据jenkins配置，写入关键配置完成"
}

#============================= start ============================
backProj

#copyScript

#writePropertes

#转gradle脚本
