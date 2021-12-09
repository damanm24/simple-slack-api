package com.ullink.slack.simpleslackapi;

import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.replies.SlackChannelReply;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static com.ullink.slack.simpleslackapi.TestUtils.getFile;
import static com.ullink.slack.simpleslackapi.TestUtils.gson;
import static com.ullink.slack.simpleslackapi.TestUtils.getLines;
import static org.junit.Assert.*;


public class SlackChannelTest {
  //THIS TEST REQUIRES A LIVE SLACK ENVIRONMENT TO BE FUNCTIONAL
  //AUTH TOKENS MUST BE PLACED IN A FILE UNDER RESOURCES CALLED "authToken.safe"
  //Corresponds to validating issue: https://github.com/Itiviti/simple-slack-api/issues/284
  //Issue 284
  @Test
  public void testChannelMemberListeners() throws IOException {
    String authTokens = getLines("/authToken.safe");
    if (authTokens == null) {
      return;
    }
    String[] tokens = authTokens.split("\n");
    SlackSession session = SlackSessionFactory.createWebSocketSlackSession(tokens[0], "");
    session.connect();

    SlackMessageHandle<SlackChannelReply> reply  = session.inviteToChannel("C02KT344KML", "damanm24");

    assert(reply.getReply().isOk());
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