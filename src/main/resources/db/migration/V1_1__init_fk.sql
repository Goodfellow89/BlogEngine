alter table post_comments
    add constraint post_comments_to_posts
    foreign key (post_id)
    references posts (id);

alter table post_comments
    add constraint post_comments_to_parent_post_comments
    foreign key (parent_id)
    references post_comments (id);

alter table post_comments
    add constraint post_comments_to_users
    foreign key (user_id)
    references users (id);

alter table post_votes
    add constraint post_votes_to_posts
    foreign key (post_id)
    references posts (id);

alter table post_votes
    add constraint post_votes_to_users
    foreign key (user_id)
    references users (id);

alter table posts
    add constraint posts_to_moderators
    foreign key (moderator_id)
    references users (id);

alter table posts
    add constraint posts_to_users
    foreign key (user_id)
    references users (id);

alter table tag2post
    add constraint tag2post_to_posts
    foreign key (post_id)
    references posts (id);

alter table tag2post
    add constraint tag2post_to_tags
    foreign key (tag_id)
    references tags (id);