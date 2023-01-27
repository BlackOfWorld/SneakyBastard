package net.blackofworld.sneakybastard.Utils.Packets;

public interface PacketListener {

    /**
     * Called when a packet is received
     *
     * @param packetEvent The {@link PacketEvent}
     */
    void onPacketReceived(PacketEvent packetEvent);

    /**
     * Called when a packet is sent
     *
     * @param packetEvent The {@link PacketEvent}
     */
    void onPacketSend(PacketEvent packetEvent);
}