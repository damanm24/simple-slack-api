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

    @Override
    public SlackEventType getEventType() {
        return SlackEventType.SLACK_MEMBER_CHANNEL_LEFT;
    }

}
