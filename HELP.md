This is an application the creates and API endpoint that receives files and returns them zipped.

The application is deployed on a docker container and the port that is using is 8080.

The end point that is used is

POST -> `http://localhost:8080/zip`

The request it's accepting is form data with a list of files with tha label "file".

To run the application you need to have MAVEN, JAVA 17 and Docker installed.

To test build and store the docker image you run the following maven command:

```shell
mvn spring-boot:build-image
```

Then start the app container with the command:

```shell
docker run -d -p 8080:8080 multifile-api:1.0 
```

Now service will be available in `http://localhost:8080`

An example CURL command that can be run from the root folder of the project is as follows:

```shell
curl -i -X POST \
   -H "Content-Type:multipart/form-data" \
   -F "file=@\".\src\test\resource\test1.txt\";type=text/plain;filename=\"test1.txt\"" \
   -F "file=@\".\src\test\resource\test2.txt\";type=text/plain;filename=\"test2.txt\"" \
 'http://locahost:8080/zip'
```

There is a Postman collection as well to test the API in the root path `Tests.postman_collection.json`