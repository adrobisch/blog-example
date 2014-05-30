package blog

import org.specs2.mutable.Specification

class RandomArticleSpec extends Specification {
  "Random Article generator" should {
    "generate lorem ipsum articles" in {
      RandomArticleGenerator.loremIpsomArticles.size must beGreaterThan(0)
    }
  }
}
