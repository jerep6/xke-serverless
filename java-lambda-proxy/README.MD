
Vous reprennez la maintenance d'un projet Express qui était packagé en Docker.
Votre responsabilité est de le faire fonctionner avec AWS Lambda



## Lancer l'application
Faire tourner en local l'application
```
npm start

curl http://localhost:3000/books
```

Ou via docker

```
docker build -t xke-serverless .
docker run -ti --rm -p 3000:3000 xke-serverless

curl http://localhost:3000/books
```



# Migration AWS Lambda (Java)

## Transformer les requêtes API Gateway en HTTP
L'intégration API Gateway et Lambda sera configurée en *LAMBDA_PROXY*. De ce fait le handler de la lambda recevra [une requête
API Gateway](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format) et renvera [une réponse compatible API Gateway](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-output-format)

Ouvrez le répertoire *java-lambda-proxy*

Dans le package handler vous trouverez la classe *ProxyHandler*. Celle classe implémente le routeur.

Créer les classes *BooksGetsHandler.ts* et *BookGetHandler.ts* dans le répertoire *handler* :
```java
public class BooksGetHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(BooksGetHandler.class);

    private static volatile BookRepository bookRepository = BookRepository.instance();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(bookRepository.getBooks())
                .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                .build();
    }
}
```

Enregister les classes Dans le ProxyHandler:
```java

    static {
        resources.add(new Resource(GET, "/books", BooksGetHandler.class));
        resources.add(new Resource(GET, "/books/{bookId}", BookGetHandler.class));
    }

``` 

## serverless.yml

### Créer le fichier serverless.yml à la racine du projet
```yml
service: xke-{TRIGRAMME}

provider:
  name: aws
  runtime: java8
  region: eu-west-1
  endpointType: regional

plugins:
  
custom:
  stage: ${opt:stage, self:provider.stage, env:USER}

functions:

  books:
    handler: com.serverless.handler.ProxyHandler
    name: xke-serverless-books-${self:custom.stage}
    memorySize: 256
    timeout: 10
    events:
      # TODO
```


