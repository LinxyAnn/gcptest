# Larecette Recipe Command Microservice [![Build Status](http://34.91.81.167/jenkins/buildStatus/icon?job=laRecette%2Frecipe-command-microservice)](http://34.91.81.167/jenkins/job/laRecette/job/recipe-command-microservice/)

[How to run microservice](#how-to-run-microservice)

[Connect to Elasticsearch](#connect-to-elasticsearch)

[Add test data to Elasticsearch](#add-test-data-to-elasticsearch)

[Connect to Spanner](#connect-to-spanner)

[Add test data to Spanner](#add-test-data-to-spanner)

***
## How to run microservice
To run the application, you need to connect to the Elasticsearch database.

***
## Connect to Elasticsearch
Elasticsearch runs in the **docker**.

Pull images:
```
$ docker pull docker.elastic.co/elasticsearch/elasticsearch:7.6.1
```
Create user defined network:
```
$ docker network create somenetwork
```
Run Elasticsearch:
```
$ docker run -d --name elasticsearch --net somenetwork -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.6.1
```
More information: [elastic.co](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html),
 [hub.docker.com](https://hub.docker.com/_/elasticsearch).
 

Add properties to application.properties
```
spring.elasticsearch.jest.proxy.host=localhost
spring.elasticsearch.jest.proxy.port=9300
```
***

## Add test data to Elasticsearch
It’s better to create mapping first.

PUT-request:
```
http://localhost:9200/recipes
```
Request body:
[larecette-general/sql/recipeservice/elastic/mapping.json](https://github.com/bacardl/larecette-general/tree/master/sql/recipeservice/elastic/mapping.json)

And then add test data using a POST-request:
```
http://localhost:9200/_bulk
```
Request body: 
[larecette-general/sql/recipeservice/elastic/data.json](https://github.com/bacardl/larecette-general/tree/master/sql/recipeservice/elastic/data.json)  
**!Important:** There must be an empty line at the end of the file `data.json`.

##### Or use a ready-made collection of requests for Postman:  
[larecette-general/sql/recipeservice/elastic/ElasticInit.postman_collection.json](https://github.com/bacardl/larecette-general/tree/master/sql/recipeservice/elastic/ElasticInit.postman_collection.json)

***
## Connect to Spanner


1. Install [Cloud SDK](https://cloud.google.com/sdk/docs#install_the_latest_cloud_tools_version_cloudsdk_current_version)
2. [Initialize the SDK](https://cloud.google.com/sdk/docs/quickstart-windows#initialize_the_sdk)
3. Get [credentials](https://cloud.google.com/docs/authentication/getting-started)

More information:  
https://cloud.google.com/sdk/docs/quickstart-windows
https://cloud.google.com/spanner/docs/getting-started/set-up  
https://cloud.google.com/sdk/docs/authorizing

Add (change) properties to application.properties
```
spring.cloud.gcp.spanner.project-id=[MY_PROJECT_ID]
spring.cloud.gcp.spanner.instance-id=[MY_INSTANCE_ID]
spring.cloud.gcp.spanner.database=[MY_DATABASE]
```

***
## Add test data to Spanner

In [Google Cloud Console](https://console.cloud.google.com/spanner) choose project and create new instance.

Create database using the DDL syntax to generate the schema.  
DDL: [larecette-general/sql/recipeservice/spanner/RECIPE_shema.sql](https://github.com/bacardl/larecette-general/tree/master/sql/recipeservice/spanner/RECIPE_shema.sql)

Sequentially fill in the table data: RECIPE, INGREDIENT, PRODUCTS (optional).  
SQL: [larecette-general/sql/recipeservice/spanner/RECIPE_data.sql](https://github.com/bacardl/larecette-general/tree/master/sql/recipeservice/spanner/RECIPE_data.sql)


***
*To be continued...*
