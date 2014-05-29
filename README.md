Logback Appender for HipChat
============================

With this appender you can forward error logs to a HipChat room.

Please note that this appender and HipChat at all are not intended to be used for high volumes logging.

It's a good idea to use this appender for error-level log entries only to get instant notifications to HipChat room and as addition to your existing appender configurations like `FileAppender`, etc.

**Notes from HipChat [documentation](https://www.hipchat.com/docs/apiv2/method/send_room_notification):**
  * There may be a slight delay before messages appear in the room.
  * Looking to send in log messages or errors in high volume? We suggest checking out a service like [Exceptional](http://exceptional.io/) or [Papertrail](http://papertrailapp.com/) instead.

## Appender Configuration

Appender uses [HipChat's room notification API](https://www.hipchat.com/docs/apiv2/method/send_room_notification) for message delivery.

To use this appender you first need to create a room notification token:
  1. Go to https://hipchat.com/rooms
  2. Pick a room and click 'Tokens' under 'Administration' menu
  3. Create new token

Second step is to add configuration of the appender to your `logback.xml`:

```xml
  <appender name="HipChat" class="com.anjlab.logback.hipchat.HipChatRoomAppender">
    <layout>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </layout>
    <room>room</room>        <!-- The id or name of the room -->
    <apiKey>api-key</apiKey> <!-- API Key from the above step -->
    <color>red</color>       <!-- Background color for message.
                                  Valid values: yellow, red, green, purple, gray,
                                  random (default: 'red') -->
    <notify>true</notify>    <!-- Whether or not this message should trigger
                                  a notification for people in the room
                                  (change the tab color, play a sound, etc).
                                  Each recipient's notification preferences
                                  are taken into account. (default: true) -->
  </appender>
```

This appender uses synchronous delivery of log messages. That's why it's a good idea to wrap it into Logback's [AsyncAppender](http://logback.qos.ch/manual/appenders.html#AsyncAppender):

```xml
  <appender name="AsyncHipChat" class="ch.qos.logback.classic.AsyncAppender">
    <appender-ref ref="HipChat" />
    <filter class="ch.qos.logback.classic.filter.LevelFilter">
      <level>ERROR</level>
      <onMatch>ACCEPT</onMatch>
      <onMismatch>DENY</onMismatch>
    </filter>
  </appender>
  
  <root level="DEBUG">
    <appender-ref ref="AsyncHipChat" />
  </root>
```

It's also a good idea to set a filter, like in the example above, to only log ERROR messages to HipChat.

## Maven repository

#### Use with Maven
``` xml
<repositories>
   ...
   <repository>
      <id>bintray-jcenter</id>
      <url>http://jcenter.bintray.com</url>
      <snapshots>
         <enabled>false</enabled>
      </snapshots>
   </repository>
   ...
</repositories>

<dependencies>
   ...
   <dependency>
      <groupId>com.anjlab</groupId>
      <artifactId>logback-hipchat-appender</artifactId>
      <version>1.0.0</version>
   </dependency>
   ...
</dependencies>
```

#### Or with Gradle
```groovy
   repositories {
      ...
      maven { url 'http://jcenter.bintray.com' }
      ...
   }
   dependencies {
      ...
      compile 'com.anjlab:logback-hipchat-appender:1.0.0'
      ...
   }
```

## License

Copyright 2014 AnjLab

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
