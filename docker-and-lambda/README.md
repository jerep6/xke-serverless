# Requirements
* [NodeJS 8.10+ installed](https://nodejs.org/en/download/)
* [Docker installed](https://www.docker.com/community-edition)

## Setup process
## Installing dependencies
```bash
npm install
```

## Local tests
```bash
npm start
```


# Deployment
The application runs in local either with *Docker* or with *AWS Lambda* 

## Docker
### Docker build
```bash
npm run-script docker:build
```

### Docker run
```bash
npm run-script docker:run
```

## Lambda
```bash
AWS_PROFILE=<profile> npm run-script lambda:deploy
```