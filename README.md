# myRetail

myRetail is RESTful service that combines product data from the RedSky API and pricing data from a local NoSQL database.
It is a [Grails 3](https://grails.org/) app backed by an in-memory [Nitrite](https://www.dizitart.org/nitrite-database.html) database.

## Installation

NB: This section assumes you have [Git](https://git-scm.com/downloads) and [JDK 8](http://www.oracle.com/technetwork/pt/java/javase/downloads/index.html) installed on the machine where you are running the app.

### Download

Clone the repo with git:

```bash
$ git clone git@github.com:joshareed/myretail.git
$ cd myretail
```

### Build

Build the code:

```bash
$ ./gradlew clean build
```

### Testing

Run the app via Gradle:

```bash
$ ./gradlew bootRun
```

Or via runnable WAR:

```bash
$ ./gradlew build
$ java -jar build/libs/myretail-0.0.1.war
```

After a few seconds, the app will be running on your [local machine](http://localhost:8080/products/13860428).
You can test it using [curl](https://curl.haxx.se/) or via your browser:

```bash
$ curl -X GET -H "Accept: application/json" http://localhost:8080/products/13860428
```

You should get a JSON response similar to:

```json
{"id":13860428,"name":"The Big Lebowski (Blu-ray)","current_price":{"value":19.98,"currency_code":"USD"}}
```

The price reported here is the list price coming from the RedSky API.  We can update the price:

```bash
$ curl -X PUT -H "Accept: application/json" -H "Content-Type: application/json" -d "{\"id\":13860428,\"current_price\":{\"value\":9.99}}" http://localhost:8080/products/13860428
```

You should get a JSON response reflecting the new price:

```json
{"id":13860428,"name":"The Big Lebowski (Blu-ray)","current_price":{"value":9.99,"currency_code":"USD"}}
```

If you try to get a product that doesn't exist:

```bash
$ curl -i -X GET -H "Accept: application/json" http://localhost:8080/products/99999999
```

You should get a 404 error with a JSON response:

```
HTTP/1.1 404
X-Application-Context: application:development
Content-Type: application/json;charset=UTF-8
Content-Language: en-US
Transfer-Encoding: chunked
Date: Wed, 18 Apr 2018 17:32:08 GMT

{"message":"Invalid id '99999999'","error":404}
```

### Automated Tests (Optional)

The code has several automated tests:

```bash
$ ./gradlew clean test
```

The test output will be in `build/reports/tests/index.html`









