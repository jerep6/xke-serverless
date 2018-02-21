FROM node:8.9.4 as builder

WORKDIR /app

COPY . /app

RUN yarn
RUN yarn build

### RUNTIME
FROM node:8.9.4-alpine
EXPOSE 3000

WORKDIR /app

ENV NODE_ENV=production

COPY --from=builder /app/build/ /app/build/
COPY --from=builder /app/package*.json /app/

RUN yarn --production

CMD [ "npm", "run", "start:production" ]
