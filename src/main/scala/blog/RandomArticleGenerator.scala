package blog

import de.svenjacobs.loremipsum.LoremIpsum

object RandomArticleGenerator {
  def loremIpsomArticles: List[Article] = {
    val articleCount = (3 + util.Random.nextInt(7))
    val loremIpsum = new LoremIpsum()
    val maxStartIndex = 25

    (for (i <- 1 to articleCount;
        title = loremIpsum.getWords(1 + util.Random.nextInt(5), util.Random.nextInt(maxStartIndex));
        author = "E-POST " + loremIpsum.getWords(1, util.Random.nextInt(maxStartIndex));
        content = loremIpsum.getParagraphs(1 + util.Random.nextInt(2)))
    yield Article(title = title, author = author, content = content, creationDate = new java.util.Date().getTime)).toList
  }
}
