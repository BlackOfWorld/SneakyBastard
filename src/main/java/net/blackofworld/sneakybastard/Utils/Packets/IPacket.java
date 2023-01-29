package net.blackofworld.sneakybastard.Utils.Packets;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IPacket {
    PacketType direction();
}
