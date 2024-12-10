package itcast.ai.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import itcast.ai.Message;
import itcast.ai.client.GPTClient;
import itcast.ai.dto.request.GPTSummaryRequest;
import itcast.ai.dto.response.GPTSummaryResponse;
import itcast.domain.blog.Blog;
import itcast.domain.blog.enums.BlogStatus;
import itcast.domain.blog.repository.BlogRepository;
import itcast.domain.user.enums.Interest;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class GPTServiceTest {

    @Mock
    private GPTClient gptClient;

    @Mock
    private BlogRepository blogRepository;

    @InjectMocks
    private GPTService gptService;

    @Test
    @DisplayName("올바른 요청이 들어오면 요약에 성공한다.")
    void summaryContent_success_test() {
        // given
        final Long blogId = 1L;
        final String originalContent = "test originalContent";

        final Blog blog = mock(Blog.class);

        final Message message = new Message("user", originalContent);

        final GPTSummaryRequest gptSummaryRequest = new GPTSummaryRequest("gpt-4o-mini", message, 0.7f);

        final String jsonResponse = "{\n" +
                "  \"category\" : \"BACKEND\",\n" +
                "  \"summary\" : \"test summary\",\n" +
                "  \"rating\" : 8\n" +
                "}";

        GPTSummaryResponse.Message messages = new GPTSummaryResponse.Message(jsonResponse);
        GPTSummaryResponse.Choice choice = new GPTSummaryResponse.Choice(messages);
        GPTSummaryResponse response = new GPTSummaryResponse(Collections.singletonList(choice));

        when(gptClient.sendRequest(gptSummaryRequest)).thenReturn(response);
        ;
        when(blogRepository.findById(blogId)).thenReturn(Optional.of(blog));

        // when
        gptService.updateBlogBySummaryContent(gptSummaryRequest);

        // then
        verify(blog, times(1)).applySummaryUpdate(any(), any(), any(), any());
        verify(blog, times(1)).applySummaryUpdate(eq("test summary"), eq(Interest.BACKEND), eq(8L),
                eq(BlogStatus.SUMMARY));
        verify(gptClient, times(1)).sendRequest(gptSummaryRequest);
    }
}
