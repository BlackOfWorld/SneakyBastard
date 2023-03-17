package net.blackofworld.SneakyBastard.Utils.Packets;

import java.lang.reflect.Method;

public record PacketMap(Method m, PacketInjector.PacketListener listener, PacketType packetType) {
}
