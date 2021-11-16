#/bin/sh

aws lambda update-function-code --function-name dlAdhocLambda-cert --s3-bucket ev-buildartifacts --s3-key apps/engvillage/cert/dl_adhoc_lambda-aws.jar
LVERSION=`aws lambda publish-version --function-name dlAdhocLambda-cert | grep '"Version"' | sed -e 's/[":, Version]//g'`
aws lambda update-alias --function-name dlAdhocLambda-cert --name active --function-version ${LVERSION}
