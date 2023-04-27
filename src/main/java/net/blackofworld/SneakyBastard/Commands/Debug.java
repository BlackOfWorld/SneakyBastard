package net.blackofworld.SneakyBastard.Commands;

import lombok.SneakyThrows;
import lombok.experimental.ExtensionMethod;
import net.blackofworld.SneakyBastard.Command.CommandBase;
import net.blackofworld.SneakyBastard.Command.CommandCategory;
import net.blackofworld.SneakyBastard.Command.CommandInfo;
import net.blackofworld.SneakyBastard.Extensions.PlayerExt;
import net.minecraft.network.protocol.game.*;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@CommandInfo(command = "debug", description = "Debug shit", Syntax = "<blocks/packets>", category = CommandCategory.Miscellaneous, requiredArgs = 1)
@ExtensionMethod({Player.class, PlayerExt.class})
public class Debug extends CommandBase {
    static HashMap<String, String> packetTable = new HashMap<>();
    static {
        try {
            FileUtils.forceMkdir(new File("SneakyBastard/"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private Location first;
    @Override
    public void Execute(Player p, ArrayList<String> args) {

        switch (args.get(0)) {
            case "blocks" -> p.Reply(doBlocks(p, false) ? "Success!" : "Error!");
            case "blocks2" -> p.Reply(doBlocks(p, true) ? "Success!" : "Error!");
            case "packets" -> doPackets(p,args);
            default -> p.sendHelp(this);
        }
    }
    public void doPackets(Player p, ArrayList<String> args) {
        if(args.size() <= 1)
        {p.Reply("Packets require an argument! (Packet name, duh)"); return;}
        String result = packetTable.getOrDefault(args.get(1), null);
        if (result == null) {
            p.Reply("Packet not found! :c", "Is your packet name properly capitalized?");
        } else {
            p.Reply("Packet name: "+ result);
        }
    }
    @SneakyThrows
    public boolean doBlocks(Player p, boolean ignoreAir) {
        Location pl = p.getLocation();
        if(first == null) {
            first = pl;
            p.sendMessage("First pos set!");
            return true;
        }

        int topBlockX = (Math.max(first.getBlockX(), pl.getBlockX()));
        int bottomBlockX = (Math.min(first.getBlockX(), pl.getBlockX()));

        int topBlockY = (Math.max(first.getBlockY(), pl.getBlockY()));
        int bottomBlockY = (Math.min(first.getBlockY(), pl.getBlockY()));

        int topBlockZ = (Math.max(first.getBlockZ(), pl.getBlockZ()));
        int bottomBlockZ = (Math.min(first.getBlockZ(), pl.getBlockZ()));

        BufferedWriter log = createLogFile("Blocks");

        for(int x = bottomBlockX; x <= topBlockX; x++)
        {
            for(int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for(int y = bottomBlockY; y <= topBlockY; y++)
                {
                    int diffBlockX = (topBlockX - x);
                    int diffBlockY = (topBlockY - y);
                    int diffBlockZ = (topBlockZ - z);

                    Block block = pl.getWorld().getBlockAt(x, y, z);
                    if(ignoreAir && block.getType() == Material.AIR) continue;
                    log.append(String.format("p.getWorld().getBlockAt(x + %d, y + %d, z + %d).setType(Material.%s);\n", diffBlockX, diffBlockY, diffBlockZ, block.getType()));
                }
            }
        }
        log.close();
        p.Reply("Wrote to SneakyBastard folder!");
        first = null;
        return true;
    }

    private BufferedWriter createLogFile(String Action) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH-mm");
        String time = sdf.format(new Date());
        return new BufferedWriter(new FileWriter(new File(String.format("SneakyBastard/Debug-%s-%s.txt", Action, time)).getAbsolutePath(), false));
    }



    static {
        packetTable.put(ClientboundInitializeBorderPacket.class.getSimpleName(), "ClientboundInitializeBorderPacket");
        packetTable.put(ClientboundSetBorderCenterPacket.class.getSimpleName(), "ClientboundSetBorderCenterPacket");
        packetTable.put(ClientboundSystemChatPacket.class.getSimpleName(), "ClientboundSystemChatPacket");
        packetTable.put(ServerboundUseItemPacket.class.getSimpleName(), "ServerboundUseItemPacket");
        packetTable.put(ServerboundUseItemOnPacket.class.getSimpleName(), "ServerboundUseItemOnPacket");
        packetTable.put(ClientboundSectionBlocksUpdatePacket.class.getSimpleName(), "ClientboundSectionBlocksUpdatePacket");
        packetTable.put(ClientboundAwardStatsPacket.class.getSimpleName(), "ClientboundAwardStatsPacket");
        packetTable.put(ClientboundPlayerPositionPacket.class.getSimpleName(), "ClientboundPlayerPositionPacket");
        packetTable.put(ClientboundResourcePackPacket.class.getSimpleName(), "ClientboundResourcePackPacket");
        packetTable.put(ServerboundChatAckPacket.class.getSimpleName(), "ServerboundChatAckPacket");
        packetTable.put(ServerPacketListener.class.getSimpleName(), "ServerPacketListener");
        packetTable.put(ServerboundSetCommandBlockPacket.class.getSimpleName(), "ServerboundSetCommandBlockPacket");
        packetTable.put(DebugPackets.class.getSimpleName(), "DebugPackets");
        packetTable.put(ClientboundCommandsPacket.class.getSimpleName(), "ClientboundCommandsPacket");
        packetTable.put(ClientboundSetTitlesAnimationPacket.class.getSimpleName(), "ClientboundSetTitlesAnimationPacket");
        packetTable.put(ClientboundTagQueryPacket.class.getSimpleName(), "ClientboundTagQueryPacket");
        packetTable.put(ClientboundSetPlayerTeamPacket.class.getSimpleName(), "ClientboundSetPlayerTeamPacket");
        packetTable.put(ServerboundEditBookPacket.class.getSimpleName(), "ServerboundEditBookPacket");
        packetTable.put(ServerboundInteractPacket.class.getSimpleName(), "ServerboundInteractPacket");
        packetTable.put(ClientboundCooldownPacket.class.getSimpleName(), "ClientboundCooldownPacket");
        packetTable.put(ClientboundContainerClosePacket.class.getSimpleName(), "ClientboundContainerClosePacket");
        packetTable.put(ClientboundPlayerInfoUpdatePacket.class.getSimpleName(), "ClientboundPlayerInfoUpdatePacket");
        packetTable.put(ServerboundChatPacket.class.getSimpleName(), "ServerboundChatPacket");
        packetTable.put(ClientboundSetTitleTextPacket.class.getSimpleName(), "ClientboundSetTitleTextPacket");
        packetTable.put(ClientboundUpdateMobEffectPacket.class.getSimpleName(), "ClientboundUpdateMobEffectPacket");
        packetTable.put(ServerboundClientInformationPacket.class.getSimpleName(), "ServerboundClientInformationPacket");
        packetTable.put(ClientboundLevelChunkPacketData.class.getSimpleName(), "ClientboundLevelChunkPacketData");
        packetTable.put(ClientboundBossEventPacket.class.getSimpleName(), "ClientboundBossEventPacket");
        packetTable.put(ClientboundContainerSetDataPacket.class.getSimpleName(), "ClientboundContainerSetDataPacket");
        packetTable.put(ClientboundPlayerInfoUpdatePacket.class.getSimpleName(), "ClientboundPlayerInfoUpdatePacket");
        packetTable.put(ServerboundResourcePackPacket.class.getSimpleName(), "ServerboundResourcePackPacket");
        packetTable.put(ServerboundClientCommandPacket.class.getSimpleName(), "ServerboundClientCommandPacket");
        packetTable.put(ClientboundBossEventPacket.class.getSimpleName(), "ClientboundBossEventPacket");
        packetTable.put(ClientboundAddPlayerPacket.class.getSimpleName(), "ClientboundAddPlayerPacket");
        packetTable.put(ClientboundPlayerCombatEnterPacket.class.getSimpleName(), "ClientboundPlayerCombatEnterPacket");
        packetTable.put(ClientboundLoginPacket.class.getSimpleName(), "ClientboundLoginPacket");
        packetTable.put(ServerboundChatSessionUpdatePacket.class.getSimpleName(), "ServerboundChatSessionUpdatePacket");
        packetTable.put(ClientboundSetDefaultSpawnPositionPacket.class.getSimpleName(), "ClientboundSetDefaultSpawnPositionPacket");
        packetTable.put(ClientboundRecipePacket.class.getSimpleName(), "ClientboundRecipePacket");
        packetTable.put(ClientboundAddEntityPacket.class.getSimpleName(), "ClientboundAddEntityPacket");
        packetTable.put(ServerboundContainerButtonClickPacket.class.getSimpleName(), "ServerboundContainerButtonClickPacket");
        packetTable.put(ServerGamePacketListener.class.getSimpleName(), "ServerGamePacketListener");
        packetTable.put(ClientboundBlockEventPacket.class.getSimpleName(), "ClientboundBlockEventPacket");
        packetTable.put(ClientboundRemoveMobEffectPacket.class.getSimpleName(), "ClientboundRemoveMobEffectPacket");
        packetTable.put(ClientboundEntityEventPacket.class.getSimpleName(), "ClientboundEntityEventPacket");
        packetTable.put(ClientboundPlayerCombatEndPacket.class.getSimpleName(), "ClientboundPlayerCombatEndPacket");
        packetTable.put(ServerboundInteractPacket.class.getSimpleName(), "ServerboundInteractPacket");
        packetTable.put(ClientboundContainerSetSlotPacket.class.getSimpleName(), "ClientboundContainerSetSlotPacket");
        packetTable.put(ServerboundInteractPacket.class.getSimpleName(), "ServerboundInteractPacket");
        packetTable.put(ClientboundExplodePacket.class.getSimpleName(), "ClientboundExplodePacket");
        packetTable.put(ClientboundGameEventPacket.class.getSimpleName(), "ClientboundGameEventPacket");
        packetTable.put(ServerboundRecipeBookChangeSettingsPacket.class.getSimpleName(), "ServerboundRecipeBookChangeSettingsPacket");
        packetTable.put(ServerboundSetBeaconPacket.class.getSimpleName(), "ServerboundSetBeaconPacket");
        packetTable.put(ClientboundOpenSignEditorPacket.class.getSimpleName(), "ClientboundOpenSignEditorPacket");
        packetTable.put(ClientboundUpdateAttributesPacket.class.getSimpleName(), "ClientboundUpdateAttributesPacket");
        packetTable.put(ClientboundCustomChatCompletionsPacket.class.getSimpleName(), "ClientboundCustomChatCompletionsPacket");
        packetTable.put(ServerboundSetCommandMinecartPacket.class.getSimpleName(), "ServerboundSetCommandMinecartPacket");
        packetTable.put(ClientboundLevelChunkPacketData.class.getSimpleName(), "ClientboundLevelChunkPacketData");
        packetTable.put(ClientboundUpdateTagsPacket.class.getSimpleName(), "ClientboundUpdateTagsPacket");
        packetTable.put(ClientboundCustomChatCompletionsPacket.class.getSimpleName(), "ClientboundCustomChatCompletionsPacket");
        packetTable.put(ServerboundSignUpdatePacket.class.getSimpleName(), "ServerboundSignUpdatePacket");
        packetTable.put(ServerboundPaddleBoatPacket.class.getSimpleName(), "ServerboundPaddleBoatPacket");
        packetTable.put(ClientboundSetTimePacket.class.getSimpleName(), "ClientboundSetTimePacket");
        packetTable.put(ClientboundRemoveEntitiesPacket.class.getSimpleName(), "ClientboundRemoveEntitiesPacket");
        packetTable.put(ClientboundSetCarriedItemPacket.class.getSimpleName(), "ClientboundSetCarriedItemPacket");
        packetTable.put(ClientboundSetPlayerTeamPacket.class.getSimpleName(), "ClientboundSetPlayerTeamPacket");
        packetTable.put(ClientboundSetActionBarTextPacket.class.getSimpleName(), "ClientboundSetActionBarTextPacket");
        packetTable.put(ServerboundMovePlayerPacket.class.getSimpleName(), "ServerboundMovePlayerPacket");
        packetTable.put(ClientboundMerchantOffersPacket.class.getSimpleName(), "ClientboundMerchantOffersPacket");
        packetTable.put(ClientboundBossEventPacket.class.getSimpleName(), "ClientboundBossEventPacket");
        packetTable.put(ClientboundServerDataPacket.class.getSimpleName(), "ClientboundServerDataPacket");
        packetTable.put(ServerboundPlayerCommandPacket.class.getSimpleName(), "ServerboundPlayerCommandPacket");
        packetTable.put(ClientboundSetCameraPacket.class.getSimpleName(), "ClientboundSetCameraPacket");
        packetTable.put(ClientboundUpdateAttributesPacket.class.getSimpleName(), "ClientboundUpdateAttributesPacket");
        packetTable.put(ClientboundBlockDestructionPacket.class.getSimpleName(), "ClientboundBlockDestructionPacket");
        packetTable.put(ServerboundRecipeBookSeenRecipePacket.class.getSimpleName(), "ServerboundRecipeBookSeenRecipePacket");
        packetTable.put(ClientboundOpenScreenPacket.class.getSimpleName(), "ClientboundOpenScreenPacket");
        packetTable.put(ClientboundSetEntityMotionPacket.class.getSimpleName(), "ClientboundSetEntityMotionPacket");
        packetTable.put(ServerboundMovePlayerPacket.class.getSimpleName(), "ServerboundMovePlayerPacket");
        packetTable.put(ClientboundSetEquipmentPacket.class.getSimpleName(), "ClientboundSetEquipmentPacket");
        packetTable.put(ClientboundPlayerPositionPacket.class.getSimpleName(), "ClientboundPlayerPositionPacket");
        packetTable.put(ClientboundSoundEntityPacket.class.getSimpleName(), "ClientboundSoundEntityPacket");
        packetTable.put(ServerboundInteractPacket.class.getSimpleName(), "ServerboundInteractPacket");
        packetTable.put(ServerboundJigsawGeneratePacket.class.getSimpleName(), "ServerboundJigsawGeneratePacket");
        packetTable.put(ClientboundLightUpdatePacket.class.getSimpleName(), "ClientboundLightUpdatePacket");
        packetTable.put(ClientboundLightUpdatePacketData.class.getSimpleName(), "ClientboundLightUpdatePacketData");
        packetTable.put(ServerboundPlayerCommandPacket.class.getSimpleName(), "ServerboundPlayerCommandPacket");
        packetTable.put(ServerboundSetStructureBlockPacket.class.getSimpleName(), "ServerboundSetStructureBlockPacket");
        packetTable.put(ServerboundPlaceRecipePacket.class.getSimpleName(), "ServerboundPlaceRecipePacket");
        packetTable.put(ClientboundRespawnPacket.class.getSimpleName(), "ClientboundRespawnPacket");
        packetTable.put(ClientboundForgetLevelChunkPacket.class.getSimpleName(), "ClientboundForgetLevelChunkPacket");
        packetTable.put(ClientboundSetEntityLinkPacket.class.getSimpleName(), "ClientboundSetEntityLinkPacket");
        packetTable.put(ClientboundBlockEntityDataPacket.class.getSimpleName(), "ClientboundBlockEntityDataPacket");
        packetTable.put(ServerboundContainerClickPacket.class.getSimpleName(), "ServerboundContainerClickPacket");
        packetTable.put(ServerboundSelectTradePacket.class.getSimpleName(), "ServerboundSelectTradePacket");
        packetTable.put(ClientboundSetChunkCacheRadiusPacket.class.getSimpleName(), "ClientboundSetChunkCacheRadiusPacket");
        packetTable.put(ServerboundInteractPacket.class.getSimpleName(), "ServerboundInteractPacket");
        packetTable.put(ClientboundBossEventPacket.class.getSimpleName(), "ClientboundBossEventPacket");
        packetTable.put(ClientboundCommandsPacket.class.getSimpleName(), "ClientboundCommandsPacket");
        packetTable.put(ClientGamePacketListener.class.getSimpleName(), "ClientGamePacketListener");
        packetTable.put(ClientboundDeleteChatPacket.class.getSimpleName(), "ClientboundDeleteChatPacket");
        packetTable.put(ClientboundLevelChunkWithLightPacket.class.getSimpleName(), "ClientboundLevelChunkWithLightPacket");
        packetTable.put(ClientboundSetHealthPacket.class.getSimpleName(), "ClientboundSetHealthPacket");
        packetTable.put(ClientboundBossEventPacket.class.getSimpleName(), "ClientboundBossEventPacket");
        packetTable.put(ClientboundOpenBookPacket.class.getSimpleName(), "ClientboundOpenBookPacket");
        packetTable.put(ClientboundBossEventPacket.class.getSimpleName(), "ClientboundBossEventPacket");
        packetTable.put(ClientboundCommandsPacket.class.getSimpleName(), "ClientboundCommandsPacket");
        packetTable.put(ClientboundUpdateAdvancementsPacket.class.getSimpleName(), "ClientboundUpdateAdvancementsPacket");
        packetTable.put(ClientboundUpdateRecipesPacket.class.getSimpleName(), "ClientboundUpdateRecipesPacket");
        packetTable.put(ClientboundMoveVehiclePacket.class.getSimpleName(), "ClientboundMoveVehiclePacket");
        packetTable.put(ClientboundSetObjectivePacket.class.getSimpleName(), "ClientboundSetObjectivePacket");
        packetTable.put(ClientboundLevelParticlesPacket.class.getSimpleName(), "ClientboundLevelParticlesPacket");
        packetTable.put(ClientboundBossEventPacket.class.getSimpleName(), "ClientboundBossEventPacket");
        packetTable.put(ServerboundChangeDifficultyPacket.class.getSimpleName(), "ServerboundChangeDifficultyPacket");
        packetTable.put(ClientboundSetDisplayObjectivePacket.class.getSimpleName(), "ClientboundSetDisplayObjectivePacket");
        packetTable.put(ServerboundInteractPacket.class.getSimpleName(), "ServerboundInteractPacket");
        packetTable.put(ClientboundSetPlayerTeamPacket.class.getSimpleName(), "ClientboundSetPlayerTeamPacket");
        packetTable.put(ClientboundBossEventPacket.class.getSimpleName(), "ClientboundBossEventPacket");
        packetTable.put(ClientboundAddExperienceOrbPacket.class.getSimpleName(), "ClientboundAddExperienceOrbPacket");
        packetTable.put(ClientboundPlaceGhostRecipePacket.class.getSimpleName(), "ClientboundPlaceGhostRecipePacket");
        packetTable.put(ServerboundPlayerActionPacket.class.getSimpleName(), "ServerboundPlayerActionPacket");
        packetTable.put(ClientboundSetChunkCacheCenterPacket.class.getSimpleName(), "ClientboundSetChunkCacheCenterPacket");
        packetTable.put(ClientboundMoveEntityPacket.class.getSimpleName(), "ClientboundMoveEntityPacket");
        packetTable.put(ServerboundPlayerActionPacket.class.getSimpleName(), "ServerboundPlayerActionPacket");
        packetTable.put(ClientboundHorseScreenOpenPacket.class.getSimpleName(), "ClientboundHorseScreenOpenPacket");
        packetTable.put(ClientboundCommandsPacket.class.getSimpleName(), "ClientboundCommandsPacket");
        packetTable.put(ServerboundCommandSuggestionPacket.class.getSimpleName(), "ServerboundCommandSuggestionPacket");
        packetTable.put(ClientboundCommandsPacket.class.getSimpleName(), "ClientboundCommandsPacket");
        packetTable.put(ClientboundLevelEventPacket.class.getSimpleName(), "ClientboundLevelEventPacket");
        packetTable.put(ServerboundChatCommandPacket.class.getSimpleName(), "ServerboundChatCommandPacket");
        packetTable.put(ClientboundLevelChunkPacketData.class.getSimpleName(), "ClientboundLevelChunkPacketData");
        packetTable.put(ClientboundContainerSetContentPacket.class.getSimpleName(), "ClientboundContainerSetContentPacket");
        packetTable.put(ClientboundAnimatePacket.class.getSimpleName(), "ClientboundAnimatePacket");
        packetTable.put(ClientboundTeleportEntityPacket.class.getSimpleName(), "ClientboundTeleportEntityPacket");
        packetTable.put(ClientboundBlockChangedAckPacket.class.getSimpleName(), "ClientboundBlockChangedAckPacket");
        packetTable.put(ServerboundTeleportToEntityPacket.class.getSimpleName(), "ServerboundTeleportToEntityPacket");
        packetTable.put(ClientboundClearTitlesPacket.class.getSimpleName(), "ClientboundClearTitlesPacket");
        packetTable.put(ClientboundBossEventPacket.class.getSimpleName(), "ClientboundBossEventPacket");
        packetTable.put(ServerboundInteractPacket.class.getSimpleName(), "ServerboundInteractPacket");
        packetTable.put(ClientboundBossEventPacket.class.getSimpleName(), "ClientboundBossEventPacket");
        packetTable.put(ClientboundSetBorderSizePacket.class.getSimpleName(), "ClientboundSetBorderSizePacket");
        packetTable.put(ClientboundPlayerAbilitiesPacket.class.getSimpleName(), "ClientboundPlayerAbilitiesPacket");
        packetTable.put(ServerboundMovePlayerPacket.class.getSimpleName(), "ServerboundMovePlayerPacket");
        packetTable.put(ServerboundSetJigsawBlockPacket.class.getSimpleName(), "ServerboundSetJigsawBlockPacket");
        packetTable.put(ClientboundCommandsPacket.class.getSimpleName(), "ClientboundCommandsPacket");
        packetTable.put(ClientboundSetBorderWarningDistancePacket.class.getSimpleName(), "ClientboundSetBorderWarningDistancePacket");
        packetTable.put(ServerboundSeenAdvancementsPacket.class.getSimpleName(), "ServerboundSeenAdvancementsPacket");
        packetTable.put(ClientboundGameEventPacket.class.getSimpleName(), "ClientboundGameEventPacket");
        packetTable.put(VecDeltaCodec.class.getSimpleName(), "VecDeltaCodec");
        packetTable.put(ClientboundSetEntityDataPacket.class.getSimpleName(), "ClientboundSetEntityDataPacket");
        packetTable.put(ServerboundMovePlayerPacket.class.getSimpleName(), "ServerboundMovePlayerPacket");
        packetTable.put(ServerboundMoveVehiclePacket.class.getSimpleName(), "ServerboundMoveVehiclePacket");
        packetTable.put(ClientboundSetPassengersPacket.class.getSimpleName(), "ClientboundSetPassengersPacket");
        packetTable.put(DebugEntityNameGenerator.class.getSimpleName(), "DebugEntityNameGenerator");
        packetTable.put(ClientboundSetBorderLerpSizePacket.class.getSimpleName(), "ClientboundSetBorderLerpSizePacket");
        packetTable.put(ServerboundClientCommandPacket.class.getSimpleName(), "ServerboundClientCommandPacket");
        packetTable.put(ClientboundPlayerLookAtPacket.class.getSimpleName(), "ClientboundPlayerLookAtPacket");
        packetTable.put(ClientboundMoveEntityPacket.class.getSimpleName(), "ClientboundMoveEntityPacket");
        packetTable.put(ClientboundBlockUpdatePacket.class.getSimpleName(), "ClientboundBlockUpdatePacket");
        packetTable.put(ClientboundPlayerInfoUpdatePacket.class.getSimpleName(), "ClientboundPlayerInfoUpdatePacket");
        packetTable.put(ClientboundChangeDifficultyPacket.class.getSimpleName(), "ClientboundChangeDifficultyPacket");
        packetTable.put(ServerboundMovePlayerPacket.class.getSimpleName(), "ServerboundMovePlayerPacket");
        packetTable.put(ClientboundSetSimulationDistancePacket.class.getSimpleName(), "ClientboundSetSimulationDistancePacket");
        packetTable.put(ClientboundSoundPacket.class.getSimpleName(), "ClientboundSoundPacket");
        packetTable.put(ClientboundUpdateEnabledFeaturesPacket.class.getSimpleName(), "ClientboundUpdateEnabledFeaturesPacket");
        packetTable.put(ClientboundSetScorePacket.class.getSimpleName(), "ClientboundSetScorePacket");
        packetTable.put(ClientboundCommandSuggestionsPacket.class.getSimpleName(), "ClientboundCommandSuggestionsPacket");
        packetTable.put(ServerboundSeenAdvancementsPacket.class.getSimpleName(), "ServerboundSeenAdvancementsPacket");
        packetTable.put(ClientboundSetExperiencePacket.class.getSimpleName(), "ClientboundSetExperiencePacket");
        packetTable.put(ClientboundMoveEntityPacket.class.getSimpleName(), "ClientboundMoveEntityPacket");
        packetTable.put(ServerboundSetCreativeModeSlotPacket.class.getSimpleName(), "ServerboundSetCreativeModeSlotPacket");
        packetTable.put(ClientboundStopSoundPacket.class.getSimpleName(), "ClientboundStopSoundPacket");
        packetTable.put(ClientboundMapItemDataPacket.class.getSimpleName(), "ClientboundMapItemDataPacket");
        packetTable.put(ServerboundEntityTagQuery.class.getSimpleName(), "ServerboundEntityTagQuery");
        packetTable.put(ClientboundPlayerChatPacket.class.getSimpleName(), "ClientboundPlayerChatPacket");
        packetTable.put(ServerboundPongPacket.class.getSimpleName(), "ServerboundPongPacket");
        packetTable.put(ClientboundDisconnectPacket.class.getSimpleName(), "ClientboundDisconnectPacket");
        packetTable.put(ClientboundTakeItemEntityPacket.class.getSimpleName(), "ClientboundTakeItemEntityPacket");
        packetTable.put(ServerboundSwingPacket.class.getSimpleName(), "ServerboundSwingPacket");
        packetTable.put(ClientboundKeepAlivePacket.class.getSimpleName(), "ClientboundKeepAlivePacket");
        packetTable.put(ClientboundRotateHeadPacket.class.getSimpleName(), "ClientboundRotateHeadPacket");
        packetTable.put(ServerboundSetCarriedItemPacket.class.getSimpleName(), "ServerboundSetCarriedItemPacket");
        packetTable.put(ServerboundResourcePackPacket.class.getSimpleName(), "ServerboundResourcePackPacket");
        packetTable.put(ClientboundSetBorderWarningDelayPacket.class.getSimpleName(), "ClientboundSetBorderWarningDelayPacket");
        packetTable.put(ServerboundPickItemPacket.class.getSimpleName(), "ServerboundPickItemPacket");
        packetTable.put(ServerboundRenameItemPacket.class.getSimpleName(), "ServerboundRenameItemPacket");
        packetTable.put(ServerboundKeepAlivePacket.class.getSimpleName(), "ServerboundKeepAlivePacket");
        packetTable.put(ClientboundPlayerCombatKillPacket.class.getSimpleName(), "ClientboundPlayerCombatKillPacket");
        packetTable.put(ClientboundSetSubtitleTextPacket.class.getSimpleName(), "ClientboundSetSubtitleTextPacket");
        packetTable.put(ClientboundMoveEntityPacket.class.getSimpleName(), "ClientboundMoveEntityPacket");
        packetTable.put(ServerboundPlayerInputPacket.class.getSimpleName(), "ServerboundPlayerInputPacket");
        packetTable.put(ClientboundTabListPacket.class.getSimpleName(), "ClientboundTabListPacket");
        packetTable.put(ClientboundRecipePacket.class.getSimpleName(), "ClientboundRecipePacket");
        packetTable.put(ClientboundPlayerInfoRemovePacket.class.getSimpleName(), "ClientboundPlayerInfoRemovePacket");
        packetTable.put(ClientboundPlayerInfoUpdatePacket.class.getSimpleName(), "ClientboundPlayerInfoUpdatePacket");
        packetTable.put(ClientboundPlayerInfoUpdatePacket.class.getSimpleName(), "ClientboundPlayerInfoUpdatePacket");
        packetTable.put(ServerboundContainerClosePacket.class.getSimpleName(), "ServerboundContainerClosePacket");
        packetTable.put(ServerboundPlayerAbilitiesPacket.class.getSimpleName(), "ServerboundPlayerAbilitiesPacket");
        packetTable.put(ClientboundPlayerInfoUpdatePacket.class.getSimpleName(), "ClientboundPlayerInfoUpdatePacket");
        packetTable.put(ServerboundAcceptTeleportationPacket.class.getSimpleName(), "ServerboundAcceptTeleportationPacket");
        packetTable.put(ServerboundLockDifficultyPacket.class.getSimpleName(), "ServerboundLockDifficultyPacket");
        packetTable.put(ServerboundBlockEntityTagQuery.class.getSimpleName(), "ServerboundBlockEntityTagQuery");
        packetTable.put(ClientboundPingPacket.class.getSimpleName(), "ClientboundPingPacket");
        packetTable.put(ClientboundDisguisedChatPacket.class.getSimpleName(), "ClientboundDisguisedChatPacket");
        packetTable.put(ClientboundCustomPayloadPacket.class.getSimpleName(), "ClientboundCustomPayloadPacket");
        packetTable.put(ServerboundCustomPayloadPacket.class.getSimpleName(), "ServerboundCustomPayloadPacket");
        packetTable.put(ClientboundSelectAdvancementsTabPacket.class.getSimpleName(), "ClientboundSelectAdvancementsTabPacket");
    }
}
