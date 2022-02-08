package main.service;

import lombok.RequiredArgsConstructor;
import main.model.Tag;
import main.repository.TagToPostRepository;
import main.repository.TagsRepository;
import main.response.api.TagInTagResponse;
import main.response.api.TagResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagsService {

    private final TagsRepository tagsRepository;
    private final TagToPostRepository tagToPostRepository;

    public TagResponse getTagResponse(String query) {

        TagResponse tagResponse = new TagResponse();
        List<TagInTagResponse> tagInTagResponses = new ArrayList<>();
        List<Tag> tags = new ArrayList<>();

        if (query != null) {
            tags.add(tagsRepository.findTagByName(query));
        } else {
            tagsRepository.findAll().forEach(tags::add);
        }

        tags.forEach(t -> {
            TagInTagResponse tagInTagResponse = new TagInTagResponse();
            tagInTagResponse.setName(t.getName());
            tagInTagResponse.setWeight(getTagWeight(t));
            tagInTagResponses.add(tagInTagResponse);
        });

        tagResponse.setTags(tagInTagResponses);
        return tagResponse;
    }

    private double getTagWeight(Tag tag) {
        List<Integer> tagCounts = new ArrayList<>();
        tagsRepository.findAll().forEach(t -> tagCounts.add(tagToPostRepository.findAllTagToPostsByTagId(t.getId()).size()));
        double maxCountTag = Collections.max(tagCounts);
        double countTag = tagToPostRepository.findAllTagToPostsByTagId(tag.getId()).size();

        return countTag / maxCountTag;
    }
}
