# Daily Info API

Api to give you daily information (currently can only get a single news article)

News provided by [News API](https://newsapi.org/)

## Set up
A conf file is required `/etc/my-apps-config/daily-info-api/secret.config.conf` which contains a api key for [News API](https://newsapi.org/).

```
news-api-key="my key"
```

## Running
`sbt run`
This will run the app locally on port 9000
[http://localhost:9000/](http://localhost:9000/)

### TODO

* Use http4s using tasks [https://http4s.org/v0.18/client/](https://http4s.org/v0.18/client/)
    * Establish how to implement an implicit of type Effect[Task]
    * Get response as json from news api [https://http4s.org/v0.18/json/](https://http4s.org/v0.18/json/)
    * Use cache when getting news data using http4s
