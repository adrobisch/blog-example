BLOG
====

A blog backend example with [CORS](http://de.wikipedia.org/wiki/Cross-Origin_Resource_Sharing) support and [HAL](http://stateless.co/hal_specification.html) resource representations.

Usage
=====

Execute

```
  sbt run
```

to start the server at port `9000`.

A GET to `http://localhost:9000` returns the links to the api:

```JavaScript
{
  "_links": {
    "self":"http://localhost:9000/",
    "articles":"http://localhost:9000/articles",
    "article":"http://localhost:9000/article",
    "random-articles":"http://localhost:9000/random-articles"
  }
}
```

POST the following content to the `article` link to create a new article:

```JavaScript
  {
    "content" : "A new blog article",
    "title" : "The title",
    "author": "bob",
    "creationDate" : 1
  }
```

`creationDate` must be a unix timestamp.

Use the `articles` link to get all articles:

```JavaScript
{
  "list":[{
    "id":0,
    "article":{
      "author":"bob",
      "title":"The title",
      "content":"A new blog article",
      "creationDate":1
    },
    "_links":{
      "self":"http://localhost:9000/article/0"
    }
  }],
  "_links":{"self":"http://localhost:9000/articles"}}
```
