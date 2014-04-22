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

A GET to `http://localhost:9000` returns the links to the api.
POST the following content to the `article` link to create a new article:

```
  {
    "content" : "A new blog article",
    "author": "bob",
    "creationDate" : 1 // UNIX timestamp
  }
```

Use the `articles` link to get all articles.