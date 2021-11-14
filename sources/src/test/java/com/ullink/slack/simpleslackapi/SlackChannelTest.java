package com.ullink.slack.simpleslackapi;

import com.ullink.slack.simpleslackapi.events.SlackChannelLeft;
import com.ullink.slack.simpleslackapi.events.SlackMemberChannelJoined;
import com.ullink.slack.simpleslackapi.events.SlackMemberChannelLeft;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackChannelLeftListener;
import com.ullink.slack.simpleslackapi.listeners.SlackMemberChannelJoinedListener;
import com.ullink.slack.simpleslackapi.listeners.SlackMemberChannelLeftListener;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;
import com.ullink.slack.simpleslackapi.replies.SlackMessageReply;
import org.junit.Test;

import java.io.IOException;

import static com.ullink.slack.simpleslackapi.TestUtils.getFile;
import static com.ullink.slack.simpleslackapi.TestUtils.gson;
import static org.junit.Assert.*;

public class SlackChannelTest {

  @Test
  public void testChannelConnect() throws IOException {
    SlackSession session = SlackSessionFactory.createWebSocketSlackSession("xoxb-2661572183286-2665263371079-nz5YTqJWddt5eoaf8VvYLqEq");
    session.connect();

    SlackMessagePostedListener messagePostedListener = new SlackMessagePostedListener()
    {
      @Override
      public void onEvent(SlackMessagePosted event, SlackSession session)
      {
        SlackChannel channelOnWhichMessageWasPosted = event.getChannel();
        String messageContent = event.getMessageContent();
        SlackUser messageSender = event.getSender();
        System.out.println(messageContent);
      }
    };

    SlackMemberChannelJoinedListener test = new SlackMemberChannelJoinedListener() {
      @Override
      public void onEvent(SlackMemberChannelJoined event, SlackSession session) {
        System.out.println("member joined");
      }
    };

    SlackMemberChannelLeftListener test1 = new SlackMemberChannelLeftListener() {
      @Override
      public void onEvent(SlackMemberChannelLeft event, SlackSession session) {
        System.out.println("member left");
      }
    };

    SlackChannelLeftListener listener = new SlackChannelLeftListener() {
      @Override
      public void onEvent(SlackChannelLeft event, SlackSession session) {
        System.out.println("someone left");
      }
    };
    session.addChannelLeftListener(listener);
    session.addMessagePostedListener(messagePostedListener);
    session.addSlackMemberChannelJoinedListener(test);
    session.addSlackMemberChannelLeftListener(test1);

    SlackMessageHandle<SlackMessageReply> reply = session.sendMessage(session.findChannelByName("general"), "Hello World");

    while(true) {}
  }

  @Test
  public void testJsonSerialize() throws IOException {
    SlackChannel channel = SlackChannel.builder()
        .id("test")
        .name("test_name")
        .topic("test_topic")
        .purpose("test_purpose")
        .direct(true)
        .build();
    String json = gson().toJson(channel);
    assertNotNull(json);
    assertEquals(getFile("/channel.json"), json);
  }


  @Test
  public void testJsonDeserialize() throws IOException {
    String json = getFile("/channel.json");
    SlackChannel object = gson().fromJson(json, SlackChannel.class);
    assertEquals("test", object.getId());
    assertEquals("test_name", object.getName());
    assertEquals("test_topic", object.getTopic());
    assertEquals("test_purpose", object.getPurpose());
    assertTrue(object.isDirect());
    assertFalse(object.isArchived());
    assertTrue(object.getMembers().isEmpty());
  }

}