### Plugins
Contrairement au javascript [le plugin serverless-offline](https://www.npmjs.com/package/serverless-offline, 
on ne dispose pas de plugin en java permettant d'émuler le comportement de serverless en local.
Pour émuler le comportement serverless on se servira d'un serveur jetty qui n'est 
accessible qu'à partir du profil integration-test. 


### Events http
Le routage s'effectue au sein de la lambda via le ProxyHandler, il est donc pas obligatoire de déclarer toutes les routes dans l'API Gateway. 

Il existe plusieurs écoles avec chacune ses avantages et inconvénients. À vous de choisir la vôtre :
 - déployer une lambda par endpoint HTTP
 - déployer une lambda par ressource REST
 - déployer une lambda pour toute l'application
 
 NB: nous n'utiliserons pas le pattern *{any+}* au niveau du path car il n'est pas supporté par notre proxy.
 
 
```
events:
  - http:
      path: /books
      method: ANY
  - http:
      path: /books/{bookId}
      method: ANY
```
 
 
## Premier déploiement
Exécuter le script serverless-offline.sh qui se trouve à la racine du projet
### Local
```
 ./serverless-offline.sh

curl http://localhost:3000/books
```
### AWS

Avant de déployer sur AWS assurez vous de ne pas être en conflit avec les autres participants. Pour se faire nous allons utiliser 
la notion de stage du framework serverless. Utile pour différencier les environnements *--stage dev*, *--stage prod*, nous mettrons notre trigramme
ex: *--stage jpi*


```
serverless deploy --stage dev

curl http://ENDPOINT_API_GATEWAY/{TRIGRAMME}dev/books
```
Résultat attendu
![Console output](serverless-deloy-output1.png "Console output")


# Conclusion
A ce stade vous êtes capable de déployer votre code dans un container docker ainsi que dans une lambda. 

# Allons plus loin
## Variables
À l'origine, la notion de stage sert à gérer les environnements. Le framework serverless permet de gérer [différentes sources de variables](https://serverless.com/framework/docs/providers/aws/guide/variables/)

Voyons comment lister les variables par environnement dans un fichier de configuration. Disons que la mémoire allouée à la lambda dépend de l'environnement :


**Fichier serverless-config.yml**
```
jpidev:
  memory: 128

jpiprod:
  memory: 512
```

**Fichier serverless.yml
```
custom:
  # load key corresponding to the stage in the file or if key doesn't exist load "lnldev"
  perEnv: ${file(serverless-config.yml):${self:custom.stage}, file(serverless-config.yml):lnldev}
  
...

memorySize: ${self:custom.perEnv.memory}
```

## Tracing

** Activer xray**



# Migration DynamoDB

## Création de la table
La liste des livres est un mock stocké dans le code. Nous allons utiliser la base de données DynamoDB.

Il est possible d'ajouter des [ressources CloudFormation](https://serverless.com/framework/docs/providers/aws/guide/resources/) dans le fichier serverless.yml
```
resources:
  Resources:
    BookTable:
      Type: 'AWS::DynamoDB::Table'
      DeletionPolicy: Delete
      Properties:
        AttributeDefinitions:
          - AttributeName: 'id'
            AttributeType: 'S'
        KeySchema:
          - AttributeName: 'id'
            KeyType: 'HASH'
        ProvisionedThroughput:
          ReadCapacityUnits: 1
          WriteCapacityUnits: 1
        TableName: ${self:custom.stage}-book
```

## Utilisation de la table
### Ajouter la dépendance dynamodb
```
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-dynamodb</artifactId>
    <version>1.11.158</version>
</dependency>
```
### Ajouter le binding au modèle Book
```java
@DynamoDBTable(tableName = "book-lnal")
public class Book {

    @DynamoDBHashKey
    private String id;

    @DynamoDBAttribute
    private String title;

    @DynamoDBAttribute
    @JsonProperty("author_id")
    private int authorId;

    @DynamoDBAttribute
    private int year;

    @DynamoDBAttribute
    private int likes;
```
### Configurer le client Dynamo

```java
public class DynamoDBManager {

    private static volatile DynamoDBManager instance;

    private DynamoDBMapper mapper;

    // this is only defined if running integration tests with local dynamodb
    // will always be null when running as an aws lambda
    private static final String PORT = System.getProperty("dynamodb.port");
    private static final String END_TO_END = System.getProperty("endtoend");


    private DynamoDBManager() {
        AmazonDynamoDBClient client;
        AmazonDynamoDBClientBuilder clientBuilder = AmazonDynamoDBClientBuilder.standard();

        if (isNotBlank(PORT) && isNotBlank(END_TO_END)) {

            AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                    new AwsClientBuilder.EndpointConfiguration(String.format("http://localhost:%s", PORT), "");

            BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials("", "");
            AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(basicAWSCredentials);

            client = (AmazonDynamoDBClient) clientBuilder
                    .withCredentials(credentialsProvider)
                    .withEndpointConfiguration(endpointConfiguration).build();

        } else {
            // in this case, we are in aws lambda : credentials are inherited
            // from environment variables
            client = (AmazonDynamoDBClient) clientBuilder.withRegion(Regions.EU_CENTRAL_1).build();
        }
        mapper = new DynamoDBMapper(client);
    }

    public static DynamoDBManager instance() {

        if (instance == null) {
            synchronized (DynamoDBManager.class) {
                if (instance == null) {
                    instance = new DynamoDBManager();
                }
            }
        }
        return instance;
    }

    public static DynamoDBMapper mapper() {
        return instance().mapper;
    }
}
```

### Ajouter la variable d'environnement
L'application est déjà paramétrée pour utiliser la variable d'environnement TABLE_BOOK (config.js)

```
functions:

  books:
  
    environment:
      TABLE_BOOK: ${self:custom.stage}-book
```


### Coder le répository
```java
public class BookDao {

    private static volatile BookDao instance;

    public static final String TABLE_NAME_ENV_VAR = "TABLE_BOOK";

    private static final DynamoDBMapper MAPPER = DynamoDBManager.mapper();

    private static volatile DynamoDBMapperConfig bookConfig;

    private BookDao() {
        Map<String, String> env = System.getenv();
        String bookTableName = env.get(TABLE_NAME_ENV_VAR);

        DynamoDBMapperConfig.TableNameOverride tableNameOverride =
                DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(bookTableName);

        bookConfig = DynamoDBMapperConfig.builder()
                .withConsistentReads(CONSISTENT)
                .withTableNameOverride(tableNameOverride)
                .withConversionSchema(ConversionSchemas.V2)
                .build();
    }

    public static BookDao instance() {
        if (instance == null) {
            synchronized (BookDao.class) {
                if (instance == null) {
                    instance = new BookDao();
                }
            }
        }
        return instance;
    }

    public Optional<Book> findBookById(int id) {
        return Optional.ofNullable(MAPPER.load(Book.class, id, bookConfig));
    }

    public List<Book> findBooks() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withConsistentRead(false);
        return MAPPER.scan(Book.class, scanExpression, bookConfig);
    }
}
```

### Brancher le répository dans le handler


### Ajouter les droits à la lambda

**Fichier serverless.yml**
```
provider:
  iamRoleStatements:
    - Effect: Allow
        Action:
          - dynamodb:*
        Resource:
          - "arn:aws:dynamodb:${self:provider.region}:*:table/${self:custom.stage}-book"
```

### Déployer

```
serverless deploy --stage {TRIGRAMME}dev

curl http://ENDPOINT_API_GATEWAY/{TRIGRAMME}dev/books
```
