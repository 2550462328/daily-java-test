package cn.zhanghui.demo.daily.redis_tomcat_session;

import java.io.IOException;

public interface Serializer {
    void setClassLoader(ClassLoader loader);

    byte[] attributesHashFrom(RedisSession session) throws IOException;

    byte[] serializeFrom(RedisSession session, SessionSerializationMetadata metadata) throws IOException;

    void deserializeInto(byte[] data, RedisSession session, SessionSerializationMetadata metadata) throws IOException, ClassNotFoundException;
}
