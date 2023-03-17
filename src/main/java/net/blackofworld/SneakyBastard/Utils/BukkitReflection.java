package net.blackofworld.SneakyBastard.Utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.IOException;

import static net.blackofworld.SneakyBastard.Utils.Reflection.setFinalStatic;

public class BukkitReflection {
    @SuppressWarnings({"UnusedReturnValue", "deprecation"})
    public static boolean changeCommandBlockStatus(boolean status) throws Exception {
        String fieldName = getCommandBlockVariableName();
        if (fieldName == null) return false;
        DedicatedServerProperties properties = ((DedicatedServer) DedicatedServer.getServer()).getProperties();
        setFinalStatic(properties, fieldName, status);
        return true;
    }


    private static @Nullable String getCommandBlockVariableName() {
        Class<?> clazz = DedicatedServerProperties.class;
        String classAsPath = clazz.getName().replace('.', '/') + ".class";
        try (java.io.InputStream classStream = clazz.getClassLoader().getResourceAsStream(classAsPath)) {
            assert classStream != null;
            ClassParser classParser = new ClassParser(classStream, classAsPath);
            JavaClass javaClass = classParser.parse();
            for (Method method : javaClass.getMethods()) {
                if (!method.getName().equals("<init>")) continue;
                Code code = method.getCode();
                InstructionList il = new InstructionList(code.getCode());
                boolean isCommandBlock = false;
                ConstantPoolGen cpg = new ConstantPoolGen(javaClass.getConstantPool());
                for (Instruction in : il.getInstructions()) {
                    if (isCommandBlock && in instanceof PUTFIELD pf) {
                        return pf.getFieldName(cpg);
                    }
                    if (in instanceof LDC ldc) {
                        if (ldc.getType(cpg) != Type.STRING) continue;
                        String str = (String) ldc.getValue(cpg);
                        if (str.equals("enable-command-block")) {
                            isCommandBlock = true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static DedicatedServer getMinecraftServer() {
        var m = Reflection.getMethodCached("{obc}.CraftServer", "getServer");
        return invoke(m, Bukkit.getServer());
    }

    public static ItemStack getItemStack(org.bukkit.inventory.ItemStack i) {
        var m = Reflection.getMethodCached("{obc}.inventory.CraftItemStack", "getHandle");
        return invoke(m, i);
    }

    public static String getItemNbt(org.bukkit.inventory.ItemStack i) {
        var m = Reflection.getMethodCached("{obc}.inventory.CraftItemStack", "asNMSCopy", org.bukkit.inventory.ItemStack.class);
        ItemStack is = invoke(m, null, i);
        CompoundTag compound = new CompoundTag();
        assert is != null;
        is.save(compound);
        return compound.getAsString();

    }

    public static ServerLevel getWorldLevel(World w) {
        var m = Reflection.getMethodCached("{obc}.CraftWorld", "getHandle");
        return invoke(m, w);
    }

    public static ServerPlayer getServerPlayer(Player p) {
        var m = Reflection.getMethodCached("{obc}.entity.CraftPlayer", "getHandle");
        return invoke(m, p);
    }

    @SuppressWarnings("unchecked")
    private static <T> @Nullable T invoke(java.lang.reflect.Method m, Object instance, Object... args) {
        try {
            return (T) m.invoke(instance, args);
        } catch (Exception e) {
            return null;
        }
    }
}