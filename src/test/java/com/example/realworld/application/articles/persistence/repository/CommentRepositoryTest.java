package com.example.realworld.application.articles.persistence.repository;

import com.example.realworld.application.articles.exception.NotFoundArticleException;
import com.example.realworld.application.articles.persistence.Article;
import com.example.realworld.application.articles.persistence.Comment;
import com.example.realworld.application.tags.persistence.Tag;
import com.example.realworld.application.users.persistence.User;
import com.example.realworld.application.users.persistence.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.example.realworld.application.articles.ArticleFixture.createArticle;
import static com.example.realworld.application.users.UserFixture.createUser;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("글에 새로운 커멘트 등록하는 테스트")
    @Test
    void when_postComment_expected_success_posting_comment() {
        // given
        User author = createUser("seokrae@gmail.com");
        User savedUser = userRepository.save(author);
        Article article = createArticle(1, savedUser, Tag.of("Kotlin"));
        Article savedArticle = articleRepository.save(article);

        // when
        Comment newComment = Comment.of("comment write", savedUser, savedArticle);
        savedArticle.comments().add(newComment);
        Comment savedComment = commentRepository.save(newComment);

        // then
        assertThat(newComment.getBody()).isEqualTo(savedComment.getBody());
    }

    @DisplayName("글의 커멘트 조회하기")
    @Test
    void when_getComments_expected_success_commentList() {
        User author = createUser("seokrae@gmail.com");
        User savedUser = userRepository.save(author);
        Article article = createArticle(1, savedUser, Tag.of("Kotlin"));
        Article savedArticle = articleRepository.save(article);

        // when
        Comment newComment1 = Comment.of("comment write1", savedUser, savedArticle);
        Comment newComment2 = Comment.of("comment write2", savedUser, savedArticle);
        Comment newComment3 = Comment.of("comment write3", savedUser, savedArticle);

        List<Comment> savedComments = commentRepository.saveAll(List.of(newComment1, newComment2, newComment3));

        savedArticle.comments().addAll(savedComments);
        int tagSize = savedArticle.getComments().size();

        assertThat(tagSize).isEqualTo(3);
    }

    @DisplayName("커멘트 수정 확인 테스트")
    @Test
    void when_update_expected_success_comment_info() {
        // given
        User author = createUser("seokrae@gmail.com");
        User savedUser = userRepository.save(author);

        Article article = createArticle(1, savedUser, Tag.of("Kotlin"));
        Article savedArticle = articleRepository.save(article);

        Comment newComment = Comment.of("comment write", savedUser, savedArticle);
        Comment savedComment = commentRepository.save(newComment);
        savedArticle.comments().add(savedComment);
        savedUser.addArticle(savedArticle);

        // when
        savedComment.update("updateBody");

        Article findArticle = savedUser.getArticleByTitle("타이틀-1")
                .orElseThrow(NotFoundArticleException::new);
        String body = findArticle.comments().get(savedComment.getId()).getBody();

        // then
        assertThat(body).isEqualTo(savedComment.getBody());
    }

    @DisplayName("커멘트 삭제 테스트")
    @Test
    void when_deleteComment_expected_success_deleted() {
        // given
        User author = createUser("seokrae@gmail.com");
        User savedUser = userRepository.save(author);

        Article article = createArticle(1, savedUser, Tag.of("Kotlin"));
        Article savedArticle = articleRepository.save(article);

        // when
        Comment newComment = Comment.of("comment write", savedUser, savedArticle);
        Comment savedComment = commentRepository.save(newComment);

        savedArticle.getComments().add(savedComment);
        savedUser.addArticle(savedArticle);

        savedArticle.getComments().remove(savedComment);

        // then
        assertThat(savedArticle.getComments().size()).isZero();
    }
}