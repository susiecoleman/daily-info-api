# News Cache

Search for news by search term. Caches news in a local cache.
News provided by [News API](https://newsapi.org/)

Project to experiment with [Monix Task](https://monix.io/docs/3x/eval/task.html) and [Http4s Client](https://http4s.org/v0.18/client/). Also used http4s [JSON handling](https://http4s.org/v0.18/json/)

## Set up
A conf file is required `/etc/my-apps-config/daily-info-api/secret.config.conf` which contains a api key for [News API](https://newsapi.org/).

```
news-api-key="my key"
```

## Running
`sbt run`
This will run the app locally on port 9000
[http://localhost:9000/](http://localhost:9000/)


