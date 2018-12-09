# Purpose
Build an application which runs either in AWS Lambda or in AWS Fargate (or ECS) without code modification.


## Setup process

### Requirements
* [NodeJS 8.10+ installed](https://nodejs.org/en/download/)
* [Docker installed](https://www.docker.com/community-edition)

## Installing dependencies
```bash
npm install
```

## Local tests
```bash
npm start
```


# Deployment

## Docker
### Docker build
```bash
npm run-script docker:build
npm run-script docker:run-local
```


## Lambda (SAM)
```bash
AWS_PROFILE=<profile> aws s3 mb <bucket_to_store_artifact>
AWS_PROFILE=<profile> BUCKET_ARTIFACT=<bucket_to_store_artifact> npm run-script sam:package
AWS_PROFILE=<profile> npm run-script sam:deploy
```


## Lambda (serverless)
```bash
AWS_PROFILE=<profile> npm run-script lambda:deploy
```