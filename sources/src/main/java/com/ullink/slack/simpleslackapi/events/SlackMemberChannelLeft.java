package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackUser;
import lombok.Data;
import lombok.NonNull;

@Data
public class SlackMemberChannelLeft implements SlackEvent {
    @NonNull
    private SlackChannel slackChannel;

    @NonNull
    private SlackUser user;

    /**
     * CS427 
     * Issue Link: https://github.com/Itiviti/simple-slack-api/issues/115
     * Returns the event type associated with this class
     * @return Slack Member Channel Leave event type
     */
    @Override
    public SlackEventType getEventType() {
        return SlackEventType.SLACK_MEMBER_CHANNEL_LEFT;
    }

    /**
     * CS427 
     * Issue Link: https://github.com/Itiviti/simple-slack-api/issues/115
     * Returns the SlackChannel object associated with a Slack Member Leave event
     * @return SlackChannel associated with this event
     */
    public SlackChannel getSlackChannel() { return slackChannel; }

    /**
     * CS427 
     * Issue Link: https://github.com/Itiviti/simple-slack-api/issues/115
     * Returns the SlackUser object associated with a Slack Member Leave event
     * @return SlackUser associated with this event
     */
    public SlackUser getSlackUser() { return user; }

}
