#/bin/sh

aws lambda update-function-code --function-name dlAdhocLambda-prod --s3-bucket ev-buildartifacts --s3-key apps/engvillage/prod/dl_adhoc_lambda-aws.jar
LVERSION=`aws lambda publish-version --function-name dlAdhocLambda-prod | grep '"Version"' | sed -e 's/[":, Version]//g'`
aws lambda update-alias --function-name dlAdhocLambda-prod --name active --function-version ${LVERSION}
