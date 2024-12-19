package itcast.blog.parser;

import itcast.blog.client.JsoupCrawler;
import itcast.domain.blog.Blog;
import itcast.domain.blog.enums.BlogStatus;
import itcast.domain.blog.enums.Platform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class VelogDataParser {

    private final JsoupCrawler jsoupCrawler;

    public List<String> getBlogUrls(final String jsonResponse) {
        final JSONObject jsonObject = new JSONObject(jsonResponse);
        final JSONArray trendingPosts = jsonObject.getJSONObject("data").getJSONArray("trendingPosts");

        return IntStream.range(0, trendingPosts.length())
                .mapToObj(trendingPosts::getJSONObject).map(post -> {
                    final String username = post.getJSONObject("user").getString("username");
                    final String urlSlug = post.getString("url_slug");
                    return "https://velog.io/@" + username + "/" + urlSlug; // BLOG URL
                }).toList();
    }

    public List<Blog> parseTrendingPosts(final List<String> blogUrl) {
        final LocalDate DEFAULT_PUBLISHED_AT = LocalDate.of(2024, 12, 12);
        return blogUrl.stream()
                .map(url -> {
                    try {
                        final Document document = jsoupCrawler.getHtmlDocumentOrNull(url);

                        final String title = Objects.requireNonNull(document).title();
                        final String thumbnail = document.selectFirst("meta[property=og:image]").attr("content");
                        final String content = document.select("div[class^=sc-][class$=atom-one]").text();
                        final String publishedAt = document.select(".information").eq(3).text();

                        log.info("title: {}", title);

                        return Blog.builder()
                                .platform(Platform.VELOG)
                                .title(title)
                                .originalContent(content)
                                .publishedAt(DEFAULT_PUBLISHED_AT)
                                .link(url)
                                .thumbnail(thumbnail)
                                .status(BlogStatus.ORIGINAL)
                                .build();
                    } catch (Exception e) {
                        log.error("Error", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
