FROM node:8.10.0 as builder

WORKDIR /app

COPY . /app

RUN npm install
RUN npm run-script build

### RUNTIME
FROM node:8.10.0-alpine
EXPOSE 3000

WORKDIR /app

ENV NODE_ENV=production

COPY --from=builder /app/build/ /app/build/
COPY --from=builder /app/package*.json /app/

RUN npm install --production

CMD [ "npm", "run-script", "start:production" ]
