package net.blackofworld.SneakyBastard.Utils.Packets;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IPacket {
    PacketType direction();
}
