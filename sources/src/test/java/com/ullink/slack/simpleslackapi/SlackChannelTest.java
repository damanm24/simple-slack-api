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
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static com.ullink.slack.simpleslackapi.TestUtils.*;
import static org.junit.Assert.*;

public class SlackChannelTest {
  private final PrintStream standardOut = System.out;
  private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

  @Before
  public void setUp() {
    System.setOut(new PrintStream(outputStreamCaptor));
  }
  @After
  public void setOut() {
    System.setOut(standardOut);
  }

  @Test
  public void testChannelConnect() throws IOException {
    String authTokens = getLines("/authToken");
    if (authTokens == null) {
      return;
    }
    String[] tokens = authTokens.split("\n");
    SlackSession session = SlackSessionFactory.createWebSocketSlackSession(tokens[0]);
    session.connect();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost("https://slack.com/api/conversations.invite?channel=C02KT344KML&users=U02LBUQ1JV6");
    httpPost.addHeader("Authorization", "Bearer " + tokens[1]);

    ResponseHandler< String > responseHandler = response -> {
      int status = response.getStatusLine().getStatusCode();
      if (status >= 200 && status < 300) {
        HttpEntity entity = response.getEntity();
        return entity != null ? EntityUtils.toString(entity) : null;
      } else {
        throw new ClientProtocolException("Unexpected response status: " + status);
      }
    };


    SlackMemberChannelJoinedListener test = (event, session1) -> System.out.println("member joined");

    SlackMemberChannelLeftListener test1 = new SlackMemberChannelLeftListener() {
      @Override
      public void onEvent(SlackMemberChannelLeft event, SlackSession session) {
        System.out.println("member left");
      }
    };

    session.addSlackMemberChannelJoinedListener(test);
    session.addSlackMemberChannelLeftListener(test1);
    client.execute(httpPost);
    //session.inviteToChannel("C02KT344KML", "damanmulye24");
    assert(outputStreamCaptor.toString().equals("member joined\n"));
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